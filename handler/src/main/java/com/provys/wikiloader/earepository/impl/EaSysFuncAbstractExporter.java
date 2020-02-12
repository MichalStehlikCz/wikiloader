package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

class EaSysFuncAbstractExporter<R extends EaSysFuncAbstractRef, T extends EaSysFuncAbstract<R, ?>>
        extends EaParentExporter<R, T> {

    EaSysFuncAbstractExporter(T eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
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
