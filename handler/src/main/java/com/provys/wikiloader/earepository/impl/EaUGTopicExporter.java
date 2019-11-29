package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.provyswiki.ProvysWikiClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class EaUGTopicExporter<R extends EaUGTopicRef<R, T>, T extends EaUGTopic<R, T>>
        extends EaDiagramOwnerExporter<R, T> {

    private static final Logger LOG = LogManager.getLogger(EaUGTopicExporter.class);

    EaUGTopicExporter(T eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    void appendUserGuide() {
        var userGuideTopicName = getEaObject().getUserGuideTopicName().orElseThrow();
        startBuilder.append("{{page>").append(userGuideTopicName)
                .append("&noheader&editbutton}}\n");
        pages.add(userGuideTopicName);
        getWikiClient().putPageIfEmpty(getEaObject().getUserGuideTopicId().orElseThrow(),
                "====== " + getEaObject().getName() + " ======\n");
    }

    private void appendTechnicalPackageLink(EaTechnicalPackageRef technicalPackage) {
        startBuilder.append("[[");
        technicalPackage.appendLink(startBuilder);
        startBuilder.append("|").append(technicalPackage.getTitleInGroup()).append("]]");
    }

    void appendIncludedIn() {
        var includedIn = getEaObject().getIncludedIn();
        if (includedIn.size() == 1) {
            startBuilder.append("Included in technical package ");
            appendTechnicalPackageLink(includedIn.get(0));
            startBuilder.append(".\n");
        } else if (includedIn.size() > 1) {
            startBuilder.append("Included in technical packages:\n");
            for (var technicalPackage : includedIn) {
                startBuilder.append("  * ");
                appendTechnicalPackageLink(technicalPackage);
                startBuilder.append("\n");
            }
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

    @Override
    void syncWiki() {
        if (getEaObject().getNamespace().isEmpty()) {
            throw new InternalException(LOG, "Technical package element should be mapped to namespace");
        }
        super.syncWiki();
    }
}
