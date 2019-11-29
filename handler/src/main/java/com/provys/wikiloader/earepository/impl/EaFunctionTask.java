package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Implements support for leaf level Enterprise Architect element ArchiMate_BusinessService. Elements represent
 * functions in system described as particular task (unlike non-leaf elements of the same type, that represent abstract
 * functions). Function can be included in technical package and should be exported to user guide.
 */
class EaFunctionTask extends EaUGTopic<EaFunctionTaskRef, EaFunctionTask> {
    EaFunctionTask(EaFunctionTaskRef objectRef, @Nullable String notes, List<EaDiagramRef> diagrams,
                   List<EaTechnicalPackageRef> includedIn) {
        super(objectRef, notes, diagrams, includedIn);
    }

    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return null;
    }
}
