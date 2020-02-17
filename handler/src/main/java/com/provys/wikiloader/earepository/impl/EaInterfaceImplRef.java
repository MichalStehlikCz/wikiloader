package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class EaInterfaceImplRef extends EaSysFuncImplRef implements EaInterfaceRef {

    static final String EA_TYPE = "TechnologyInterface";
    static final String EA_STEREOTYPE = "ArchiMate_TechnologyInterface";

    EaInterfaceImplRef(EaRepositoryImpl repository, EaObjectRef parent, String name, @Nullable String alias,
                       int treePos, int elementId) {
        super(repository, parent, name, alias, EA_TYPE, EA_STEREOTYPE, treePos, elementId);
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Interface " + getPlainName();
    }

    @Override
    public String toString() {
        return "EaInterfaceImplRef{} " + super.toString();
    }
}
