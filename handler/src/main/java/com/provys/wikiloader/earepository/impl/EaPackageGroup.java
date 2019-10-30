package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

class EaPackageGroup extends EaParentBase<EaPackageGroupRef, EaDiagramRef, EaProductPackageRef, EaPackageGroupRef> {
    EaPackageGroup(EaPackageGroupRef objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
                   @Nullable List<EaProductPackageRef> elements, @Nullable List<EaPackageGroupRef> packages) {
        super(objectRef, notes, diagrams, elements, packages);
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaPackageGroupExporter(this, wikiClient);
    }
}
