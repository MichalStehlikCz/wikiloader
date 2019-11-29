package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Represents leaf element exported as single topic (not namespace). Might potentially contain diagrams, that are
 * exported to parent namespace using this element's alias + . + their alias
 */
class EaLeafElementRef extends EaLeafElementRefBase {

    private static final Logger LOG = LogManager.getLogger(EaLeafElementRef.class);

    EaLeafElementRef(EaRepositoryImpl repository, EaObjectRefBase parent, String name, String alias, String type, String stereotype, int treePos, int elementId) {
        super(repository, parent, name, alias, type, stereotype, treePos, elementId);
    }

    @Override
    @Nonnull
    public EaLeafElement getObject() {
        return getRepository().getLoader().loadLeafElement(this);
    }

}
