package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * Implements support for leaf level Enterprise Architect element ArchiMate_BusinessService. Elements represent
 * functions in system described as particular task (unlike non-leaf elements of the same type, that represent abstract
 * functions). Function can be included in technical package and should be exported to user guide.
 */
class EaFunctionTask extends EaUGTopicBase<EaFunctionTaskRef, EaFunctionTask> {

    EaFunctionTask(EaFunctionTaskRef objectRef, @Nullable String notes, List<EaDiagramRef> diagrams,
                   List<EaTechnicalPackageRef> includedIn) {
        super(objectRef, notes, diagrams, includedIn);
    }

    @Nonnull
    public Optional<String> getTrainingGuideTopicName() {
        return getObjectRef().getTrainingGuideTopicName();
    }

    @Nonnull
    public Optional<String> getTrainingGuideTopicId() {
        return getObjectRef().getTrainingGuideTopicId();
    }

    @Nonnull
    public Optional<String> getTrainingWalkThroughTopicName() {
        return getObjectRef().getTrainingWalkThroughTopicName();
    }

    @Nonnull
    public Optional<String> getTrainingWalkThroughTopicId() {
        return getObjectRef().getTrainingWalkThroughTopicId();
    }

    @Nonnull
    public Optional<String> getTrainingMaterialsTopicName() {
        return getObjectRef().getTrainingMaterialsTopicName();
    }

    @Nonnull
    public Optional<String> getTrainingMaterialsTopicId() {
        return getObjectRef().getTrainingMaterialsTopicId();
    }

    @Nonnull
    public Optional<String> getFullTrainingGuideTopicName() {
        return getObjectRef().getFullTrainingGuideTopicName();
    }

    @Nonnull
    public Optional<String> getFullTrainingGuideTopicId() {
        return getObjectRef().getFullTrainingGuideTopicId();
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaFunctionTaskExporter(this, wikiClient);
    }
}
