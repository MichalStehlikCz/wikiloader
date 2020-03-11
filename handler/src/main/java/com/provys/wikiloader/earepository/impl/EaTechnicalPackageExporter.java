package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaObjectRef;

import java.util.List;
import java.util.stream.Collectors;

class EaTechnicalPackageExporter extends EaObjectRegularExporter<EaTechnicalPackage> {

    static final String PACKAGE_DESCRIPTION_NAME = "package_description";
    private static final String ATTRIBUTES_USED_NAME = "attributes_used";
    private static final String SAMPLE_DATA_NAME = "sample_data_description";
    private static final String OVERVIEW_NAME = "overview";
    private static final String COVERED_FUNCTIONS_NAME = "covered_functions";
    private static final String REPORTS_NAME = "reports";
    private static final String INTERFACES_NAME = "interfaces";
    private static final String ADDITIONAL_SERVICES_NAME = "additional_services";
    private static final String MIGRATION_NAME = "migration";
    private static final String SETTINGS_NAME = "settings";
    private static final String RELATED_NAME = "related";

    private boolean hasMigration = false;
    private String reports = null;
    private String interfaces = null;
    private final PackageDocumentExporter documentExporter;

    EaTechnicalPackageExporter(EaTechnicalPackage eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
        documentExporter = new PackageDocumentExporter(eaObject.getAlias().orElseThrow(),
            eaObject.getPlainName(), eaObject.getNamespace().orElseThrow(), List.of(eaObject),
            eaObject.getRepository(), wikiClient);
    }

    @Override
    void appendTags() {
        startBuilder.append("{{tag>technical_package}}");
    }

