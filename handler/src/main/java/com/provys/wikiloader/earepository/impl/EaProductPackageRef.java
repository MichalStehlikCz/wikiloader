package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("squid:S2160") // packageGroup is only cached -> no need to include it in equals
class EaProductPackageRef extends EaNamespaceElementRef implements EaItemRef {

    @Nullable
    private EaProductPackage productPackage;

    EaProductPackageRef(EaRepositoryImpl repository, @Nullable EaObjectRef parent, String name,
                        @Nullable String alias, int treePos, int elementId) {
        super(repository, parent, name, alias, "Product", "ArchiMate_Product", treePos, elementId);
    }

    @Nonnull
    @Override
    public String getTitle() {
        return getName() + " Package";
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
