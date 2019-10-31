package com.provys.wikiloader.earepository.impl;

import javax.annotation.Nullable;

@SuppressWarnings("squid:S2160") // packageGroup is only cached -> no need to include it in equals
class EaPackageGroupRef extends EaDefaultPackageRef {

    private EaPackageGroup packageGroup;

    EaPackageGroupRef(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name,
                      @Nullable String alias, @Nullable String stereotype, int treePos, int packageId) {
        super(repository, parent, name, alias, stereotype, treePos, packageId);
    }

    private synchronized void loadObject() {
        if (packageGroup == null) {
            packageGroup = getRepository().getLoader().loadPackageGroup(this);
        }
    }

    @Override
    public EaPackageGroup getObject() {
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
