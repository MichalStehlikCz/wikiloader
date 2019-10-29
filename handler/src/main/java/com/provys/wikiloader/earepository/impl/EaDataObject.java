package com.provys.wikiloader.earepository.impl;

import com.provys.catalogue.api.Entity;
import com.provys.wikiloader.earepository.EaElementRef;
import com.provys.wikiloader.earepository.EaRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

class EaDataObject extends EaObjectRegularBase<EaElementRef> {

    @Nullable
    private final Entity entity;

    EaDataObject(EaRepository repository, EaElementRef objectRef, @Nullable Entity entity, @Nullable String notes) {
        super(repository, objectRef, notes);
        this.entity = entity;
    }

    @Nonnull
    Optional<Entity> getEntity() {
        return Optional.ofNullable(entity);
    }
}
