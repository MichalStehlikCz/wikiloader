package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class EaObjectRegular extends EaObjectRegularBase<EaObjectRef> {

    EaObjectRegular(EaObjectRef objectRef, @Nullable String notes) {
        super(objectRef, notes);
    }

    @Nonnull
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaObjectRegularExporter<>(this, wikiClient);
    }
}
