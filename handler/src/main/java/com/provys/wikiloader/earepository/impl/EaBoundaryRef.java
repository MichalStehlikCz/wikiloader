package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Represents Boundary element.
 * Boundary element is not exported, but if its alias is specified, it is considered link to package or element with
 * specified path - often used to mark area corresponding to package in diagram
 */
class EaBoundaryRef extends EaElementRefBase {

    private static final Logger LOG = LogManager.getLogger(EaBoundaryRef.class);

    EaBoundaryRef(@Nullable EaObjectRefBase parent, String name, @Nullable String alias, int treePos, int elementId) {
        super(parent, name, alias, "Boundary", treePos, elementId);
    }

    @Override
    public boolean isTopic() {
        LOG.debug("Boundary {} is not exported", this::getName);
        return false;
    }

    @Override
    public Optional<String> getTopicId() {
        return Optional.empty(); // topic is not generated from boundary
    }

    @Override
    public Optional<String> getNamespace() {
        return Optional.empty(); // namespace is not exported from boundary
    }

    @Override
    public boolean hasLink() {
        if (getAlias().isEmpty()) {
            return false;
        }
        return getParent().map(EaObjectRef::hasLink).orElse(true);
    }

    @Override
    public void appendNamespace(StringBuilder builder) {
        throw new InternalException(LOG, "Cannot append namespace - diagram reference not exported " + this);
    }

    @Override
    public void appendParentLink(StringBuilder builder) {
        if (!hasLink()) {
            throw new InternalException(LOG, "Cannot append link - diagram not exported " + this);
        }
        builder.append(getAlias().orElseThrow());
    }

    @Override
    public String toString() {
        return "EaBoundaryRef{} " + super.toString();
    }
}
