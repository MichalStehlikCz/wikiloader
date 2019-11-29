package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents leaf element (no sub-elements) without any specific behaviour
 */
class EaLeafElement extends EaLeafElementBase<EaLeafElementRef> {

    EaLeafElement(EaLeafElementRef objectRef, @Nullable String notes, List<EaDiagramRef> diagrams) {
        super(objectRef, notes, diagrams);
    }

    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaDiagramOwnerExporter<>(this, wikiClient);
    }
}
