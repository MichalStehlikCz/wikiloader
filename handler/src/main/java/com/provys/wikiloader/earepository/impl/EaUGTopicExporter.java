package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

class EaUGTopicExporter<R extends EaUGTopicRefBase<R, T>, T extends EaUGTopicBase<R, T>>
        extends EaDiagramOwnerExporter<R, T> {

    EaUGTopicExporter(T eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    void appendUserGuide() {
        var userGuideTopicName = getEaObject().getUserGuideTopicName().orElseThrow();
        startBuilder.append("===== User Guide =====\n")
                .append("{{page>").append(userGuideTopicName)
                .append("&noheader&editbutton}}\n")
                .append('\n');
        pages.add(userGuideTopicName);
        getWikiClient().putPageIfEmpty(getEaObject().getUserGuideTopicId().orElseThrow(),
                "====== " + getEaObject().getPlainName() + " ======\n");
    }

    void appendIncludedIn() {
        appendList(null, "Included in technical package",
                "Included in technical packages", getEaObject().getIncludedIn());
    }

    void appendReports() {
        appendList(null, "Uses report",
                "Uses reports", getEaObject().getReports());
    }

    void appendInterfaces() {
        appendList(null, "Uses interface",
                "Uses interfaces", getEaObject().getInterfaces());
    }

    @Override
    void appendBody() {
        // handle diagrams
        appendDiagrams();
        // insert own content
        appendDocument();
        // insert included information
        appendIncludedIn();
        // insert used reports
        appendReports();
        // insert used interfaces
        appendInterfaces();
        // insert user guide topic
        appendUserGuide();
    }
}
