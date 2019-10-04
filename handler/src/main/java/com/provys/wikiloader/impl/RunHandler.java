package com.provys.wikiloader.impl;

import com.provys.common.exception.InternalException;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.wikimap.WikiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Collection;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.Repository;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Class encapsulates execution of synchronisation of Enterprise Architect model to wiki
 */
class RunHandler {

    private static final Logger LOG = LogManager.getLogger(RunHandler.class);

    /** Name of model package */
    private static final String MODEL_NAME = "Product Model";
    /** Name of root namespace in wiki */
    private static final String ROOT_NAMESPACE = "eamodel";

    private final Repository eaRepository;
    private final ProvysWikiClient wikiClient;
    private final ElementHandlerFactory elementHandlerFactory;
    @Nullable
    private final String path;
    private final boolean recursive;
    private Package rootPackage;
    private Element rootElement;

    RunHandler(Repository eaRepository, ProvysWikiClient wikiClient, ElementHandlerFactory elementHandlerFactory,
               @Nullable String path, boolean recursive) {
        this.eaRepository = Objects.requireNonNull(eaRepository);
        this.wikiClient = Objects.requireNonNull(wikiClient);
        this.elementHandlerFactory = Objects.requireNonNull(elementHandlerFactory);
        this.path = path;
        this.recursive = recursive;
    }

    /**
     * Get child package with specified alias.
     *
     * @param parent is parent package
     * @param alias  is required alias
     * @return package if one exists, empty optional if no such package is found
     */
    private Optional<Package> getChildPackageByAlias(Package parent, String alias) {
        Collection<Package> children = parent.GetPackages();
        try {
            for (var child : children) {
                if (child.GetAlias().equalsIgnoreCase(alias)) {
                    return Optional.of(child);
                }
                child.destroy();
            }
        } finally {
            children.destroy();
        }
        return Optional.empty();
    }

    /**
     * Get element with given alias from collection
     */
    private Optional<Element> getElementByAlias(Collection<Element> elements, String alias) {
        for (var element : elements) {
            if (element.GetAlias().equalsIgnoreCase(alias)) {
                return Optional.of(element);
            }
            element.destroy();
        }
        return Optional.empty();
    }

    /**
     * Get element in package with specified alias.
     *
     * @param pkg   is package
     * @param alias is required alias
     * @return element if one exists, empty optional if no such element is found
     */
    private Optional<Element> getElementByAlias(Package pkg, String alias) {
        Collection<Element> elements = pkg.GetElements();
        try {
            return getElementByAlias(elements, alias);
        } finally {
            elements.destroy();
        }
    }

    /**
     * Get child element with specified alias.
     *
     * @param parent is parent element
     * @param alias  is required alias
     * @return element if one exists, empty optional if no such element is found
     */
    private Optional<Element> getChildElementByAlias(Element parent, String alias) {
        Collection<Element> elements = parent.GetElements();
        try {
            return getElementByAlias(elements, alias);
        } finally {
            elements.destroy();
        }
    }

    private void evalRoot() {
        if (rootPackage == null) {
            throw new IllegalStateException("Function cannot be called before root package is set");
        }
        if (path != null) {
            // we will parse path and try to find appropriate package or element
            for (var part : path.split(":")) {
                if (rootPackage != null) {
                    // first try to find subpackage
                    var pkg = getChildPackageByAlias(rootPackage, part);
                    if (pkg.isPresent()) {
                        rootPackage.destroy();
                        rootPackage = pkg.get();
                    } else {
                        // if not successful, check if there is appropriate element
                        var element = getElementByAlias(rootPackage, part);
                        rootPackage.destroy();
                        rootPackage = null;
                        rootElement = element
                                .orElseThrow(
                                        () -> new InternalException(LOG, "Package or element " + part + " not found"));
                    }
                } else {
                    var element = getChildElementByAlias(rootElement, part);
                    rootElement.destroy();
                    rootElement = element
                            .orElseThrow(() -> new InternalException(LOG, "Element " + part + " not found"));
                }
            }
        }
    }

    void run() {
        Collection<Package> models = eaRepository.GetModels();
        try {
            rootPackage = models.GetByName(MODEL_NAME);
            var wikiMap = new WikiMap(eaRepository, rootPackage, ROOT_NAMESPACE, true);
            rootElement = null;
            evalRoot();
            if (rootElement != null) {
                elementHandlerFactory.getElementHandler(rootElement, wikiMap).ifPresentOrElse(
                        element -> element.sync(wikiClient, recursive),
                        () -> {
                            throw new InternalException(LOG, "Root element not evaluated for export to wiki");
                        });
                rootElement.destroy();
                rootElement = null;
            } else if (rootPackage != null) {
                PackageHandler.ofPackage(rootPackage, elementHandlerFactory, wikiMap).ifPresentOrElse(
                        pkg -> pkg.sync(wikiClient, recursive),
                        () -> {
                            throw new InternalException(LOG, "Root package not evaluated for export to wiki");
                        });
                rootPackage.destroy();
                rootPackage = null;
            } else {
                throw new InternalException(LOG, "Root item not found");
            }
        } finally {
            models.destroy();
        }
    }
}
