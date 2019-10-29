package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class EaParent<T extends EaObjectRef> extends EaObjectRegularBase<T> {

    @Nonnull
    private final List<EaDiagramRef> diagrams;
    @Nonnull
    private final List<EaElementRef> elements;
    @Nonnull
    private final List<EaPackageRef> packages;

    EaParent(EaRepository repository, T objectRef, @Nullable String notes,
             @Nullable List<EaDiagramRef> diagrams, @Nullable List<EaElementRef> elements,
             @Nullable List<EaPackageRef> packages) {
        super(repository, objectRef, notes);
        if ((diagrams == null) || diagrams.isEmpty()) {
            this.diagrams = Collections.emptyList();
        } else {
            this.diagrams = new ArrayList<>(diagrams);
        }
        if ((elements == null) || elements.isEmpty()) {
            this.elements = Collections.emptyList();
        } else {
            this.elements = new ArrayList<>(elements);
        }
        if ((packages == null) || packages.isEmpty()) {
            this.packages = Collections.emptyList();
        } else {
            this.packages = new ArrayList<>(packages);
        }
    }

    @Nonnull
    List<EaDiagramRef> getDiagrams() {
        return Collections.unmodifiableList(diagrams);
    }

    @Nonnull
    List<EaElementRef> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Nonnull
    List<EaPackageRef> getPackages() {
        return Collections.unmodifiableList(packages);
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaParentExporter<>(this, wikiClient);
    }
}
