package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;
import com.provys.wikiloader.earepository.EaObjectRef;
import com.provys.wikiloader.earepository.EaPackageRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract class EaParentBase<T extends EaObjectRef, D extends EaDiagramRef, E extends EaElementRef, P extends EaPackageRef>
        extends EaObjectRegularBase<T> {
    @Nonnull
    private final List<D> diagrams;
    @Nonnull
    private final List<E> elements;
    @Nonnull
    private final List<P> packages;

    EaParentBase(T objectRef, @Nullable String notes, @Nullable List<D> diagrams, @Nullable List<E> elements,
                 @Nullable List<P> packages) {
        super(objectRef, notes);
        if (diagrams == null) {
            this.diagrams = Collections.emptyList();
        } else {
            this.diagrams = List.copyOf(diagrams);
        }
        if (elements == null) {
            this.elements = Collections.emptyList();
        } else {
            this.elements = List.copyOf(elements);
        }
        if (packages == null) {
            this.packages = Collections.emptyList();
        } else {
            this.packages = List.copyOf(packages);
        }
    }

    @Nonnull
    List<D> getDiagrams() {
        return diagrams;
    }

    @Nonnull
    List<E> getElements() {
        return elements;
    }

    @Nonnull
    List<P> getPackages() {
        return packages;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaParentBase<?, ?, ?, ?> that = (EaParentBase<?, ?, ?, ?>) o;
        return diagrams.equals(that.diagrams) &&
                elements.equals(that.elements) &&
                packages.equals(that.packages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), diagrams, elements, packages);
    }

    @Override
    public String toString() {
        return "EaParentBase{} " + super.toString();
    }
}
