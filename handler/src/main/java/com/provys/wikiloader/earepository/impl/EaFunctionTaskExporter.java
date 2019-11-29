package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

public class EaFunctionTaskExporter extends EaUGTopicExporter<EaFunctionTaskRef, EaFunctionTask> {
    EaFunctionTaskExporter(EaFunctionTask eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    void appendTrainingGuide() {
        startBuilder.append("{{page>").append(EaUGTopicRef.USER_GUIDE_NAME).append("&noheader&editbutton}}\n");
        pages.add(EaUGTopicRef.USER_GUIDE_NAME);
        getWikiClient().putPageIfEmpty(getEaObject().getUserGuideTopicId().orElseThrow(),
                "====== " + getEaObject().getName() + " ======\n");
    }

    @Override
    void appendBody() {
        // handle diagrams
        appendDiagrams();
        // insert own content
        appendDocument();
        // insert included information
        appendIncludedIn();
        // insert user guide topic
        appendUserGuide();
        // insert training guide topic
        appendTrainingGuide();
    }
}
