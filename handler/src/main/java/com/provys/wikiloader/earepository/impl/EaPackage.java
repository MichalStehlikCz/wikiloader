package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;
import com.provys.wikiloader.earepository.EaPackageRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

class EaPackage extends EaPackageBase<EaPackageRef, EaDiagramRef, EaElementRef, EaPackageRef> {

    EaPackage(EaPackageRef objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
              @Nullable List<EaElementRef> elements, @Nullable List<EaPackageRef> packages) {
        super(objectRef, notes, diagrams, elements, packages);
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaPackageExporter<>(this, wikiClient);
    }
}
