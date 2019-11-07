package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents product package, modelled by element with stereotype ArchiMate_Product
 */
class EaProductPackage extends EaLeafElementBase<EaProductPackageRef> {

    private final List<EaTechnicalPackageRef> technicalPackages;

    EaProductPackage(EaProductPackageRef objectRef, @Nullable String notes,
                     List<EaDiagramRef> diagrams, List<EaTechnicalPackageRef> technicalPackages) {
        super(objectRef, notes, diagrams);
        this.technicalPackages = List.copyOf(technicalPackages);
    }

    List<EaTechnicalPackageRef> getTechnicalPackages() {
        return technicalPackages;
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
