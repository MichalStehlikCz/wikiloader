package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nullable;

class EaMeaningItemRef extends EaUGTopicRefBase<EaMeaningItemRef, EaMeaningItem> implements EaMeaningRef {

    EaMeaningItemRef(EaRepositoryImpl repository, EaObjectRef parent, String name, @Nullable String alias,
                     int treePos, int elementId) {
        super(repository, parent, name, alias, "Meaning", "ArchiMate_Meaning", treePos, elementId);
    }

    @Override
    protected synchronized void loadObject() {
        if (object == null) {
            object = getRepository().getLoader().loadMeaningItem(this);
        }
    }
}
