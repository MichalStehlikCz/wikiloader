package com.provys.wikiloader.earepository.impl;

import javax.annotation.Nullable;
import java.util.Objects;

class EaProductPackageRef extends EaDefaultElementRef {

    private final EaProductPackageType packageType;

    EaProductPackageRef(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name,
                        @Nullable String alias, int treePos, int elementId, EaProductPackageType packageType) {
        super(repository, parent, name, alias, "ArchiMate_Product", treePos, elementId, false);
        this.packageType = packageType;
    }

    @Override
    public EaProductPackage getObject() {
        return getRepository().getLoader().loadProductPackage(this);
    }

    EaProductPackageType getPackageType() {
        return packageType;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaProductPackageRef that = (EaProductPackageRef) o;
        return packageType == that.packageType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), packageType);
    }

    @Override
    public String toString() {
        return "EaProductPackageRef{" +
                "packageType=" + packageType +
                "} " + super.toString();
    }
}
