package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Implements support for Enterprise Architect element ArchiMate_Meaning. Element represents description of concept,
 * that is included in technical package and should be exported to user guide.
 */
class EaMeaningItem extends EaUGTopicBase<EaMeaningItemRef, EaMeaningItem> {
    EaMeaningItem(EaMeaningItemRef objectRef, @Nullable String notes, List<EaDiagramRef> diagrams,
                  List<EaTechnicalPackageRef> includedIn) {
        super(objectRef, notes, diagrams, includedIn);
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaMeaningItemExporter(this, wikiClient);
    }
}
