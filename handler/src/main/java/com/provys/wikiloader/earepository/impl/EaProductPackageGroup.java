package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nullable;
import java.util.List;

class EaProductPackageGroup extends EaItemGroup<EaProductPackageRef, EaProductPackageGroupRef, EaProductPackageGroup> {
    EaProductPackageGroup(EaProductPackageGroupRef objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
                          @Nullable List<EaProductPackageRef> elements,
                          @Nullable List<EaProductPackageGroupRef> packages) {
        super(objectRef, notes, diagrams, elements, packages);
    }

    @Override
    EaProductPackageGroup self() {
        return this;
    }
}
