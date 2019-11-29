package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Represents technical package, modelled by element with stereotype ArchiMate_ApplicationComponent
 */
class EaTechnicalPackage extends EaLeafElementBase<EaTechnicalPackageRef> {

    private final List<EaElementRef> functions;
    private final List<EaProductPackageRef> containedIn;
    private final List<EaTechnicalPackageRef> prerequisities;

    EaTechnicalPackage(EaTechnicalPackageRef objectRef, @Nullable String notes,
                       List<EaDiagramRef> diagrams, List<EaElementRef> functions,
                       List<EaProductPackageRef> containedIn, List<EaTechnicalPackageRef> prerequisities) {
        super(objectRef, notes, diagrams);
        this.functions = List.copyOf(functions);
        this.containedIn = List.copyOf(containedIn);
        this.prerequisities = List.copyOf(prerequisities);
    }

    List<EaElementRef> getFunctions() {
        return functions;
    }

    List<EaProductPackageRef> getContainedIn() {
        return containedIn;
    }

    List<EaTechnicalPackageRef> getPrerequisities() {
        return prerequisities;
    }

    @Nonnull
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaTechnicalPackageExporter(this, wikiClient);
    }

    @Nonnull
    @Override
    public String getTitle() {
        return getName() + " Technical Package";
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaTechnicalPackage that = (EaTechnicalPackage) o;
        return Objects.equals(functions, that.functions) &&
                Objects.equals(containedIn, that.containedIn) &&
                Objects.equals(prerequisities, that.prerequisities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), functions, containedIn, prerequisities);
    }

    @Override
    public String toString() {
        return "EaTechnicalPackage{} " + super.toString();
    }
}