package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

class EaFunctionTaskExporter extends EaUGTopicExporter<EaFunctionTaskRef, EaFunctionTask> {
    EaFunctionTaskExporter(EaFunctionTask eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    private void appendTrainingWalkThrough(StringBuilder builder) {
        builder.append("{{page>").append(getEaObject().getTrainingWalkThroughTopicName().orElseThrow())
                .append("&noheader&editbutton}}\n");
        getWikiClient().putPageIfEmpty(getEaObject().getTrainingWalkThroughTopicId().orElseThrow(),
                "====== " + getEaObject().getName() + " Training Walk-Through ======\n");
        pages.add(getEaObject().getTrainingWalkThroughTopicName().orElseThrow());
    }

    private void appendTrainingMaterials(StringBuilder builder) {
        builder.append("{{page>").append(getEaObject().getTrainingMaterialsTopicName().orElseThrow())
                .append("&noheader&editbutton}}\n");
        getWikiClient().putPageIfEmpty(getEaObject().getTrainingMaterialsTopicId().orElseThrow(),
                "====== " + getEaObject().getName() + " Training Materials ======\n");
        pages.add(getEaObject().getTrainingMaterialsTopicName().orElseThrow());
    }

    void appendTrainingGuide() {
        startBuilder.append('\n')
                .append("===== Training Guide =====\n")
                .append("{{page>").append(getEaObject().getTrainingGuideTopicName().orElseThrow())
                .append("&noheader&editbutton}}\\\\\n");
        var builder = new StringBuilder()
                .append("====== ").append(getEaObject().getName()).append(" Training Guide ======\n")
                .append("===== Training Walkthrough =====\n");
        appendTrainingWalkThrough(builder);
        builder.append("===== Materials =====\n");
        appendTrainingMaterials(builder);
        getWikiClient().putGeneratedPage(getEaObject().getTrainingGuideTopicId().orElseThrow(), builder.toString());
        pages.add(getEaObject().getTrainingGuideTopicName().orElseThrow());
    }

    void exportFullTrainingGuide() {
        getWikiClient().putGeneratedPage(getEaObject().getFullTrainingGuideTopicId().orElseThrow(),
                "====== " + getEaObject().getName() + " Full Training Guide ======\n" +
                        "===== User Guide =====\n" +
                        "{{page>" + getEaObject().getUserGuideTopicName().orElseThrow() + "&noheader&editbutton}}\n" +
                        "===== Training Walkthrough =====\n" +
                        "{{page>" + getEaObject().getTrainingWalkThroughTopicName().orElseThrow() +
                        "&noheader&editbutton}}\n" +
                        "===== Materials =====\n" +
                        "{{page>" + getEaObject().getTrainingMaterialsTopicName().orElseThrow() +
                        "&noheader&editbutton}}\n");
        pages.add(getEaObject().getFullTrainingGuideTopicName().orElseThrow());
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
        // insert training guide topic
        appendTrainingGuide();
        // create full training guide page
        exportFullTrainingGuide();
    }
}
