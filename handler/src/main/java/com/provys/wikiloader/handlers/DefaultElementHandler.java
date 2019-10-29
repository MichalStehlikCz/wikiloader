package com.provys.wikiloader.handlers;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.impl.DefaultExporter;
import com.provys.wikiloader.earepository.impl.ExporterProducer;
import org.sparx.Element;

import javax.annotation.Nonnull;

class DefaultElementHandler extends ElementHandlerBase<DefaultElementHandler> {

    DefaultElementHandler(ExporterProducer<DefaultElementHandler> exporterProducer, Element element, WikiElement info,
                          HandlerFactory handlerFactory, WikiMap wikiMap) {
        super(exporterProducer, element, info, handlerFactory, wikiMap);
        if (element.GetAlias().isEmpty()) {
            throw new InternalException(LOG, "Cannot process element with empty alias");
        }
    }

    DefaultElementHandler(Element element, WikiElement info, HandlerFactory handlerFactory, WikiMap wikiMap) {
        this(DefaultExporter::new, element, info, handlerFactory, wikiMap);
    }

    @Nonnull
    @Override
    DefaultElementHandler self() {
        return this;
    }
}
