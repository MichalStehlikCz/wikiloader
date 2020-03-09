package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.impl.EaDefaultDiagram.DiagramObjectRef;

class EaDefaultDiagramExporter extends EaObjectRegularExporter<EaDefaultDiagram> {

    EaDefaultDiagramExporter(EaDefaultDiagram eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    private String getFilename() {
        return getEaObject().getTopicId().orElseThrow() + ".png";
    }

    private void appendDiagramObject(DiagramObjectRef diagramObject) {
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
    }

    @Override
    void appendBody() {
        startBuilder.append("{{map>").append(getFilename()).append("|Diagram ").append(getEaObject().getName())
                .append("}}\n");
        getEaObject().getDiagramObjects().stream().sorted().forEach(this::appendDiagramObject);
        startBuilder.append("{{<map}}");
        // insert own content
        appendDocument();
    }

    @Override
    void syncWiki() {
        var picture = getEaObject().getPicture();
        if (picture != null) {
            getWikiClient().putAttachment(getFilename(), picture, true, true);
        }
        super.syncWiki();
    }
}
