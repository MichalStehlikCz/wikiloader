package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;
import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Common ancestor for representation of element or package with child elements
 *
 * @param <T> is type of object reference it is created from
 * @param <D> is list of references to diagrams attached to object
 * @param <E> is list of references to sub-elements attached to object
 */
abstract class EaParentBase<T extends EaObjectRef, D extends EaDiagramRef, E extends EaElementRef>
        extends EaDiagramOwnerBase<T, D> {

    @Nonnull
    private final List<E> elements;

    EaParentBase(T objectRef, @Nullable String notes, @Nullable List<D> diagrams, @Nullable List<E> elements) {
        super(objectRef, notes, diagrams);
        if (elements == null) {
            this.elements = Collections.emptyList();
        } else {
            this.elements = List.copyOf(elements);
        }
    }

    @Nonnull
    List<E> getElements() {
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaParentBase<?, ?, ?> that = (EaParentBase<?, ?, ?>) o;
        return elements.equals(that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elements);
    }

    @Override
    public String toString() {
        return "EaParentBase{} " + super.toString();
    }
}
