package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nullable;
import java.util.Objects;

abstract class EaElementRefBase extends EaObjectRefBase implements EaElementRef {

    private final int elementId;

    EaElementRefBase(@Nullable EaObjectRefBase parent, String name, @Nullable String alias,
                     @Nullable String stereotype, int treePos, int elementId) {
        super(parent, name, alias, stereotype, treePos);
        Objects.requireNonNull(parent);
        this.elementId = elementId;
    }

    @Override
    public int getElementId() {
        return elementId;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaElementRefBase eaElement = (EaElementRefBase) o;
        return (elementId == eaElement.elementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elementId);
    }

    @Override
    public String toString() {
        return "EaElementRefBase{" +
                "elementId=" + elementId +
                "} " + super.toString();
    }
}
