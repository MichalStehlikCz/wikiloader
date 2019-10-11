package com.provys.wikiloader.handlers;

import com.provys.catalogue.api.CatalogueRepository;
import com.provys.wikiloader.impl.Handler;
import com.provys.wikiloader.impl.HandlerFactory;
import com.provys.wikiloader.wikimap.WikiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Element;
import org.sparx.Package;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
class DefaultHandlerFactory implements HandlerFactory {

    private static final Logger LOG = LogManager.getLogger(DefaultHandlerFactory.class);

    @Nonnull
    private final CatalogueRepository catalogueRepository;

    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    DefaultHandlerFactory(CatalogueRepository catalogueRepository) {
        this.catalogueRepository = Objects.requireNonNull(catalogueRepository);
    }

    @Override
    @Nonnull
    public Optional<Handler> getElementHandler(Element element, WikiMap wikiMap) {
        if (element.GetType().equals("Boundary") || element.GetType().equals("UMLDiagram")) {
            // some types of elements are only used as areas in diagram and have are not exported themselves
            LOG.debug("Skip element {} of type {}", element::GetName, element::GetType);
            return Optional.empty();
        }
        var info = wikiMap.getWikiElement(element);
        if (info.getTopicId().isEmpty()) {
            if (element.GetName().equals("START") || element.GetName().equals("END")) {
                // START and END process steps are used in diagram, but are not exported
                LOG.debug("Skip {} {} with empty alias", element::GetType, element::GetName);
            } else {
                LOG.info("Skip {} {} with empty alias", element::GetType, element::GetName);
            }
            return Optional.empty();
        }
        switch (element.GetStereotype()) {
            case "ArchiMate_DataObject":
                return Optional.of(new DefaultElementHandler((handler, wikiClient) -> new DataObjectExporter(handler,
                        wikiClient, catalogueRepository), element, info, this, wikiMap));
            case "ArchiMate_Product":
                return Optional.of(new DefaultElementHandler(ProductPackageExporter::new, element, info,
                        this, wikiMap));
            default:
                return Optional.of(new DefaultElementHandler(element, info, this, wikiMap));
        }
    }

    @Nonnull
    @Override
    public Collection<Handler> getElementHandlers(org.sparx.Collection<Element> elements, WikiMap wikiMap) {
        var result = new ArrayList<Handler>(elements.GetCount());
        for (var element : elements) {
            getElementHandler(element, wikiMap).ifPresent(result::add);
        }
        return result;
    }

    @Nonnull
    @Override
    public Optional<Handler> getPackageHandler(Package pkg, WikiMap wikiMap) {
        var wikiPackage = wikiMap.getWikiPackage(pkg);
        if (!wikiPackage.isExported()) {
            LOG.info("Package {} skipped, alias is empty", pkg::GetName);
            return Optional.empty();
        }
        switch (pkg.GetStereotypeEx()) {
            case "PackagingModel":
            case "PackageGroup":
                return Optional.of(new DefaultPackageHandler(PackageGroupExporter::new, pkg, wikiPackage,
                        this, wikiMap));
            default:
                return Optional.of(new DefaultPackageHandler(pkg, wikiPackage, this, wikiMap));
        }
    }

    @Nonnull
    @Override
    public Collection<Handler> getPackageHandlers(org.sparx.Collection<Package> pkgs, WikiMap wikiMap) {
        var result = new ArrayList<Handler>(pkgs.GetCount());
        for (Package pkg : pkgs) {
            getPackageHandler(pkg, wikiMap).ifPresent(result::add);
        }
        return result;
    }
}
