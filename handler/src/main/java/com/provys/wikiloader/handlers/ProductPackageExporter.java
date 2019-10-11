package com.provys.wikiloader.handlers;

import com.provys.common.exception.InternalException;
import com.provys.provyswiki.ProvysWikiClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

class ProductPackageExporter extends DefaultExporter<DefaultElementHandler> {

    private static final Logger LOG = LogManager.getLogger(ProductPackageExporter.class);

    private static final String PRODUCT_DESCRIPTION_NAME = "product_description";
    private static final String ATTRIBUTES_USED_NAME = "attributes_used";
    private static final String SAMPLE_DATA_NAME = "sample_data_description";
    private static final String REPORTS_NAME = "reports";
    private static final String INTERFACES_NAME = "interfaces";
    private static final String MIGRATION_NAME = "migration";
    private static final String SETTINGS_QUESTIONNAIRE_NAME = "settingsquestionnaire";
    private static final String SETTINGS_NAME = "settings";
    private static final String RELATED_NAME = "related";
    private static final String EXPORTS = "export:package_";
    private static final String EXPORT_SETTINGS_QUESTIONNAIRE_POSTFIX = "_sq";
    private static final String EXPORT_USER_GUIDE_POSTFIX = "_ug";
    private static final String EXPORT_TRAINING_GUIDE_POSTFIX = "_tg";
    private static final String EXPORT_TRAINING_GUIDE_SHORT_POSTFIX = "_tgnoug";

    private String reports;
    private String interfaces;
    private boolean hasMigration;
    private boolean hasSettingsQuestionnaire;

    ProductPackageExporter(DefaultElementHandler handler, ProvysWikiClient wikiClient) {
        super(handler, wikiClient);
    }

    private String getPackageName() {
        return getHandler().getEaName();
    }

    @Override
    @Nonnull
    String getTitle() {
        return getPackageName() + " Package";
    }

    private void prepareReports() {
        reports = null;
    }

    private void prepareInterfaces() {
        interfaces = null;
    }

    private void prepareManualPages() {
        var namespace = getHandler().getNamespace()
                .orElseThrow(() ->  new InternalException(LOG, "Product package element should be mapped to namespace"));
        hasMigration = !getWikiClient().getPage(namespace + ":" + MIGRATION_NAME).isEmpty();
        hasSettingsQuestionnaire = !getWikiClient().getPage(namespace + ":" + SETTINGS_QUESTIONNAIRE_NAME).isEmpty();
    }

    @Override
    void appendBody() {
        // handle diagrams
        appendDiagrams();
        // insert own content
        appendDocument();
        // product package description should not have sub-packages
        if (!getSubPackages().isEmpty()) {
            LOG.warn("Sub-packages are ignored in product package, name {}, wiki document {}",
                    () -> getHandler().getEaName(), () -> getHandler().getId());
        }
        // product package description should not have sub-elements
        if (!getElements().isEmpty()) {
            LOG.warn("Sub-elements are ignored in product package, name {}, wiki document {}",
                    () -> getHandler().getEaName(), () -> getHandler().getId());
        }
        startBuilder.append("===== Product description =====\n")
                .append("<btn collapse=\"Product_description\" type=\"success\" icon=\"fa fa-chevron-circle-down\">Single page description</btn>\n")
                .append("\n")
                .append("<collapse id=\"Product_description\" collapsed=\"true\"><well>\n")
                .append("{{page>").append(PRODUCT_DESCRIPTION_NAME).append("&noheader&editbutton}}\n")
                .append("</well></collapse>\n")
                .append("\n")
        .append("<btn collapse=\"Product_description_links\" type=\"success\" icon=\"fa fa-chevron-circle-down\">Package content</btn>")
        .append("\n")
        .append("<collapse id=\"Product_description_links\" collapsed=\"true\"><well>");
        // insert package content based on linked sub-packages and functions...
        prepareReports();
        if (reports != null) {
            startBuilder.append("==== Reports ====\n")
                    .append("{{page>").append(REPORTS_NAME).append("&noheader}}\n")
                    .append("\n");
        }
        prepareInterfaces();
        if (interfaces != null) {
            startBuilder.append("==== Interfaces ====\n")
                    .append("{{page>").append(INTERFACES_NAME).append("&noheader}}\n")
                    .append("\n");
        }
        startBuilder.append("</well></collapse>\n");
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
            startBuilder.append("  * [[").append(EXPORTS).append(getHandler().getEaAlias())
                    .append(EXPORT_SETTINGS_QUESTIONNAIRE_POSTFIX).append("|Settings questionnaire]]\n");
        }
        startBuilder.append("  * [[").append(EXPORTS).append(getHandler().getEaAlias())
                .append(EXPORT_USER_GUIDE_POSTFIX).append("|User guide]]\n")
                .append("  * [[").append(EXPORTS).append(getHandler().getEaAlias())
                .append(EXPORT_TRAINING_GUIDE_POSTFIX).append("|Training guide]]\n")
                .append("  * [[").append(EXPORTS).append(getHandler().getEaAlias())
                .append(EXPORT_TRAINING_GUIDE_SHORT_POSTFIX).append("|Training guide without user guide]]\n")
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

    private void exportProductDescription(String namespace) {
        pages.add(PRODUCT_DESCRIPTION_NAME);
        var id = namespace + ":" + PRODUCT_DESCRIPTION_NAME;
        if (getWikiClient().getPage(id).isEmpty()) {
            getWikiClient().putPage(id,"Package " + getPackageName() + " description");
        }
    }

    private void exportAttributesUsed(String namespace) {
        pages.add(ATTRIBUTES_USED_NAME);
        var id = namespace + ":" + ATTRIBUTES_USED_NAME;
        if (getWikiClient().getPage(id).isEmpty()) {
            getWikiClient().putPage(id, "====== Attributes Used in " + getPackageName() + " Package ======\n" +
                    "This page is used to keep consistency when configuring " + getPackageName() + " package.\n");
        }
    }

    private void exportSampleDataDescription(String namespace) {
        pages.add(SAMPLE_DATA_NAME);
        var id = namespace + ":" + SAMPLE_DATA_NAME;
        if (getWikiClient().getPage(id).isEmpty()) {
            getWikiClient().putPage(id, "====== Sample Data for Package " + getPackageName() + " ======\n");
        }
    }

    private void exportReports(String namespace) {
        if (reports != null) {
            pages.add(REPORTS_NAME);
            getWikiClient().putPage(namespace + ":" + REPORTS_NAME, reports);
        }
    }

    private void exportInterfaces(String namespace) {
        if (reports != null) {
            pages.add(INTERFACES_NAME);
            getWikiClient().putPage(namespace + ":" + INTERFACES_NAME, interfaces);
        }
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
        var id = namespace + ":" + SETTINGS_NAME;
        if (getWikiClient().getPage(id).isEmpty()) {
            getWikiClient().putPage(id, "====== Settings for Package " + getPackageName() + " ======\n");
        }
    }

    private void exportRelated() {
        pages.add(RELATED_NAME);
    }

    @Override
    void syncNamespace(String namespace) {
        exportProductDescription(namespace);
        exportAttributesUsed(namespace);
        exportSampleDataDescription(namespace);
        exportReports(namespace);
        exportInterfaces(namespace);
        exportMigration();
        exportSettingsQuestionnaire();
        exportSettings(namespace);
        exportRelated();
        super.syncNamespace(namespace);
    }

    @Override
    void syncWiki() {
        if (getHandler().getNamespace().isEmpty()) {
            throw new InternalException(LOG, "Product package element should be mapped to namespace");
        }
        super.syncWiki();
    }
}
