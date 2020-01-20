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
        startBuilder.append("\n===== Chapters =====\n");
    }

    void appendUserGuide() {
        var userGuideTopicName = getEaObject().getUserGuideTopicName().orElseThrow();
        startBuilder.append("===== User Guide =====\n")
                .append("{{page>").append(userGuideTopicName)
                .append("&noheader&editbutton}}\n")
                .append('\n');
        pages.add(userGuideTopicName);
        getWikiClient().putPageIfEmpty(getEaObject().getUserGuideTopicId().orElseThrow(),
                "====== " + getEaObject().getName() + " ======\n");
    }

    @Override
    void appendDocument() {
        super.appendDocument();
        appendUserGuide();
    }
}
