package com.provys.wikiloader.handlers;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.impl.*;
import com.provys.wikiloader.wikimap.WikiElement;
import com.provys.wikiloader.wikimap.WikiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Element;

import javax.annotation.Nonnull;
import java.util.*;

class DefaultElementHandler extends HandlerBase<DefaultElementHandler> {

    private static final Logger LOG = LogManager.getLogger(DefaultElementHandler.class);

    @Nonnull
    private final Element element;
    @Nonnull
    private final WikiElement info;
    @Nonnull
    private final HandlerFactory handlerFactory;
    @Nonnull
    private final WikiMap wikiMap;


    DefaultElementHandler(ExporterProducer<DefaultElementHandler> exporterProducer, Element element, WikiElement info,
                          HandlerFactory handlerFactory, WikiMap wikiMap) {
        super(exporterProducer);
        this.element = Objects.requireNonNull(element);
        if (element.GetAlias().isEmpty()) {
            throw new InternalException(LOG, "Cannot process element with empty alias");
        }
        this.info = Objects.requireNonNull(info);
        this.handlerFactory = Objects.requireNonNull(handlerFactory);
        this.wikiMap = wikiMap;
    }

    DefaultElementHandler(Element element, WikiElement info, HandlerFactory handlerFactory, WikiMap wikiMap) {
        this(DefaultExporter::new, element, info, handlerFactory, wikiMap);
    }

    /**
     * @return element this handler is used for
     */
    @Nonnull
    Element getElement() {
        return element;
    }

    /**
     * @return topic id
     */
    @Nonnull
    public String getId() {
        return info.getTopicId().orElseThrow();
    }

    /**
     * @return name (lower-case of alias) used as topic name
     */
    @Nonnull
    @Override
    public String getRelLink() {
        return info.getRelLink().orElseThrow();
    }

    @Override
    public void appendPages(Collection<String> pages) {

    }

    /**
     * @return namespace for element mapped to namespace
     */
    @Nonnull
    public Optional<String> getNamespace() {
        return info.getNamespace();
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
    public String getEaName() {
        return element.GetName();
    }

    @Override
    @Nonnull
    public String getEaAlias() {
        return element.GetAlias();
    }

    @Override
    @Nonnull
    public String getEaNotes() {
        return element.GetNotes();
    }

    @Nonnull
    @Override
    public Collection<Handler> getDiagrams() {
        var diagrams = element.GetDiagrams();
        try {
            return DiagramHandler.ofCollection(diagrams, wikiMap);
        } finally {
            diagrams.destroy();
        }
    }

    @Nonnull
    @Override
    public Collection<Handler> getSubPackages() {
        return Collections.emptyList();
    }

    @Override
    @Nonnull
    public List<Handler> getElements() {
        var subElements = element.GetElements();
        var result = new ArrayList<Handler>(subElements.GetCount());
        for (var subElement : subElements) {
            handlerFactory.getElementHandler(subElement, wikiMap).ifPresent(result::add);
        }
        subElements.destroy();
        return result;
    }

    @Nonnull
    @Override
    DefaultElementHandler self() {
        return this;
    }

    @Override
    void destroyEaObject() {
        element.destroy();
    }
}
