package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;
import com.provys.wikiloader.earepository.EaObjectRef;
import com.provys.wikiloader.earepository.EaPackageRef;

import java.util.stream.Collectors;

class EaPackageExporter<R extends EaPackageRef, T extends EaPackageBase<R, ? extends EaDiagramRef, ? extends EaElementRef,
        ? extends EaPackageRef>> extends EaParentExporter<R, T> {

    EaPackageExporter(T eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
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

    @Override
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
