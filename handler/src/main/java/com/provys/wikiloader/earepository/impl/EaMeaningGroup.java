package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class EaMeaningGroup extends EaParentBase<EaMeaningGroupRef, EaDiagramRef, EaMeaningRef> {

    EaMeaningGroup(EaMeaningGroupRef objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
                   @Nullable List<EaMeaningRef> elements) {
        super(objectRef, notes, diagrams, elements);
    }

    @Nonnull
    public Optional<String> getOverviewTopicName() {
        return getObjectRef().getOverviewTopicName();
    }

    @Nonnull
    public Optional<String> getOverviewTopicId() {
        return getObjectRef().getOverviewTopicId();
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaMeaningGroupExporter(this, wikiClient);
    }

    @Override
    public String toString() {
        return "EaMeaningGroup{} " + super.toString();
    }
}
