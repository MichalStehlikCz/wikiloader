package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

class EaInterfaceAbstract extends EaSysFuncAbstract<EaInterfaceAbstractRef, EaInterfaceRef> {

    EaInterfaceAbstract(EaInterfaceAbstractRef objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
                     @Nullable List<EaInterfaceRef> elements) {
        super(objectRef, notes, diagrams, elements);
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaInterfaceAbstractExporter(this, wikiClient);
    }

    @Override
    public String toString() {
        return "EaInterfaceAbstract{} " + super.toString();
    }
}

