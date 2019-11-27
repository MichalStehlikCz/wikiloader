package com.provys.wikiloader.earepository.impl;

import javax.annotation.Nullable;

class EaTechnicalPackageGroupRef extends EaItemGroupRef<EaTechnicalPackageRef, EaTechnicalPackageGroupRef,
        EaTechnicalPackageGroup> {

    EaTechnicalPackageGroupRef(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name,
                               @Nullable String alias, @Nullable String stereotype, int treePos, int packageId) {
        super(repository, parent, name, alias, stereotype, treePos, packageId);
    }

    @Override
    protected synchronized void loadObject() {
        if (packageGroup == null) {
            packageGroup = getRepository().getLoader().loadTechnicalPackageGroup(this);
        }
    }
}
