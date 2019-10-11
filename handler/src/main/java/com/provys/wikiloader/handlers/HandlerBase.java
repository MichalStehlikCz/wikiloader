package com.provys.wikiloader.handlers;

import com.provys.provyswiki.ProvysWikiClient;

import javax.annotation.Nonnull;
import java.util.Objects;

abstract class HandlerBase<T extends HandlerBase> implements HandlerInt {

    private final ExporterProducer<T> exporterProducer;

    HandlerBase(ExporterProducer<T> exporterProducer) {
        this.exporterProducer = Objects.requireNonNull(exporterProducer);
    }

    @Nonnull
    private Exporter getExporter(ProvysWikiClient wikiClient) {
        return exporterProducer.produce(self(), wikiClient);
    }

    @Nonnull
    abstract T self();

    /**
     * Method closes EA resources. Called after export of given page is finished, before invoking export of child
     * objects
     */
    abstract void destroyEaObject();

    @Override
    public void sync(ProvysWikiClient wikiClient, boolean recursive) {
        var children = getExporter(wikiClient).run(recursive);
        // we now have everything we need from given ea object and can release it
        destroyEaObject();
        // synchronize diagrams, elements and subpackages
        for (var child : children) {
            child.sync(wikiClient, true);
        }
    }
}
