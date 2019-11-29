package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EaNamespaceElement extends EaParentBase<EaElementRef, EaDiagramRef, EaElementRef> {

    EaNamespaceElement(EaElementRef objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
                       @Nullable List<EaElementRef> elements) {
        super(objectRef, notes, diagrams, elements);
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaParentExporter<>(this, wikiClient);
    }
}
