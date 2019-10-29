package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class EaPackageGroup extends EaParent<EaPackageGroupRef> {
    EaPackageGroup(EaRepository repository, EaPackageGroupRef objectRef, @Nullable String notes,
                   @Nullable List<EaDiagramRef> diagrams, @Nullable List<EaElementRef> elements,
                   @Nullable List<EaPackageRef> packages) {
        super(repository, objectRef, notes, diagrams, elements, packages);
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaParentExporter<>(this, wikiClient);
    }
}
