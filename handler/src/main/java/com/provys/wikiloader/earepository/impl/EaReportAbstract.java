package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

class EaReportAbstract extends EaSysFuncAbstract<EaReportAbstractRef, EaReportRef> {

    EaReportAbstract(EaReportAbstractRef objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
                   @Nullable List<EaReportRef> elements) {
        super(objectRef, notes, diagrams, elements);
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaReportAbstractExporter(this, wikiClient);
    }

    @Override
    public String toString() {
        return "EaReportAbstract{} " + super.toString();
    }
}
