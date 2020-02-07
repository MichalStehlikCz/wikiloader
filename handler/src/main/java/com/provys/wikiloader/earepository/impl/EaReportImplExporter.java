package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

public class EaReportImplExporter extends EaDiagramOwnerExporter<EaReportImplRef, EaReportImpl> {

    EaReportImplExporter(EaReportImpl eaObject, ProvysWikiClient wikiClient) {
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

    private void appendUgTopicLink(EaUGTopicRef ugTopicTask) {
        startBuilder.append("[[");
        ugTopicTask.appendLink(startBuilder);
        startBuilder.append("|").append(ugTopicTask.getTitle()).append("]]");
    }

    void appendUsedIn() {
        var usedIn = getEaObject().getUsedIn();
        if (usedIn.size() == 1) {
            startBuilder.append("Used by ");
            appendUgTopicLink(usedIn.get(0));
            startBuilder.append(".\n")
                    .append('\n');
        } else if (usedIn.size() > 1) {
            startBuilder.append("Used by:\n");
            for (var functionTask : usedIn) {
                startBuilder.append("  * ");
                appendUgTopicLink(functionTask);
                startBuilder.append("\n");
            }
            startBuilder.append('\n');
        }
    }

    @Override
    void appendBody() {
        // handle diagrams
        appendDiagrams();
        // insert own content
        appendDocument();
        // insert included information
        appendUsedIn();
        // insert user guide topic
        appendDescription();
    }
}
