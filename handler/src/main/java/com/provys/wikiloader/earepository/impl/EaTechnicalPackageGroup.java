package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nullable;
import java.util.List;

class EaTechnicalPackageGroup extends EaItemGroup<EaTechnicalPackageRef, EaTechnicalPackageGroupRef,
        EaTechnicalPackageGroup> {

    EaTechnicalPackageGroup(EaTechnicalPackageGroupRef objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams, @Nullable List<EaTechnicalPackageRef> elements, @Nullable List<EaTechnicalPackageGroupRef> packages) {
        super(objectRef, notes, diagrams, elements, packages);
    }

    @Override
    EaTechnicalPackageGroup self() {
        return this;
    }

    @Override
    public String toString() {
        return "EaTechnicalPackageGroup{} " + super.toString();
    }
}
