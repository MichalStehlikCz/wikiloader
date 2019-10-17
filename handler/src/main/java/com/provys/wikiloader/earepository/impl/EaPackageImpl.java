package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaPackage;
import org.sparx.Package;

import javax.annotation.Nullable;
import java.util.Objects;

public class EaPackageImpl extends EaObjectImpl implements EaPackage {

    static EaPackageImpl ofPackage(Package pkg, EaRepositoryImpl eaRepository) {
        var parent = (pkg.GetParentID() == 0) ? null : eaRepository.getPackageById(pkg.GetParentID());
        var result = new EaPackageImpl(parent, pkg.GetName(), pkg.GetAlias(), pkg.GetStereotypeEx(), pkg.GetTreePos(),
                pkg.GetPackageID());
        pkg.destroy();
        return result;
    }

    private final int packageId;

    private EaPackageImpl(@Nullable EaObjectImpl parent, String name, @Nullable String alias,
                          @Nullable String stereotype, int treePos, int packageId) {
        super(parent, name, alias, stereotype, treePos);
        this.packageId = packageId;
    }

    @Override
    public int getPackageId() {
        return packageId;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaPackageImpl eaPackage = (EaPackageImpl) o;
        return (packageId == eaPackage.packageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), packageId);
    }

    @Override
    public String toString() {
        return "EaPackageImpl{" +
                "packageId=" + packageId +
                "} " + super.toString();
    }
}
