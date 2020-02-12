package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class EaInterfaceAbstractRef extends EaSysFuncAbstractRef implements EaReportRef {

    static final String EA_TYPE = "ApplicationInterface";
    static final String EA_STEREOTYPE = "ArchiMate_ApplicationInterface";

    EaInterfaceAbstractRef(EaRepositoryImpl repository, @Nullable EaObjectRef parent, String name,
                        @Nullable String alias, int treePos, int elementId) {
        super(repository, parent, name, alias, EA_TYPE, EA_STEREOTYPE, treePos,
                elementId);
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Report " + getPlainName();
    }

    @Override
    @Nonnull
    public EaInterfaceAbstract getObject() {
        return getRepository().getLoader().loadInterfaceAbstract(this);
    }

    @Override
    public String toString() {
        return "EaInterfaceAbstractRef{} " + super.toString();
    }
}
