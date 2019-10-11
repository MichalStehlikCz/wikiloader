package com.provys.wikiloader.handlers;

import com.provys.provyswiki.ProvysWikiClient;

import javax.annotation.Nonnull;

public interface ExporterProducer<T extends HandlerInt> {

    @Nonnull
    Exporter produce(T handler, ProvysWikiClient wikiClient);
}
