package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class EaReportAbstract extends EaParentBase<EaReportAbstractRef, EaDiagramRef, EaReportRef>
        implements EaElementRef {

    EaReportAbstract(EaReportAbstractRef objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
                   @Nullable List<EaReportRef> elements) {
        super(objectRef, notes, diagrams, elements);
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaReportAbstractExporter(this, wikiClient);
    }

    @Nonnull
    public Optional<String> getDescriptionTopicName() {
        return getObjectRef().getDescriptionTopicName();
    }

    @Nonnull
    public Optional<String> getDescriptionTopicId() {
        return getObjectRef().getDescriptionTopicId();
    }

    @Override
    public int getElementId() {
        return getObjectRef().getElementId();
    }
}
