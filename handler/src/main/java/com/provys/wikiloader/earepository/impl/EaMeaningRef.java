package com.provys.wikiloader.earepository.impl;

import javax.annotation.Nullable;

class EaMeaningRef extends EaUGTopicRef<EaMeaningRef, EaMeaning> {

    EaMeaningRef(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name, @Nullable String alias,
                 int treePos, int elementId) {
        super(repository, parent, name, alias, "Meaning", "ArchiMate_Meaning", treePos, elementId);
    }

    @Override
    protected synchronized void loadObject() {
        if (object == null) {
            object = getRepository().getLoader().loadMeaning(this);
        }
    }
}
