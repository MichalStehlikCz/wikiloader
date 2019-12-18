package com.provys.wikiloader.earepository.impl;

import com.provys.catalogue.api.Entity;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
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
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaDataObjectExporter(this, wikiClient);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof EaDataObject)) return false;
        if (!super.equals(o)) return false;
        EaDataObject that = (EaDataObject) o;
        return Objects.equals(getEntity(), that.getEntity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEntity());
    }

    @Override
    public String toString() {
        return "EaDataObject{" +
                "entity=" + entity +
                "} " + super.toString();
    }
}
