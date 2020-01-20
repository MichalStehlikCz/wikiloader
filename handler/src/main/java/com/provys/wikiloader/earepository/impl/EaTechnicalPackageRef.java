package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("squid:S2160") // package is only cached -> no need to include it in equals
class EaTechnicalPackageRef extends EaNamespaceElementRef implements EaItemRef {

    @Nullable
    private EaTechnicalPackage technicalPackage;

    EaTechnicalPackageRef(EaRepositoryImpl repository, @Nullable EaObjectRef parent, String name,
                          @Nullable String alias, int treePos, int elementId) {
        super(repository, parent, name, alias, "ApplicationComponent", "ArchiMate_ApplicationComponent",
                treePos, elementId);
    }

    @Nonnull
    @Override
    public String getTitle() {
        return getName() + " Technical Package";
    }

    private synchronized void loadObject() {
        if (technicalPackage == null) {
            technicalPackage = getRepository().getLoader().loadTechnicalPackage(this);
        }
    }

    @Override
    @Nonnull
    public EaTechnicalPackage getObject() {
        if (technicalPackage == null) {
            loadObject();
        }
        assert (technicalPackage != null);
        return technicalPackage;
    }

    @Override
    public String toString() {
        return "EaTechnicalPackageRef{" +((technicalPackage == null) ? "not loaded" : "loaded") + "} " +
                super.toString();
    }
}