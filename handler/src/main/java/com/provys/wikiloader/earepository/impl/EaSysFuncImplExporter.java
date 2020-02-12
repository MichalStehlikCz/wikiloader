package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

public class EaSysFuncImplExporter extends EaDiagramOwnerExporter<EaSysFuncImplRef, EaSysFuncImpl> {

    EaSysFuncImplExporter(EaSysFuncImpl eaObject, ProvysWikiClient wikiClient) {
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

    void appendUsedIn() {
        appendList(null, "Used by", "Used by", getEaObject().getUsedIn());
    }

    @Override
    void appendBody() {
        super.appendBody();
        // insert included information
        appendUsedIn();
        // insert user guide topic
        appendDescription();
    }
}
