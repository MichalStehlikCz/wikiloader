package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nullable;

class EaFunctionAbstractRef extends EaNamespaceElementRef implements EaFunctionRef {
    EaFunctionAbstractRef(EaRepositoryImpl repository, @Nullable EaObjectRef parent, String name,
                          @Nullable String alias, int treePos, int elementId) {
        super(repository, parent, name, alias, "BusinessService", "ArchiMate_BusinessService", treePos,
                elementId);
    }
}
