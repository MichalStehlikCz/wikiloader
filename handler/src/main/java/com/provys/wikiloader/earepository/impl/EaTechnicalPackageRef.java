package com.provys.wikiloader.earepository.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("squid:S2160") // package is only cached -> no need to include it in equals
class EaTechnicalPackageRef extends EaDefaultElementRef {

    @Nullable
    private EaTechnicalPackage technicalPackage;

    EaTechnicalPackageRef(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name,
                        @Nullable String alias, int treePos, int elementId) {
        super(repository, parent, name, alias, "ApplicationComponent", "ArchiMate_ApplicationComponent",
                treePos, elementId,false);
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