package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaObjectRef;

import java.util.stream.Collectors;

class EaDiagramOwnerExporter<R extends EaObjectRef, T extends EaDiagramOwnerBase<R, ? extends EaDiagramRef>>
        extends EaObjectRegularExporter<T> {

    EaDiagramOwnerExporter(T eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    /**
     * Append diagrams to document. By default, inserts all diagrams inline. If there is more than one diagram, show
     * diagram headers, in case of single diagram assumes that it is overview and clips title
     */
    void appendDiagrams() {
        var diagrams = getEaObject().getDiagrams().stream().filter(EaObjectRef::isTopic).collect(Collectors.toList());
        for (var diagram : diagrams) {
            inlineObject(diagram, (diagrams.size() > 1));
            subObjects.add(diagram);
        }
    }

    @Override
    void appendBody() {
        // insert own content
        appendDocument();
        // handle diagrams
        appendDiagrams();
    }
}
