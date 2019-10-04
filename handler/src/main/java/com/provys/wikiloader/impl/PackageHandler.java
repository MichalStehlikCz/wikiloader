package com.provys.wikiloader.impl;

import com.provys.common.exception.InternalException;
import com.provys.dokuwiki.PageIdParser;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.elementhandlers.DiagramHandler;
import com.provys.wikiloader.wikimap.WikiMap;
import com.provys.wikiloader.wikimap.WikiPackage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Package;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Class implements synchronisation of package to designated directory in wiki
 */
class PackageHandler {

    private static final Logger LOG = LogManager.getLogger(PackageHandler.class);

    static Optional<PackageHandler> ofPackage(Package pkg, ElementHandlerFactory elementHandlerFactory,
                                                     WikiMap wikiMap) {
        var wikiPackage = wikiMap.getWikiPackage(pkg);
        if (!wikiPackage.isExported()) {
            return Optional.empty();
        }
        return Optional.of(new PackageHandler(pkg, wikiPackage, elementHandlerFactory, wikiMap));
    }

    @Nonnull
    private final Package pkg;
    @Nonnull
    private final String namespace;
    @Nonnull
    private final String name;
    private final boolean syncContent;
    /** Factory used to retrieve handlers for elements */
    @Nonnull
    private final ElementHandlerFactory elementHandlerFactory;
    /** Resolver used to resolve package and element links */
    @Nonnull
    private final WikiMap wikiMap;

    /**
     * Create new package reader usable to import content of given package to wiki.
     *
     * @param pkg is package to be imported
     */
    private PackageHandler(Package pkg, WikiPackage wikiPackage, ElementHandlerFactory elementHandlerFactory,
                           WikiMap wikiMap) {
        this.pkg = Objects.requireNonNull(pkg);
        this.namespace = wikiPackage.getNamespace().orElseThrow(() -> new InternalException(LOG,
                "Cannot export package " + pkg.GetName() + " without namespace (parent not exported or missing alias)"));
        this.name = new PageIdParser().getName(namespace);
        this.syncContent = wikiPackage.isSync();
        this.elementHandlerFactory = Objects.requireNonNull(elementHandlerFactory);
        this.wikiMap = Objects.requireNonNull(wikiMap);
    }

    /**
     * @return namespace of package (lowercase, as used in wiki)
     */
    @Nonnull
    private String getNamespace() {
        return namespace;
    }

    /**
     * @return name of package (lowercase of alias, used in wiki namespace)
     */
    @Nonnull
    private String getName() {
        return name;
    }

    private List<DiagramHandler> getDiagrams() {
        var diagrams = pkg.GetDiagrams();
        var result = new ArrayList<DiagramHandler>(diagrams.GetCount());
        for (var diagram : diagrams) {
            result.add(new DiagramHandler(diagram, wikiMap));
        }
        diagrams.destroy();
        return result;
    }

    private List<PackageHandler> getSubPackages() {
        var subPackages = pkg.GetPackages();
        var result = new ArrayList<PackageHandler>(subPackages.GetCount());
        for (Package subPackage : subPackages) {
            PackageHandler.ofPackage(subPackage, elementHandlerFactory, wikiMap).ifPresentOrElse(result::add,
                    () -> LOG.info("Package {} skipped, alias is empty", subPackage::GetName));
        }
        subPackages.destroy();
        return result;
    }

    private List<ElementHandler> getElements() {
        var elements = pkg.GetElements();
        var result = new ArrayList<ElementHandler>(elements.GetCount());
        for (var element : elements) {
            elementHandlerFactory.getElementHandler(element, wikiMap).ifPresent(result::add);
        }
        elements.destroy();
        return result;
    }

    private void appendElement(ElementHandler element, ProvysWikiClient wikiClient, StringBuilder startBuilder,
                               List<String> contentBuilder, boolean sync) {
        startBuilder.append("  * [[").append(element.getRelLink()).append("]]\n");
        contentBuilder.add(element.getRelLink());
        if (sync) {
            element.sync(wikiClient);
        }
    }

    private void inlineElement(ElementHandler element, ProvysWikiClient wikiClient, StringBuilder startBuilder,
                               List<String> contentBuilder) {
        startBuilder.append("{{page>").append(element.getRelLink()).append("&noheader}}\n");
        contentBuilder.add(element.getRelLink());
        element.sync(wikiClient);
    }

    void sync(ProvysWikiClient wikiClient, boolean recursive) {
        LOG.info("Synchronize package {} to namespace {}", pkg::GetName, () -> namespace);
        wikiClient.syncSidebar(namespace);
        StringBuilder startBuilder = new StringBuilder();
        List<String> contentBuilder = new ArrayList<>(10);
        startBuilder.append("===== ").append(pkg.GetName()).append(" =====\n");
        // handle diagrams
        var diagrams = getDiagrams();
        for (var diagram : diagrams) {
            inlineElement(diagram, wikiClient, startBuilder, contentBuilder);
        }
        // handle sub-packages
        var subPackages = getSubPackages();
        if (!subPackages.isEmpty()) {
            startBuilder.append("\n==== Areas ====\n");
            if (!contentBuilder.isEmpty()) {
                contentBuilder.add("\\\\");
            }
            for (var subPackage : subPackages) {
                startBuilder.append("  * [[.").append(subPackage.getName()).append(":]]\n");
                contentBuilder.add("." + subPackage.getName() + ":");
            }
        }
        // handle elements
        var elements = getElements();
        if (!elements.isEmpty()) {
            startBuilder.append("\n==== Objects ====\n");
            if (!contentBuilder.isEmpty()) {
                contentBuilder.add("\\\\");
            }
            for (var element : elements) {
                appendElement(element, wikiClient, startBuilder, contentBuilder, recursive);
            }
        }
        // synchronize to wiki
        wikiClient.putPage(namespace + ":start", startBuilder.toString());
        if (syncContent) {
            wikiClient.syncContent(namespace, contentBuilder);
            wikiClient.deleteUnusedNamespaces(namespace, contentBuilder);
            wikiClient.deleteUnusedPages(namespace, contentBuilder);
        }
        // synchronize subpackages
        if (recursive) {
            for (var subPackage : subPackages) {
                subPackage.sync(wikiClient, true);
            }
        }
    }
}
