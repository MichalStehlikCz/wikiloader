package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObject;
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

    EaBoundaryRef(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name, @Nullable String alias,
                  int treePos, int elementId) {
        super(repository, parent, name, alias, "Boundary", treePos, elementId);
    }

    @Override
    public EaObject getObject() {
        return new EaBoundary(this);
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
    public void appendLink(StringBuilder builder) {
        if (!hasLink()) {
            throw new InternalException(LOG, "Cannot append link - boundary not exported " + this);
        }
        var alias = getAlias().orElseThrow();
        if (alias.charAt(0) != ':') {
            // in case alias starts with :, it contains full path and we cannot prefix it with parent namespace
            getParent().ifPresent(parent -> parent.appendNamespace(builder, true));
        }
        builder.append(alias).append(":");
    }

    @Override
    void appendParentLink(StringBuilder builder, boolean leadingDot) {
        if (!hasLink()) {
            throw new InternalException(LOG, "Cannot append link - boundary not exported " + this);
        }
        var alias = getAlias().orElseThrow();
        if (leadingDot && (alias.charAt(0) != ':')) {
            builder.append(".");
        }
        builder.append(alias).append(":");
    }

    @Override
    public void appendNamespace(StringBuilder builder, boolean trailingColon) {
        throw new InternalException(LOG, "Cannot append namespace - boundary " + this +
                "does not correspond to namespace");
    }

    @Override
    public String toString() {
        return "EaBoundaryRef{} " + super.toString();
    }
}
