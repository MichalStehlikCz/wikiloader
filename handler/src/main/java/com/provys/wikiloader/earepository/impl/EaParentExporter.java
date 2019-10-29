package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

class EaParentExporter<T extends EaParent<? extends EaObjectRef>> extends EaObjectRegularExporter<T> {

    private static final Logger LOG = LogManager.getLogger(EaParentExporter.class);

    EaParentExporter(T eaObject, ProvysWikiClient wikiClient) {
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

    /**
     * Append header for subpackages section
     */
    void appendPackagesHeader() {
        startBuilder.append("\n===== Areas =====\n");
    }

    /**
     * Append subpackages to document. By default, inserts just list of subpackages. Insert section header only if there
     * is at least one package
     */
    void appendPackages() {
        var packages = getEaObject().getPackages().stream().filter(EaObjectRef::isTopic).collect(Collectors.toList());
        if (!packages.isEmpty()) {
            appendPackagesHeader();
            for (var subPackage : packages) {
                linkObject(subPackage);
                subObjects.add(subPackage);
            }
        }
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

    void appendBody() {
        // handle diagrams
        appendDiagrams();
        // insert own content
        appendDocument();
        // handle sub-packages
        appendPackages();
        // handle elements
        appendElements();
    }
}
