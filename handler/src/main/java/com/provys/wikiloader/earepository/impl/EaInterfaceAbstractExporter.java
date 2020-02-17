package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

class EaInterfaceAbstractExporter extends EaSysFuncAbstractExporter<EaInterfaceAbstractRef, EaInterfaceAbstract> {

    EaInterfaceAbstractExporter(EaInterfaceAbstract eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    /**
     * Append header for elements section
     */
    @Override
    void appendElementsHeader() {
        startBuilder.append("\n===== Interfaces =====\n");
    }

}
