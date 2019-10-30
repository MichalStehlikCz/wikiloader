package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents product package, modelled by element with stereotype ArchiMate_Product
 */
class EaProductPackage extends EaObjectRegularBase<EaProductPackageRef> {

    private final List<EaDiagramRef> diagrams;
    private final List<EaElementRef> functions;

    EaProductPackage(EaProductPackageRef objectRef, @Nullable String notes,
                     List<EaDiagramRef> diagrams, List<EaElementRef> functions) {
        super(objectRef, notes);
        this.diagrams = List.copyOf(diagrams);
        this.functions = List.copyOf(functions);
    }

    List<EaDiagramRef> getDiagrams() {
        return diagrams;
    }

    List<EaElementRef> getFunctions() {
        return functions;
    }

    @Nonnull
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaProductPackageExporter(this, wikiClient);
    }

    @Nonnull
    public String getTitle() {
        return getName() + " Package";
    }
}
