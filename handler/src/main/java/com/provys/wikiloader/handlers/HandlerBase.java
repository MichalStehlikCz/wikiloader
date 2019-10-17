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

    @Override
    public void sync(ProvysWikiClient wikiClient, boolean recursive) {
        var children = getExporter(wikiClient).run(recursive);
        // synchronize diagrams, elements and subpackages
        for (var child : children) {
            child.sync(wikiClient, true);
        }
    }
}
