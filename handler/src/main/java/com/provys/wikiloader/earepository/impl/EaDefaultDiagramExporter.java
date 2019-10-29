package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

class EaDefaultDiagramExporter extends EaObjectRegularExporter<EaDefaultDiagram> {

    EaDefaultDiagramExporter(EaDefaultDiagram eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    private String getFilename() {
        return getEaObject().getTopicId().orElseThrow() + ".png";
    }

    @Override
    void appendBody() {
        // insert own content
        appendDocument();
    }

    void syncWiki() {
        getWikiClient().putAttachment(getFilename(), getEaObject().getDiagram(), true, true);
        super.syncWiki();
    }
}
