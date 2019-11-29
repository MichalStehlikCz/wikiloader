package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Implements support for Enterprise Architect element ArchiMate_Meaning. Element represents description of concept,
 * that is included in technical package and should be exported to user guide.
 */
class EaMeaning extends EaUGTopic<EaMeaningRef, EaMeaning> {
    EaMeaning(EaMeaningRef objectRef, @Nullable String notes, List<EaDiagramRef> diagrams,
              List<EaTechnicalPackageRef> includedIn) {
        super(objectRef, notes, diagrams, includedIn);
    }

    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaMeaningExporter(this, wikiClient);
    }
}