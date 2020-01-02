package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

abstract class EaItemGroup<E extends EaItemRef, R extends EaItemGroupRef<E, R, G>,
        G extends EaItemGroup<E, R, G>> extends EaPackageBase<R, EaDiagramRef, E, R> {
    EaItemGroup(R objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
                @Nullable List<E> elements, @Nullable List<R> packages) {
        super(objectRef, notes, diagrams, elements, packages);
    }

    abstract G self();

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaItemGroupExporter<E, R, G>(self(), wikiClient);
    }
}
