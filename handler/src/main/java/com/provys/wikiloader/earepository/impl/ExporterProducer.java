package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaObject;

import javax.annotation.Nonnull;

interface ExporterProducer<T extends EaObject> {

    @Nonnull
    Exporter produce(T eaObject, ProvysWikiClient wikiClient);
}
