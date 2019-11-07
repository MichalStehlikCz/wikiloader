package com.provys.wikiloader.earepository.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("squid:S2160") // packageGroup is only cached -> no need to include it in equals
class EaProductPackageRef extends EaDefaultElementRef {

    @Nullable
    private EaProductPackage productPackage;

    EaProductPackageRef(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name,
                        @Nullable String alias, int treePos, int elementId) {
        super(repository, parent, name, alias, "Product", "ArchiMate_Product", treePos, elementId,
                false);
    }

    private synchronized void loadObject() {
        if (productPackage == null) {
            productPackage = getRepository().getLoader().loadProductPackage(this);
        }
    }

    @Override
    @Nonnull
    public EaProductPackage getObject() {
        if (productPackage == null) {
            loadObject();
        }
        assert (productPackage != null);
        return productPackage;
    }


    @Override
    public String toString() {
        return "EaProductPackageRef{" +((productPackage == null) ? "not loaded" : "loaded") + "} " + super.toString();
    }
}
