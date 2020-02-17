package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaObjectRef;

import java.util.Optional;
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
    private static final String SETTINGS_QUESTIONNAIRE_NAME = "settingsquestionnaire";
    private static final String SETTINGS_NAME = "settings";
    private static final String RELATED_NAME = "related";
    private static final String EXPORTS = "export:package_";
    private static final String EXPORT_SETTINGS_QUESTIONNAIRE_POSTFIX = "_sq";
    private static final String EXPORT_USER_GUIDE_POSTFIX = "_ug";
    private static final String EXPORT_FULL_TRAINING_GUIDE_POSTFIX = "_tg";
    private static final String EXPORT_TRAINING_GUIDE_POSTFIX = "_tgnoug";

    private boolean hasMigration = false;
    private boolean hasSettingsQuestionnaire = false;
    private String reports = null;
    private String interfaces = null;

    EaTechnicalPackageExporter(EaTechnicalPackage eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    @Override
    void appendTags() {
        startBuilder.append("{{tag>technical_package}}");
    }

    private void prepareManualPages() {
        var namespace = getEaObject().getNamespace()
                .orElseThrow(() ->  new InternalException("Technical package element should be mapped to namespace"));
        hasMigration = !getWikiClient().getPage(namespace + ":" + MIGRATION_NAME).isEmpty();
        hasSettingsQuestionnaire = !getWikiClient().getPage(namespace + ":" + SETTINGS_QUESTIONNAIRE_NAME).isEmpty();
    }

    /**
     * Append diagrams to document. By default, inserts all diagrams inline. If there is more than one diagram, show
     * diagram headers, in case of single diagram assumes that it is overview and clips title
     */
    private void appendDiagrams() {
        var diagrams = getEaObject().getDiagrams().stream().filter(EaObjectRef::isTopic).collect(Collectors.toList());
        for (var diagram : diagrams) {
            inlineObject(diagram, (diagrams.size() > 1));
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
                .append("\n")
                .append("<collapse id=\"Product_description\" collapsed=\"true\"><well>\n")
                .append("{{page>").append(PACKAGE_DESCRIPTION_NAME).append("&noheader}}\n")
                .append("</well></collapse>\n")
                .append("\n");
        prepareManualPages();
        startBuilder.append("===== Description =====\n")
                .append("  * [[").append(ATTRIBUTES_USED_NAME).append("|Used attributes]]\n")
                .append("  * [[").append(SAMPLE_DATA_NAME).append("|Sample Data]]\n");
        if (hasMigration) {
            startBuilder.append("  * [[").append(MIGRATION_NAME).append("|Migration]]\n");
        }
        startBuilder.append("\n");
        startBuilder.append("===== Document Export Pages =====\n");
        if (hasSettingsQuestionnaire) {
            startBuilder.append("  * [[").append(EXPORTS).append(getEaObject().getAlias().orElseThrow())
                    .append(EXPORT_SETTINGS_QUESTIONNAIRE_POSTFIX).append("|Settings questionnaire]]\n");
        }
        startBuilder.append("  * [[").append(EXPORTS).append(getEaObject().getAlias().orElseThrow())
                .append(EXPORT_USER_GUIDE_POSTFIX).append("|User Guide]]\n")
                .append("  * [[").append(EXPORTS).append(getEaObject().getAlias().orElseThrow())
                .append(EXPORT_FULL_TRAINING_GUIDE_POSTFIX).append("|Full Training Guide]]\n")
                .append("  * [[").append(EXPORTS).append(getEaObject().getAlias().orElseThrow())
                .append(EXPORT_TRAINING_GUIDE_POSTFIX).append("|Short Training Guide]]\n")
                .append("\n");
        startBuilder.append("===== Settings =====\n");
        if (hasSettingsQuestionnaire) {
            startBuilder.append("  * [[").append(SETTINGS_QUESTIONNAIRE_NAME).append("|Settings questionnaire]]\n");
        }
        startBuilder.append("  *[[").append(SETTINGS_NAME).append("|Settings]]\n")
                .append("\n");
        startBuilder.append("===== Related Pages =====\n")
                .append("{{page>").append(RELATED_NAME).append("&noheader&editbutton}}\n");
    }

    private void exportOverview(String namespace) {
        pages.add(OVERVIEW_NAME);
        getWikiClient().putPageIfEmpty(namespace + ":" + OVERVIEW_NAME,
                "===== Technical Package " + getEaObject().getPlainName() + " Overview =====\n");
    }

    private void exportCoveredFunctions(String namespace) {
        pages.add(COVERED_FUNCTIONS_NAME);
        getWikiClient().putPageIfEmpty(namespace + ":" + COVERED_FUNCTIONS_NAME,
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
            getWikiClient().putGeneratedPage(namespace + ":" + REPORTS_NAME, reports);
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
            getWikiClient().putGeneratedPage(namespace + ":" + INTERFACES_NAME, interfaces);
        }
    }

    private void exportAdditionalServices(String namespace) {
        pages.add(ADDITIONAL_SERVICES_NAME);
        getWikiClient().putPageIfEmpty(namespace + ":" + ADDITIONAL_SERVICES_NAME,
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
        getWikiClient().putPageIfEmpty(namespace + ":" + ATTRIBUTES_USED_NAME,
                "====== Attributes Used in " + getEaObject().getName() + " Package ======\n" +
                        "This page is used to keep consistency when configuring " + getEaObject().getName() +
                        " package.\n");
    }

    private void exportSampleDataDescription(String namespace) {
        pages.add(SAMPLE_DATA_NAME);
        getWikiClient().putPageIfEmpty(namespace + ":" + SAMPLE_DATA_NAME,
                "====== Sample Data for Package " + getEaObject().getName() + " ======\n");
    }

    private void exportMigration() {
        if (hasMigration) {
            pages.add(MIGRATION_NAME);
        }
    }

    private void exportSettingsQuestionnaire() {
        if (hasSettingsQuestionnaire) {
            pages.add(SETTINGS_QUESTIONNAIRE_NAME);
        }
    }

    private void exportSettings(String namespace) {
        pages.add(SETTINGS_NAME);
        getWikiClient().putPageIfEmpty(namespace + ":" + SETTINGS_NAME,
                "====== Settings for Package " + getEaObject().getName() + " ======\n");
    }

    private static Optional<String> getUserGuideTopicId(EaObjectRef objectRef) {
        if (objectRef instanceof EaUGTopicRef) {
            return ((EaUGTopicRef) objectRef).getUserGuideTopicId();
        }
        return Optional.empty();
    }

    private void appendOutput(StringBuilder builder, String postfix) {
        builder.append('\n')
                .append("====== Output ======\n")
                .append("Exported document: {{:").append(EXPORTS).append(getEaObject().getAlias().orElseThrow())
                .append(postfix).append(".docx}} \\\\\n")
                .append("Log: {{:").append(EXPORTS).append(getEaObject().getAlias().orElseThrow())
                .append(postfix).append(".log}} \\\n");
        getWikiClient().putGeneratedPage(EXPORTS + getEaObject().getAlias().orElseThrow() + postfix,
                builder.toString());
    }

    private void exportUserGuide() {
        var builder = new StringBuilder()
                .append("====== ").append(getEaObject().getName()).append(" - User Guide ======\n");
        var functionContent = getEaObject().getRepository().getWikiSetBuilder()
                .setThreshold(3)
                .addEaObjects(getEaObject().getFunctions()).build();
        for (var contentObject : functionContent) {
            contentObject.appendContent(builder, EaTechnicalPackageExporter::getUserGuideTopicId);
        }
        appendOutput(builder, EXPORT_USER_GUIDE_POSTFIX);
    }

    private static Optional<String> getFullTrainingGuideTopicId(EaObjectRef objectRef) {
        if (objectRef instanceof EaFunctionTaskRef) {
            return ((EaFunctionTaskRef) objectRef).getFullTrainingGuideTopicId();
        } else if (objectRef instanceof EaUGTopicRef) {
            return ((EaUGTopicRef) objectRef).getUserGuideTopicId();
        }
        return Optional.empty();
    }

    private void exportFullTrainingGuide() {
        var builder = new StringBuilder()
                .append("====== ").append(getEaObject().getName()).append(" - Full Training Guide ======\n");
        var functionContent = getEaObject().getRepository().getWikiSetBuilder()
                .setThreshold(3)
                .addEaObjects(getEaObject().getFunctions()).build();
        for (var contentObject : functionContent) {
            contentObject.appendContent(builder, EaTechnicalPackageExporter::getFullTrainingGuideTopicId);
        }
        appendOutput(builder, EXPORT_FULL_TRAINING_GUIDE_POSTFIX);
    }

    private static Optional<String> getTrainingGuideTopicId(EaObjectRef objectRef) {
        if (objectRef instanceof EaFunctionTaskRef) {
            return ((EaFunctionTaskRef) objectRef).getTrainingGuideTopicId();
        }
        return Optional.empty();
    }

    private void exportTrainingGuide() {
        var builder = new StringBuilder()
                .append("====== ").append(getEaObject().getName()).append(" - Short Training Guide ======\n");
        var functionContent = getEaObject().getRepository().getWikiSetBuilder()
                .setThreshold(3)
                .addEaObjects(getEaObject()
                        .getFunctions()
                        .stream()
                        .filter(eaElementRef -> (eaElementRef instanceof EaFunctionTaskRef))
                        .collect(Collectors.toList()))
                .build();
        for (var contentObject : functionContent) {
            contentObject.appendContent(builder, EaTechnicalPackageExporter::getTrainingGuideTopicId);
        }
        appendOutput(builder, EXPORT_TRAINING_GUIDE_POSTFIX);
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
        exportSettingsQuestionnaire();
        exportSettings(namespace);
        exportUserGuide();
        exportFullTrainingGuide();
        exportTrainingGuide();
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
}
