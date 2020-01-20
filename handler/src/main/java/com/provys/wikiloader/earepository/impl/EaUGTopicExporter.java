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
                "====== " + getEaObject().getName() + " ======\n");
    }

    private void appendTechnicalPackageLink(EaTechnicalPackageRef technicalPackage) {
        startBuilder.append("[[");
        technicalPackage.appendLink(startBuilder);
        startBuilder.append("|").append(technicalPackage.getShortTitle()).append("]]");
    }

    void appendIncludedIn() {
        var includedIn = getEaObject().getIncludedIn();
        if (includedIn.size() == 1) {
            startBuilder.append("Included in technical package ");
            appendTechnicalPackageLink(includedIn.get(0));
            startBuilder.append(".\n")
                    .append('\n');
        } else if (includedIn.size() > 1) {
            startBuilder.append("Included in technical packages:\n");
            for (var technicalPackage : includedIn) {
                startBuilder.append("  * ");
                appendTechnicalPackageLink(technicalPackage);
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
        appendIncludedIn();
        // insert user guide topic
        appendUserGuide();
    }
}
