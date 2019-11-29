package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;
import com.provys.wikiloader.earepository.EaObjectRef;

import java.util.stream.Collectors;

class EaParentExporter<R extends EaObjectRef, T extends EaParentBase<R, ? extends EaDiagramRef, ? extends EaElementRef>>
        extends EaDiagramOwnerExporter<R, T> {

    EaParentExporter(T eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    /**
     * Append header for elements section
     */
    void appendElementsHeader() {
        startBuilder.append("\n===== Objects =====\n");
    }

    /**
     * Append subpackages to document. By default, inserts just list of subpackages. Insert section header only if there
     * is at least one package
     */
    void appendElements() {
        var elements = getEaObject().getElements().stream().filter(EaObjectRef::isTopic).collect(Collectors.toList());
        if (!elements.isEmpty()) {
            appendElementsHeader();
            for (var element : elements) {
                linkObject(element);
                subObjects.add(element);
            }
        }
    }

    @Override
    void appendBody() {
        // handle diagrams
        appendDiagrams();
        // insert own content
        appendDocument();
        // handle elements
        appendElements();
    }
}
