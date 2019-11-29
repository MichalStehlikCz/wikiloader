package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;
import com.provys.wikiloader.earepository.EaPackageRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract class EaPackageBase<T extends EaPackageRef, D extends EaDiagramRef, E extends EaElementRef, P extends EaPackageRef>
        extends EaParentBase<T, D, E> {
    @Nonnull
    private final List<P> packages;

    EaPackageBase(T objectRef, @Nullable String notes, @Nullable List<D> diagrams, @Nullable List<E> elements,
                  @Nullable List<P> packages) {
        super(objectRef, notes, diagrams, elements);
        if (packages == null) {
            this.packages = Collections.emptyList();
        } else {
            this.packages = List.copyOf(packages);
        }
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
        EaPackageBase<?, ?, ?, ?> that = (EaPackageBase<?, ?, ?, ?>) o;
        return packages.equals(that.packages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), packages);
    }

    @Override
    public String toString() {
        return "EaPackageBase{} " + super.toString();
    }
}
