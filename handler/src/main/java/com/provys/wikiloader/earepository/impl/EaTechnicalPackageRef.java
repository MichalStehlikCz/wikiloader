package com.provys.wikiloader.earepository.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("squid:S2160") // package is only cached -> no need to include it in equals
class EaTechnicalPackageRef extends EaNamespaceElementRef implements EaItemRef {

    @Nullable
    private EaTechnicalPackage technicalPackage;

    EaTechnicalPackageRef(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name,
                        @Nullable String alias, int treePos, int elementId) {
        super(repository, parent, name, alias, "ApplicationComponent", "ArchiMate_ApplicationComponent",
                treePos, elementId);
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

    @Nonnull
    @Override
    public String getTitleInGroup() {
        return getName();
    }

    @Override
    public String toString() {
        return "EaTechnicalPackageRef{" +((technicalPackage == null) ? "not loaded" : "loaded") + "} " +
                super.toString();
    }
}