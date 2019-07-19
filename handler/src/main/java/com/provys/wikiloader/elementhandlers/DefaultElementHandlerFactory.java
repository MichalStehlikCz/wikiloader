package com.provys.wikiloader.elementhandlers;

import com.provys.catalogue.api.CatalogueRepository;
import com.provys.wikiloader.impl.ElementHandler;
import com.provys.wikiloader.impl.LinkResolver;
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
    @SuppressWarnings("squid:S3655") // Sonar does not support IsEmpty for Optional yet...
    public Optional<ElementHandler> getElementHandler(Element element, LinkResolver linkResolver) {
        Optional<String> id = linkResolver.getElementId(element);
        if (id.isEmpty()) {
            LOG.info("Skip {} {} with empty alias", element::GetType, element::GetName);
            return Optional.empty();
        }
        switch (element.GetType()) {
            case "Boundary":
                // Boundary in diagram links to package, but is not exported on its own
                return Optional.empty();
            case "BusinessObject":
                return Optional.of(new BusinessObjectHandler(element, id.get()));
            case "DataObject":
                return Optional.of(new DataObjectHandler(element, id.get(), catalogueRepository));
            default:
                return Optional.of(new DefaultElementHandler(element, id.get()));
        }
    }
}
