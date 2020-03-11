package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaObjectRef;
import com.provys.wikiloader.earepository.EaRepository;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

class PackageDocumentExporter {

  private static final String EXPORTS = "export:package_";
  private static final String EXPORT_USER_GUIDE_POSTFIX = "_ug";
  private static final String EXPORT_FULL_TRAINING_GUIDE_POSTFIX = "_tg";
  private static final String EXPORT_TRAINING_GUIDE_POSTFIX = "_tgnoug";

  private final String alias;
  private final String name;
  private final String namespace;
  private final EaRepository repository;
  private final Set<EaTechnicalPackage> technicalPackages;
  private final ProvysWikiClient wikiClient;

  PackageDocumentExporter(String alias, String name, String namespace,
      Collection<EaTechnicalPackage> technicalPackages, EaRepository repository,
      ProvysWikiClient wikiClient) {
    this.alias = alias;
    this.name = name;
    this.namespace = namespace;
    this.technicalPackages = Set.copyOf(technicalPackages);
    this.repository = repository;
    this.wikiClient = wikiClient;
  }

  void appendDocumentsSection(StringBuilder builder) {
    builder.append("===== Document Export Pages =====\n");
    builder.append("  * [[").append(EXPORTS).append(alias)
        .append(EXPORT_USER_GUIDE_POSTFIX).append("|User Guide]]\n")
        .append("  * [[").append(EXPORTS).append(alias)
        .append(EXPORT_FULL_TRAINING_GUIDE_POSTFIX).append("|Full Training Guide]]\n")
        .append("  * [[").append(EXPORTS).append(alias)
        .append(EXPORT_TRAINING_GUIDE_POSTFIX).append("|Short Training Guide]]\n")
        .append('\n');
  }

  private void appendOutput(StringBuilder builder, String postfix) {
    builder.append('\n')
        .append("====== Output ======\n")
        .append("Exported document: {{:").append(EXPORTS).append(alias)
        .append(postfix).append(".docx}} \\\\\n")
        .append("Log: {{:").append(EXPORTS).append(alias)
        .append(postfix).append(".log}} \\\n");
    wikiClient.putGeneratedPage(EXPORTS + alias + postfix, builder.toString());
  }

  private void appendFunctions(StringBuilder builder,
      Function<EaObjectRef, Optional<String>> linkFunction) {
    var functionContentBuilder = repository.getWikiSetBuilder()
        .setThreshold(3);
    for (var technicalPackage: technicalPackages) {
      functionContentBuilder.addEaObjects(technicalPackage.getFunctions());
    }
    for (var contentObject : functionContentBuilder.build()) {
      contentObject.appendContent(builder, linkFunction);
    }
  }

  private static Optional<String> getUserGuideTopicId(EaObjectRef objectRef) {
    if (objectRef instanceof EaUGTopicRef) {
      return ((EaUGTopicRef) objectRef).getUserGuideTopicId();
    }
    return Optional.empty();
  }

  private void exportUserGuide() {
    var builder = new StringBuilder()
        .append("====== ").append(name).append(" - User Guide ======\n");
    appendFunctions(builder, PackageDocumentExporter::getUserGuideTopicId);
    appendOutput(builder, EXPORT_USER_GUIDE_POSTFIX);
  }

  private static Optional<String> getFullTrainingGuideTopicId(EaObjectRef objectRef) {
    if (objectRef instanceof EaFunctionTaskRef) {
      return ((EaFunctionTaskRef) objectRef).getFullTrainingGuideTopicId();
    }
    if (objectRef instanceof EaUGTopicRef) {
      return ((EaUGTopicRef) objectRef).getUserGuideTopicId();
    }
    return Optional.empty();
  }

  private void exportFullTrainingGuide() {
    var builder = new StringBuilder()
        .append("====== ").append(name).append(" - Full Training Guide ======\n");
    appendFunctions(builder, PackageDocumentExporter::getFullTrainingGuideTopicId);
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
        .append("====== ").append(name).append(" - Short Training Guide ======\n");
    var functionContentBuilder = repository.getWikiSetBuilder()
        .setThreshold(3);
    for (var technicalPackage: technicalPackages) {
      functionContentBuilder.addEaObjects(technicalPackage.getFunctions()
          .stream()
          .filter(eaElementRef -> (eaElementRef instanceof EaFunctionTaskRef))
          .collect(Collectors.toList()));
    }
    for (var contentObject : functionContentBuilder.build()) {
      contentObject.appendContent(builder, PackageDocumentExporter::getTrainingGuideTopicId);
    }
    appendOutput(builder, EXPORT_TRAINING_GUIDE_POSTFIX);
  }

  void export() {
    exportUserGuide();
    exportFullTrainingGuide();
    exportTrainingGuide();
  }

  @Override
  public String toString() {
    return "PackageDocumentExporter{"
        + "alias='" + alias + '\''
        + ", namespace='" + namespace + '\''
        + ", technicalPackages=" + technicalPackages
        + ", wikiClient=" + wikiClient
        + '}';
  }
}
