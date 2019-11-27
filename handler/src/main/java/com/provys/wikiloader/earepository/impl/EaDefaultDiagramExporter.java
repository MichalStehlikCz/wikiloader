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
        startBuilder.append("{{map>").append(getFilename()).append("|Diagram ").append(getEaObject().getName())
                .append("}}\n");
        getEaObject().getDiagramObjects().stream().sorted().forEach(diagramObject -> {
            if (diagramObject.getElementRef().hasLink()) {
                startBuilder.append("  * [[");
                diagramObject.getElementRef().appendLink(startBuilder);
                startBuilder.append('|');
                diagramObject.getElementRef().appendLink(startBuilder);
                startBuilder.append('@')
                        .append(diagramObject.getImgLeft()).append(',')
                        .append(diagramObject.getImgTop()).append(',')
                        .append(diagramObject.getImgRight()).append(',')
                        .append(diagramObject.getImgBottom()).append("]]\n");
            }
        });
        startBuilder.append("{{<map}}");
        // insert own content
        appendDocument();
    }

    @Override
    void syncWiki() {
        getWikiClient().putAttachment(getFilename(), getEaObject().getDiagram(), true, true);
        super.syncWiki();
    }
}
