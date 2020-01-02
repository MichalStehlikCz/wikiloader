package com.provys.wikiloader.earepository.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("squid:S2160") // packageGroup is only cached -> no need to include it in equals
abstract class EaItemGroupRef<E extends EaItemRef, R extends EaItemGroupRef<E, R, G>,
        G extends EaItemGroup<E, R, G>> extends EaDefaultPackageRef {

    @Nullable
    protected G packageGroup;

    EaItemGroupRef(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name,
                   @Nullable String alias, @Nullable String stereotype, int treePos, int packageId) {
        super(repository, parent, name, alias, stereotype, treePos, packageId);
    }

    protected abstract void loadObject();

    @Override
    @Nonnull
    public G getObject() {
        if (packageGroup == null) {
            loadObject();
        }
        assert (packageGroup != null);
        return packageGroup;
    }

    @Override
    public String toString() {
        return "EaPackageGroupRef{" +((packageGroup == null) ? "not loaded" : "loaded") + "} " + super.toString();
    }
}
