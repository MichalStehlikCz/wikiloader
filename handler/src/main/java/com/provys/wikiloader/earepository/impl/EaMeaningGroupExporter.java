package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

public class EaMeaningGroupExporter extends EaParentExporter<EaMeaningGroupRef, EaMeaningGroup> {

    EaMeaningGroupExporter(EaMeaningGroup eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    /**
     * Append header for elements section
     */
    @Override
    void appendElementsHeader() {
        startBuilder.append("\n===== Specializations =====\n");
    }

    void appendOverview() {
        var overviewTopicName = getEaObject().getOverviewTopicName().orElseThrow();
        startBuilder.append("===== Overview =====\n")
                .append("{{page>").append(overviewTopicName)
                .append("&noheader&editbutton}}\n")
                .append('\n');
        pages.add(overviewTopicName);
        getWikiClient().putPageIfEmpty(getEaObject().getOverviewTopicId().orElseThrow(),
                "====== " + getEaObject().getPlainName() + " Overview ======\n");
    }

    @Override
    void appendDocument() {
        super.appendDocument();
        // insert user guide topic
        appendOverview();
    }
}
