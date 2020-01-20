package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Abstract function is element of type BusinessService that is part of capability model. Abstract function can be used
 * in process model, does not have training guide, rather represents business action that has multiple implementations
 * in Provys system. These implementations are then represented by function tasks
 */
class EaFunctionAbstract extends EaParentBase<EaFunctionAbstractRef, EaDiagramRef, EaElementRef> {
    EaFunctionAbstract(EaFunctionAbstractRef objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
                       @Nullable List<EaElementRef> elements) {
        super(objectRef, notes, diagrams, elements);
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaFunctionAbstractExporter(this, wikiClient);
    }
}
