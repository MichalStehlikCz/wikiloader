package com.provys.wikiloader.earepository.impl;

import javax.annotation.Nullable;

class EaFunctionTaskRef extends EaUGTopicRef<EaFunctionTaskRef, EaFunctionTask> implements EaFunctionRef {

    static final String TRAINING_GUIDE_POSTFIX = "training_guide";
    static final String TRAINING_WALKTHROUGH_POSTFIX = "training_walkthrough";
    static final String TRAINING_MATERIALS_POSTFIX = "training_materials";

    EaFunctionTaskRef(EaRepositoryImpl repository, EaObjectRefBase parent, String name, @Nullable String alias,
                      int treePos, int elementId) {
        super(repository, parent, name, alias, "BusinessService", "ArchiMate_BusinessService", treePos,
                elementId);
    }

    @Override
    protected synchronized void loadObject() {
        if (object == null) {
            object = getRepository().getLoader().loadFunctionTask(this);
        }
    }
}

