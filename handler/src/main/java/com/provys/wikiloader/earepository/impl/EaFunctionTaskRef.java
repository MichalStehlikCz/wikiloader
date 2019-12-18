package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

class EaFunctionTaskRef extends EaUGTopicRef<EaFunctionTaskRef, EaFunctionTask> implements EaFunctionRef {

    private static final Logger LOG = LogManager.getLogger(EaFunctionTaskRef.class);

    static final String TRAINING_GUIDE_POSTFIX = "training_guide";
    static final String TRAINING_WALKTHROUGH_POSTFIX = "training_walkthrough";
    static final String TRAINING_MATERIALS_POSTFIX = "training_materials";
    static final String FULL_TRAINING_GUIDE_POSTFIX = "full_training_guide";

    EaFunctionTaskRef(EaRepositoryImpl repository, EaObjectRefBase parent, String name, @Nullable String alias,
                      int treePos, int elementId) {
        super(repository, parent, name, alias, "BusinessService", "ArchiMate_BusinessService", treePos,
                elementId);
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Function " + getName();
    }

    /**
     * @return topic name of training guide topic
     */
    @Nonnull
    Optional<String> getTrainingGuideTopicName() {
        var alias = getAlias();
        if (alias.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(alias.get() + "." + TRAINING_GUIDE_POSTFIX);
    }

    /**
     * @return full topicId of training guide topic
     */
    @Nonnull
    Optional<String> getTrainingGuideTopicId() {
        return getTrainingGuideTopicName()
                .flatMap(topicName ->
                        getParent()
                                .map(EaObjectRef::getNamespace) // get namespace from parent
                                .orElseThrow(() -> new InternalException(LOG, "Function task should have parent")) // if no parent, use "" as prefix)
                                .map(ns -> ns + ":" + topicName));
    }

    /**
     * @return topic name of training walk-through topic
     */
    @Nonnull
    Optional<String> getTrainingWalkThroughTopicName() {
        var alias = getAlias();
        if (alias.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(alias.get() + "." + TRAINING_WALKTHROUGH_POSTFIX);
    }

    /**
     * @return full topicId of training walk-through topic
     */
    @Nonnull
    Optional<String> getTrainingWalkThroughTopicId() {
        return getTrainingWalkThroughTopicName()
                .flatMap(topicName ->
                        getParent()
                                .map(EaObjectRef::getNamespace) // get namespace from parent
                                .orElseThrow(() -> new InternalException(LOG, "Function task should have parent")) // if no parent, use "" as prefix)
                                .map(ns -> ns + ":" + topicName));
    }

    /**
     * @return topic name of training materials topic
     */
    @Nonnull
    Optional<String> getTrainingMaterialsTopicName() {
        var alias = getAlias();
        if (alias.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(alias.get() + "." + TRAINING_MATERIALS_POSTFIX);
    }

    /**
     * @return full topicId of training materials topic
     */
    @Nonnull
    Optional<String> getTrainingMaterialsTopicId() {
        return getTrainingMaterialsTopicName()
                .flatMap(topicName ->
                        getParent()
                                .map(EaObjectRef::getNamespace) // get namespace from parent
                                .orElseThrow(() -> new InternalException(LOG, "Function task should have parent")) // if no parent, use "" as prefix)
                                .map(ns -> ns + ":" + topicName));
    }

    /**
     * @return topic name of training guide topic
     */
    @Nonnull
    Optional<String> getFullTrainingGuideTopicName() {
        var alias = getAlias();
        if (alias.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(alias.get() + "." + FULL_TRAINING_GUIDE_POSTFIX);
    }

    /**
     * @return full topicId of training guide topic
     */
    @Nonnull
    Optional<String> getFullTrainingGuideTopicId() {
        return getFullTrainingGuideTopicName()
                .flatMap(topicName ->
                        getParent()
                                .map(EaObjectRef::getNamespace) // get namespace from parent
                                .orElseThrow(() -> new InternalException(LOG, "Function task should have parent")) // if no parent, use "" as prefix)
                                .map(ns -> ns + ":" + topicName));
    }

    @Override
    public void appendPages(Collection<String> pages) {
        super.appendPages(pages);
        getTrainingGuideTopicName().ifPresent(pages::add);
        getTrainingWalkThroughTopicName().ifPresent(pages::add);
        getTrainingMaterialsTopicName().ifPresent(pages::add);
        getFullTrainingGuideTopicName().ifPresent(pages::add);
    }

    @Override
    protected synchronized void loadObject() {
        if (object == null) {
            object = getRepository().getLoader().loadFunctionTask(this);
        }
    }
}

