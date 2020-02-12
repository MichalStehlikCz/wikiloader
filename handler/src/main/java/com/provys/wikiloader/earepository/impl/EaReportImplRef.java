package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

class EaReportImplRef extends EaSysFuncImplRef implements EaReportRef {

    EaReportImplRef(EaRepositoryImpl repository, EaObjectRef parent, String name, @Nullable String alias, int treePos,
                    int elementId) {
        super(repository, parent, name, alias, "Representation", "ArchiMate_Representation", treePos,
                elementId);
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Report " + getPlainName();
    }

    @Override
    public String toString() {
        return "EaReportImplRef{} " + super.toString();
    }
}
