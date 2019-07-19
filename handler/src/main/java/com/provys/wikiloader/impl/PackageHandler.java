package com.provys.wikiloader.impl;

import com.provys.dokuwiki.PageIdParser;
import com.provys.provyswiki.ProvysWikiClient;
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
                                                     LinkResolver linkResolver) {
        return linkResolver.getPackageNamespace(pkg)
                .map(ns -> new PackageHandler(pkg, ns, elementHandlerFactory, linkResolver));
    }

    @Nonnull
    private final Package pkg;
    @Nonnull
    private final String namespace;
    @Nonnull
    private final String name;
    /** Factory used to retrieve handlers for elements */
    @Nonnull
    private final ElementHandlerFactory elementHandlerFactory;
    /** Resolver used to resolve package and element links */
    @Nonnull
    private final LinkResolver linkResolver;

    /**
     * Create new package reader usable to import content of given package to wiki.
     *
     * @param pkg is package to be imported
     * @param namespace is namespace where package should be placed
     */
    private PackageHandler(Package pkg, String namespace, ElementHandlerFactory elementHandlerFactory,
                   LinkResolver linkResolver) {
        this.pkg = Objects.requireNonNull(pkg);
        this.namespace = Objects.requireNonNull(namespace);
        this.name = new PageIdParser().getName(namespace);
        this.elementHandlerFactory = Objects.requireNonNull(elementHandlerFactory);
        this.linkResolver = Objects.requireNonNull(linkResolver);
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
            result.add(new DiagramHandler(diagram));
        }
        diagrams.destroy();
        return result;
    }

    private List<PackageHandler> getSubPackages() {
        var subPackages = pkg.GetPackages();
        var result = new ArrayList<PackageHandler>(subPackages.GetCount());
        for (Package subPackage : subPackages) {
            PackageHandler.ofPackage(subPackage, elementHandlerFactory, linkResolver).ifPresentOrElse(result::add,
                    () -> LOG.info("Package " + subPackage.GetName() + " skipped, alias is empty"));
        }
        subPackages.destroy();
        return result;
    }

    private List<ElementHandler> getElements() {
        var elements = pkg.GetElements();
        var result = new ArrayList<ElementHandler>(elements.GetCount());
        for (var element : elements) {
            elementHandlerFactory.getElementHandler(element, linkResolver).ifPresent(result::add);
        }
        elements.destroy();
        return result;
    }

    private void appendElement(ElementHandler element, ProvysWikiClient wikiClient, StringBuilder startBuilder,
                               List<String> contentBuilder) {
        startBuilder.append("  * [[").append(element.getName()).append("]]\n");
        contentBuilder.add(element.getName());
        element.sync(wikiClient, linkResolver);
    }

    void sync(ProvysWikiClient wikiClient) {
        LOG.info("Synchronize package {} to namespace {}", pkg.GetName(), namespace);
        wikiClient.syncSidebar(namespace);
        StringBuilder startBuilder = new StringBuilder();
        List<String> contentBuilder = new ArrayList<>(10);
        startBuilder.append("===== ").append(pkg.GetName()).append(" =====\n");
        // handle diagrams
        var diagrams = getDiagrams();
        if (!diagrams.isEmpty()) {
            startBuilder.append("\n==== Diagrams ====\n");
            for (var diagram : diagrams) {
                appendElement(diagram, wikiClient, startBuilder, contentBuilder);
            }
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
                appendElement(element, wikiClient, startBuilder, contentBuilder);
            }
        }
        // synchronize to wiki
        wikiClient.putPage(namespace + ":start", startBuilder.toString());
        wikiClient.syncContent(namespace, contentBuilder);
        wikiClient.deleteUnusedNamespaces(namespace, contentBuilder);
        wikiClient.deleteUnusedPages(namespace, contentBuilder);
        // synchronize subpackages
        for (var subPackage : subPackages) {
            subPackage.sync(wikiClient);
        }
    }
}
