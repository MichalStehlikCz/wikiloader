package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

class EaReportAbstractExporter extends EaSysFuncAbstractExporter {

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

}
