package com.provys.wikiloader.earepository.impl;

import javax.annotation.Nullable;

class EaFunctionAbstractRef extends EaNamespaceElementRef implements EaFunctionRef {
    EaFunctionAbstractRef(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name,
                          @Nullable String alias, int treePos, int elementId) {
        super(repository, parent, name, alias, "BusinessService", "ArchiMate_BusinessService", treePos,
                elementId);
    }
}
