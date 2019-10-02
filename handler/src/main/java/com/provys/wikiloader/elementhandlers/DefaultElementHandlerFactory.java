package com.provys.wikiloader.elementhandlers;

import com.provys.catalogue.api.CatalogueRepository;
import com.provys.wikiloader.impl.ElementHandler;
import com.provys.wikiloader.wikimap.WikiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Element;

import javax.annotation.Nonnull;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
class DefaultElementHandlerFactory implements com.provys.wikiloader.impl.ElementHandlerFactory {

    private static final Logger LOG = LogManager.getLogger(DefaultElementHandlerFactory.class);

    @Nonnull
    private final CatalogueRepository catalogueRepository;

    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    DefaultElementHandlerFactory(CatalogueRepository catalogueRepository) {
        this.catalogueRepository = Objects.requireNonNull(catalogueRepository);
    }

    @Override
    @Nonnull
    public Optional<ElementHandler> getElementHandler(Element element, WikiMap wikiMap) {
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
        switch (element.GetType()) {
            case "BusinessObject":
                return Optional.of(new BusinessObjectHandler(element, info, this, wikiMap));
            case "DataObject":
                return Optional.of(new DataObjectHandler(element, info, this, wikiMap,
                        catalogueRepository));
            default:
                return Optional.of(new DefaultElementHandler(element, info, this, wikiMap));
        }
    }
}
