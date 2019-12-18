package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    List<EaTechnicalPackageRef> getTechPrerequisities() {
        return getTechnicalPackages().stream()
                .flatMap(techPackage -> techPackage.getObject().getPrerequisities().stream())
                .filter(techPackage -> !technicalPackages.contains(techPackage))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaProductPackageExporter(this, wikiClient);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaProductPackage that = (EaProductPackage) o;
        return Objects.equals(technicalPackages, that.technicalPackages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), technicalPackages);
    }

    @Override
    public String toString() {
        return "EaProductPackage{} " + super.toString();
    }
}
