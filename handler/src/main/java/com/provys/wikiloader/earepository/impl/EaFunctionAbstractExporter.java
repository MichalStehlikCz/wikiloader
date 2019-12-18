package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

/**
 * Implements export of abstract function (e.g. one that might be connected to workflow, but is implemented in system
 * using multiple tasks)
 */
class EaFunctionAbstractExporter extends EaParentExporter<EaFunctionAbstractRef, EaFunctionAbstract> {
    EaFunctionAbstractExporter(EaFunctionAbstract eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    /**
     * Append header for elements section
     */
    @Override
    void appendElementsHeader() {
        startBuilder.append("\n===== Implementations =====\n");
    }
}