    private void prepareManualPages() {
        var namespace = getEaObject().getNamespace()
                .orElseThrow(() ->  new InternalException("Technical package element should be mapped to namespace"));
        hasMigration = !getWikiClient().getPage(namespace + ':' + MIGRATION_NAME).isEmpty();
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

    private void appendContainedIn() {
        appendList(null, "Contained in product package", "Contained in product packages",
                getEaObject().getContainedIn());
    }

    private void appendPrerequisities() {
        appendList(null, "Requires technical package", "Requires technical packages",
                getEaObject().getPrerequisities());
    }

    @Override
    void appendBody() {
        // handle diagrams
        appendDiagrams();
        // insert own content
        appendDocument();
        appendPrerequisities();
        appendContainedIn();
        startBuilder.append("===== Product description =====\n")
                .append("<btn collapse=\"Product_description\" type=\"success\" icon=\"fa fa-chevron-circle-down\">Single Page Description</btn>\n")
                .append('\n')
                .append("<collapse id=\"Product_description\" collapsed=\"true\"><well>\n")
                .append("{{page>").append(PACKAGE_DESCRIPTION_NAME).append("&noheader}}\n")
                .append("</well></collapse>\n")
                .append('\n');
        prepareManualPages();
        startBuilder.append("===== Description =====\n")
                .append("  * [[").append(ATTRIBUTES_USED_NAME).append("|Used attributes]]\n")
                .append("  * [[").append(SAMPLE_DATA_NAME).append("|Sample Data]]\n");
        if (hasMigration) {
            startBuilder.append("  * [[").append(MIGRATION_NAME).append("|Migration]]\n");
        }
        startBuilder.append('\n');
        documentExporter.appendDocumentsSection(startBuilder);
        startBuilder.append("===== Settings =====\n");
        startBuilder.append("  *[[").append(SETTINGS_NAME).append("|Settings]]\n")
                .append('\n')
                .append("===== Related Pages =====\n")
                .append("{{page>").append(RELATED_NAME).append("&noheader&editbutton}}\n");
    }

    private void exportOverview(String namespace) {
        pages.add(OVERVIEW_NAME);
        getWikiClient().putPageIfEmpty(namespace + ':' + OVERVIEW_NAME,
                "===== Technical Package " + getEaObject().getPlainName() + " Overview =====\n");
    }

    private void exportCoveredFunctions(String namespace) {
        pages.add(COVERED_FUNCTIONS_NAME);
        getWikiClient().putPageIfEmpty(namespace + ':' + COVERED_FUNCTIONS_NAME,
                "===== Covered Functions - Package " + getEaObject().getPlainName() + " =====\n");
    }

    /**
     * Generate reports page to reports property
     */
    private void prepareReports() {
        var reportList = getEaObject().getReports();
        if (!reportList.isEmpty()) {
            var builder = new StringBuilder();
            builder.append("===== Reports - Package ").append(getEaObject().getPlainName()).append(" =====\n");
            for (var report : reportList) {
                builder.append("==== ").append(report.getPlainName()).append(" ====\n");
                builder.append("{{page>").append(report.getDescriptionTopicId()).append("&noheader}}\n");
            }
            reports = builder.toString();
        }
    }

    private void exportReports(String namespace) {
        if (reports != null) {
            pages.add(REPORTS_NAME);
            getWikiClient().putGeneratedPage(namespace + ':' + REPORTS_NAME, reports);
        }
    }

    /**
     * Generate interfaces page to interfaces property
     */
    private void prepareInterfaces() {
        var interfaceList = getEaObject().getInterfaces();
        if (!interfaceList.isEmpty()) {
            var builder = new StringBuilder();
            builder.append("===== Interfaces - Package ").append(getEaObject().getPlainName()).append(" =====\n");
            for (var iface : interfaceList) {
                builder.append("==== ").append(iface.getPlainName()).append(" ====\n");
                builder.append("{{page>").append(iface.getDescriptionTopicId()).append("&noheader}}\n");
            }
            interfaces = builder.toString();
        }
    }

    private void exportInterfaces(String namespace) {
        if (interfaces != null) {
            pages.add(INTERFACES_NAME);
            getWikiClient().putGeneratedPage(namespace + ':' + INTERFACES_NAME, interfaces);
        }
    }

    private void exportAdditionalServices(String namespace) {
        pages.add(ADDITIONAL_SERVICES_NAME);
        getWikiClient().putPageIfEmpty(namespace + ':' + ADDITIONAL_SERVICES_NAME,
                "===== Recommended Additional Services for Package " + getEaObject().getName() + " =====\n");
    }

    private void exportPackageDescription(String namespace) {
        pages.add(PACKAGE_DESCRIPTION_NAME);
        var id = namespace + ":" + PACKAGE_DESCRIPTION_NAME;
        var builder = new StringBuilder()
                .append("{{tag>technical_package_description}}\n")
                .append("====== Package ").append(getEaObject().getName()).append(" Description ======\n")
                .append("===== Description =====\n")
                .append("{{page>").append(OVERVIEW_NAME).append("&noheader&editbutton}}\n")
                .append("\\\\\n");
        exportOverview(namespace);
        builder.append("===== Covered Functions =====\n")
                .append("{{page>").append(COVERED_FUNCTIONS_NAME).append("&noheader&editbutton}}\n")
                .append("\\\\\n");
        exportCoveredFunctions(namespace);
        // insert package content based on linked sub-packages and functions...
        prepareReports();
        if (reports != null) {
            exportReports(namespace);
            builder.append("===== Reports =====\n")
                    .append("{{page>").append(REPORTS_NAME).append("&noheader}}\n")
                    .append("\\\\\n");
        }
        prepareInterfaces();
        if (interfaces != null) {
            exportInterfaces(namespace);
            builder.append("===== Interfaces =====\n")
                    .append("{{page>").append(INTERFACES_NAME).append("&noheader}}\n")
                    .append("\\\\\n");
        }
        builder.append("===== Recommended Services to Purchase =====\n")
                .append("{{page>").append(ADDITIONAL_SERVICES_NAME).append("&noheader&editbutton}}\n")
                .append("\\\\\n");
        exportAdditionalServices(namespace);
        getWikiClient().putGeneratedPage(id, builder.toString());
    }

    private void exportAttributesUsed(String namespace) {
        pages.add(ATTRIBUTES_USED_NAME);
        getWikiClient().putPageIfEmpty(namespace + ':' + ATTRIBUTES_USED_NAME,
                "====== Attributes Used in " + getEaObject().getName() + " Package ======\n" +
                        "This page is used to keep consistency when configuring " + getEaObject().getName() +
                        " package.\n");
    }

    private void exportSampleDataDescription(String namespace) {
        pages.add(SAMPLE_DATA_NAME);
        getWikiClient().putPageIfEmpty(namespace + ':' + SAMPLE_DATA_NAME,
                "====== Sample Data for Package " + getEaObject().getName() + " ======\n");
    }

    private void exportMigration() {
        if (hasMigration) {
            pages.add(MIGRATION_NAME);
        }
    }

    private void exportSettings(String namespace) {
        pages.add(SETTINGS_NAME);
        getWikiClient().putPageIfEmpty(namespace + ':' + SETTINGS_NAME,
                "====== Settings for Package " + getEaObject().getName() + " ======\n");
    }

     private void exportRelated() {
        pages.add(RELATED_NAME);
    }

    @Override
    void syncNamespace(String namespace) {
        exportPackageDescription(namespace);
        exportAttributesUsed(namespace);
        exportSampleDataDescription(namespace);
        exportMigration();
        exportSettings(namespace);
        documentExporter.export();
        exportRelated();
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
        return "EaTechnicalPackageExporter{" + super.toString() + '}';
    }
}
