package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nullable;
import java.util.List;

class EaFunctionAbstract extends EaParentBase<EaFunctionAbstractRef, EaDiagramRef, EaElementRef> {
    EaFunctionAbstract(EaFunctionAbstractRef objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
                       @Nullable List<EaElementRef> elements) {
        super(objectRef, notes, diagrams, elements);
    }

    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaFunctionAbstractExporter(this, wikiClient);
    }
}
