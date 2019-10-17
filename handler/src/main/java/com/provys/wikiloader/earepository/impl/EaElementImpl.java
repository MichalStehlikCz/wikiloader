package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaElement;
import org.sparx.Element;

import javax.annotation.Nullable;
import java.util.Objects;

class EaElementImpl extends EaObjectImpl implements EaElement {

    static EaElementImpl ofElement(Element element, EaRepositoryImpl eaRepository) {
        var parentElement = element.GetParentID();
        var parent = (parentElement > 0) ? eaRepository.getElementById(parentElement) :
                eaRepository.getPackageById(element.GetPackageID());
        var result = new EaElementImpl(parent, element.GetName(), element.GetAlias(), element.GetStereotype(),
                element.GetTreePos(), element.GetElementID());
        element.destroy();
        return result;
    }

    private final int elementId;

    private EaElementImpl(@Nullable EaObjectImpl parent, String name, @Nullable String alias,
                          @Nullable String stereotype, int treePos, int elementId) {
        super(parent, name, alias, stereotype, treePos);
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
        EaElementImpl eaElement = (EaElementImpl) o;
        return (elementId == eaElement.elementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elementId);
    }

    @Override
    public String toString() {
        return "EaElementImpl{" +
                "elementId=" + elementId +
                "} " + super.toString();
    }
}
