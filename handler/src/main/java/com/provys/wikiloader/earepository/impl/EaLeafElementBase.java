package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Common ancestor for product and technical packages
 */
abstract class EaLeafElementBase<T extends EaElementRef> extends EaObjectRegularBase<T> {

    protected final List<EaDiagramRef> diagrams;

    EaLeafElementBase(T objectRef, @Nullable String notes, List<EaDiagramRef> diagrams) {
        super(objectRef, notes);
        this.diagrams = List.copyOf(diagrams);
    }

    List<EaDiagramRef> getDiagrams() {
        return diagrams;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaLeafElementBase<?> that = (EaLeafElementBase<?>) o;
        return Objects.equals(diagrams, that.diagrams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), diagrams);
    }
}
