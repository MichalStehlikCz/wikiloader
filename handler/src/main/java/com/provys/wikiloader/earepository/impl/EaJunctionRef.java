package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ArchiMate Junction is basically generic leaf element, but it is often intentionally not exported to wiki and we do
 * not want warnings because of missing alias
 */
class EaJunctionRef extends EaLeafElementRef {

    private static final Logger LOG = LogManager.getLogger(EaJunctionRef.class);

    EaJunctionRef(EaRepositoryImpl repository, EaObjectRef parent, String name, String alias, int treePos,
                  int elementId) {
        super(repository, parent, name, alias, "Junction", "ArchiMate_Junction", treePos, elementId);
    }

    @Override
    public boolean isTopic() {
        if (isIgnoredType()) {
            LOG.debug("{} is not exportable type", this::getEaDesc);
            return false;
        }
        if (getParent().filter(EaObjectRef::isTopic).isEmpty()) {
            LOG.debug("{} not exported - parent not exported", this::getEaDesc);
            return false;
        }
        if (getAlias().isEmpty()) {
            // only debug instead of warning in ancestor
            LOG.debug("{} not exported - alias missing", this::getEaDesc);
            return false;
        }
        return true;
    }
}
