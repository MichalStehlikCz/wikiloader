package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

class EaProductPackageExporter extends EaObjectRegularExporter<EaProductPackage> {

    private static final Logger LOG = LogManager.getLogger(EaProductPackageExporter.class);

    private static final String PACKAGE_DESCRIPTION_NAME = "package_description";

    private final PackageDocumentExporter documentExporter;

    EaProductPackageExporter(EaProductPackage eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
        this.documentExporter = new PackageDocumentExporter(
            "pp_" + eaObject.getAlias().orElseThrow(), eaObject.getName(),
            eaObject.getNamespace().orElseThrow(), eaObject.getTechnicalPackages().stream()
                .map(EaTechnicalPackageRef::getObject)
                .collect(Collectors.toList()), eaObject.getRepository(), wikiClient);
    }

    @Override
    void appendTags() {
        startBuilder.append("{{tag>product_package}}");
    }

    /**
     * Append diagrams to document. By default, inserts all diagrams inline. If there is more than one diagram, show
     * diagram headers, in case of single diagram assumes that it is overview and clips title
     */
    private void appendDiagrams() {
        var diagrams = getEaObject().getDiagrams().stream().filter(EaObjectRef::isTopic).collect(Collectors.toList());
        for (var diagram : diagrams) {
            inlineObject(diagram, diagrams.size() > 1);
            subObjects.add(diagram);
        }
    }

    @Override
    void appendBody() {
        // handle diagrams
        appendDiagrams();
        // insert own content
        appendDocument();
        startBuilder.append("===== Contents =====\n");
        for (var techPackage : getEaObject().getTechnicalPackages()) {
            if (techPackage.hasLink()) {
                startBuilder.append("  * [[");
                techPackage.appendLink(startBuilder);
                startBuilder.append('|').append(techPackage.getShortTitle()).append("]]\n");
            } else {
                LOG.warn("Technical package {} excluded from content of product package {}", techPackage::getEaDesc,
                        () -> getEaObject().getEaDesc());
            }
        }
        startBuilder.append("===== Package Description =====\n");
        startBuilder.append("{{page>").append(PACKAGE_DESCRIPTION_NAME).append("&noheader&editbutton}}\n");
        documentExporter.appendDocumentsSection(startBuilder);
    }

    private void exportPackageDescription(String namespace) {
        pages.add(PACKAGE_DESCRIPTION_NAME);
        var id = namespace + ":" + PACKAGE_DESCRIPTION_NAME;
        var builder = new StringBuilder()
                .append("{{tag>product_package_description}}\n")
                .append("====== Package ").append(getEaObject().getName()).append(" Description ======\n");
        for (var techPackage : getEaObject().getTechnicalPackages()) {
            if (techPackage.hasLink()) {
                if (getEaObject().getTechnicalPackages().size() > 1) {
                    builder.append("==== ").append(techPackage.getShortTitle()).append(" ====\n");
                }
                builder.append("{{page>");
                techPackage.appendNamespace(builder, true);
                builder.append(EaTechnicalPackageExporter.PACKAGE_DESCRIPTION_NAME)
                        .append("&noheader}}\n");
            }
        }
        getWikiClient().putGeneratedPage(id, builder.toString());
    }

    @Override
    void syncNamespace(String namespace) {
        exportPackageDescription(namespace);
        documentExporter.export();
        super.syncNamespace(namespace);
    }

    @Override
    void syncWiki() {
        if (getEaObject().getNamespace().isEmpty()) {
            throw new InternalException("Technical package element should be mapped to namespace");
        }
        super.syncWiki();
    }

    @Override
    public String toString() {
        return "EaProductPackageExporter{" + super.toString() + '}';
    }
}
