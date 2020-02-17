package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * MeaningGroup is meaning that has sub-items. These sub-items must be meanings. Meaning group can be included in
 * technical package, in that case, it is exported to user-guide of that technical package together with all its
 * child meanings. This inclusion is implemented in loader
 */
class EaMeaningGroupRef extends EaNamespaceElementRef
        implements EaMeaningRef {

    public static final String OVERVIEW_NAME = "overview";

    EaMeaningGroupRef(EaRepositoryImpl repository, @Nullable EaObjectRef parent, String name,
                      @Nullable String alias, int treePos, int elementId) {
        super(repository, parent, name, alias, "Meaning", "ArchiMate_Meaning", treePos, elementId);
    }

    @Nonnull
    public Optional<String> getOverviewTopicName() {
        return Optional.of(OVERVIEW_NAME);
    }

    @Nonnull
    public Optional<String> getOverviewTopicId() {
        return getNamespace().map(ns -> ns + ":" + OVERVIEW_NAME);
    }

    @Override
    @Nonnull
    public EaMeaningGroup getObject() {
        return getRepository().getLoader().loadMeaningGroup(this);
    }
}
