package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class EaReportAbstractRef extends EaSysFuncAbstractRef implements EaReportRef {

    EaReportAbstractRef(EaRepositoryImpl repository, @Nullable EaObjectRef parent, String name,
                      @Nullable String alias, int treePos, int elementId) {
        super(repository, parent, name, alias, "Representation", "ArchiMate_Representation", treePos,
                elementId);
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Report " + getPlainName();
    }

    @Override
    @Nonnull
    public EaReportAbstract getObject() {
        return getRepository().getLoader().loadReportAbstract(this);
    }

    @Override
    public String toString() {
        return "EaReportAbstractRef{} " + super.toString();
    }
}
