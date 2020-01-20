package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;

/**
 * Represents leaf element exported as single topic (not namespace). Might potentially contain diagrams, that are
 * exported to parent namespace using this element's alias + . + their alias
 */
class EaLeafElementRef extends EaLeafElementRefBase<EaLeafElementRef> {

    EaLeafElementRef(EaRepositoryImpl repository, EaObjectRef parent, String name, String alias, String type, String stereotype, int treePos, int elementId) {
        super(repository, parent, name, alias, type, stereotype, treePos, elementId);
    }

    @Override
    @Nonnull
    public EaLeafElement getObject() {
        return getRepository().getLoader().loadLeafElement(this);
    }

}
