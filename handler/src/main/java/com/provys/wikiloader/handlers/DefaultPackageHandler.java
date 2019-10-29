package com.provys.wikiloader.handlers;

import com.provys.dokuwiki.PageIdParser;
import com.provys.wikiloader.earepository.impl.DefaultExporter;
import com.provys.wikiloader.earepository.impl.ExporterProducer;
import org.sparx.Package;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

class DefaultPackageHandler extends HandlerBase<DefaultPackageHandler> {

    @Nonnull
    private final Package pkg;
    @Nonnull
    private final String namespace;
    @Nonnull
    private final String name;
    private final boolean underParent;
    /** Factory used to retrieve handlers for elements */
    @Nonnull
    private final HandlerFactory handlerFactory;
    /** Resolver used to resolve package and element links */
    @Nonnull
    private final WikiMap wikiMap;

    DefaultPackageHandler(ExporterProducer<DefaultPackageHandler> exporterProducer, Package pkg, WikiPackage wikiPackage,
                          HandlerFactory handlerFactory, WikiMap wikiMap) {
        super(exporterProducer);
        this.pkg = Objects.requireNonNull(pkg);
        this.namespace = wikiPackage.getNamespace().orElseThrow();
        this.name = new PageIdParser().getName(namespace);
        this.underParent = wikiPackage.isUnderParent();
        this.handlerFactory = Objects.requireNonNull(handlerFactory);
        this.wikiMap = Objects.requireNonNull(wikiMap);
    }

    DefaultPackageHandler(Package pkg, WikiPackage wikiPackage, HandlerFactory handlerFactory, WikiMap wikiMap) {
        this(DefaultExporter::new, pkg, wikiPackage, handlerFactory, wikiMap);
    }

    @Nonnull
    Package getPkg() {
        return pkg;
    }

    /**
     * @return name of package (lowercase of alias, used in wiki namespace)
     */
    @Nonnull
    String getName() {
        return name;
    }

    boolean isUnderParent() {
        return underParent;
    }

    @Nonnull
    HandlerFactory getHandlerFactory() {
        return handlerFactory;
    }

    @Nonnull
    WikiMap getWikiMap() {
        return wikiMap;
    }

    @Override
    @Nonnull
    public Collection<Handler> getDiagrams() {
        var diagrams = pkg.GetDiagrams();
        try {
            return DiagramHandler.ofCollection(diagrams, wikiMap);
        } finally {
            diagrams.destroy();
        }
    }

    @Override
    @Nonnull
    public Collection<Handler> getSubPackages() {
        var subPackages = pkg.GetPackages();
        try {
            return handlerFactory.getPackageHandlers(subPackages, wikiMap);
        } finally {
            subPackages.destroy();
        }
    }

    @Override
    @Nonnull
    public Collection<Handler> getElements() {
        var elements = pkg.GetElements();
        try {
            return handlerFactory.getElementHandlers(elements, wikiMap);
        } finally {
            elements.destroy();
        }
    }

    @Override
    @Nonnull
    public String getEaName() {
        return pkg.GetName();
    }

    @Nonnull
    @Override
    public String getEaAlias() {
        return pkg.GetAlias();
    }

    @Nonnull
    @Override
    public String getEaNotes() {
        return pkg.GetNotes();
    }

    @Override
    @Nonnull
    public Optional<String> getNamespace() {
        return Optional.of(namespace);
    }

    @Override
    @Nonnull
    public String getId() {
        return namespace + ":start";
    }

    @Nonnull
    @Override
    public String getRelLink() {
        if (underParent) {
            return "." + name + ":";
        } else {
            return ":" + namespace + ":";
        }
    }

    @Override
    public void appendPages(Collection<String> pages) {
        pages.add(getRelLink());
    }

    @Nonnull
    @Override
    DefaultPackageHandler self() {
        return this;
    }
}
