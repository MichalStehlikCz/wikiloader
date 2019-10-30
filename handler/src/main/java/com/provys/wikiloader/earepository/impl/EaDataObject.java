package com.provys.wikiloader.earepository.impl;

import com.provys.catalogue.api.Entity;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

class EaDataObject extends EaObjectRegularBase<EaElementRef> {

    @Nullable
    private final Entity entity;

    EaDataObject(EaElementRef objectRef, @Nullable Entity entity, @Nullable String notes) {
        super(objectRef, notes);
        this.entity = entity;
    }

    @Nonnull
    Optional<Entity> getEntity() {
        return Optional.ofNullable(entity);
    }

    @Nonnull
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaDataObjectExporter(this, wikiClient);
    }
}
