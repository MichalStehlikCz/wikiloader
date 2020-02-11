package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

class EaReportAbstractExporter extends EaParentExporter<EaReportAbstractRef, EaReportAbstract> {

    EaReportAbstractExporter(EaReportAbstract eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    /**
     * Append header for elements section
     */
    @Override
    void appendElementsHeader() {
        startBuilder.append("\n===== Reports =====\n");
    }

    void appendDescription() {
        var descriptionTopicName = getEaObject().getDescriptionTopicName().orElseThrow();
        startBuilder.append("===== Description =====\n")
                .append("{{page>").append(descriptionTopicName)
                .append("&noheader&editbutton}}\n")
                .append('\n');
        pages.add(descriptionTopicName);
        getWikiClient().putPageIfEmpty(getEaObject().getDescriptionTopicId().orElseThrow(),
                "====== " + getEaObject().getPlainName() + " ======\n");
    }

    @Override
    void appendDocument() {
        super.appendDocument();
        appendDescription();
    }
}
