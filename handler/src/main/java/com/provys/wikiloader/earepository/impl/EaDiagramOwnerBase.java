package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Common ancestor for object, owning diagram(s)
 *
 * @param <T> type of object reference this object is created from
 * @param <D> is class of diagrams owned by this object
 */
abstract class EaDiagramOwnerBase<T extends EaObjectRef, D extends EaDiagramRef> extends EaObjectRegularBase<T> {

    @Nonnull
    private final List<D> diagrams;

    EaDiagramOwnerBase(T objectRef, @Nullable String notes, @Nullable List<D> diagrams) {
        super(objectRef, notes);
        if (diagrams == null) {
            this.diagrams = Collections.emptyList();
        } else {
            this.diagrams = List.copyOf(diagrams);
        }
    }

    @Nonnull
    List<D> getDiagrams() {
        return diagrams;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaDiagramOwnerBase<?, ?> that = (EaDiagramOwnerBase<?, ?>) o;
        return diagrams.equals(that.diagrams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), diagrams);
    }

    @Override
    public String toString() {
        return "EaDiagramOwnerBase{} " + super.toString();
    }
}
