package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObject;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Represents box-like elements (Boundary, Grouping).
 * These elements are not exported, but if their alias is specified, it is considered link to package or element with
 * specified path - often used to mark area corresponding to package in diagram
 */
class EaGroupRef extends EaElementRefBase {

    private static final Logger LOG = LogManager.getLogger(EaGroupRef.class);

    EaGroupRef(EaRepositoryImpl repository, @Nullable EaObjectRef parent, String name, @Nullable String alias,
               String type, int treePos, int elementId) {
        super(repository, parent, name, alias, type, null, treePos, elementId);
    }

    @Override
    @Nonnull
    public EaObject getObject() {
        return new EaGroup(this);
    }

    @Override
    public boolean isIgnoredType() {
        return true;
    }

    @Override
    @Nonnull
    public Optional<String> getTopicId() {
        return Optional.empty(); // topic is not generated from boundary
    }

    @Override
    @Nonnull
    public Optional<String> getNamespace() {
        return Optional.empty(); // namespace is not exported from boundary
    }

    @Override
    @SuppressWarnings("squid:S3655") // sonar doesn't recognise Optional.isEmpty
    public boolean hasLink() {
        if (getAlias().isEmpty()) {
            LOG.debug("{} not exported - alias is missing", this::getEaDesc);
            return false;
        }
        if (getAlias().get().charAt(0) == ':') {
            // if alias contains full path, we can export it regardless of parent...
            return true;
        }
        return getParent().map(EaObjectRef::hasLink).orElse(true);
    }

    @Override
    public void appendLinkNoCheck(StringBuilder builder) {
        var alias = getAlias().orElseThrow();
        if (alias.charAt(0) != ':') {
            super.appendLinkNoCheck(builder);
        } else {
            builder.append(alias).append(":");
        }
    }

    @Override
    void appendParentLinkNoCheck(StringBuilder builder, boolean leadingDot) {
        var alias = getAlias().orElseThrow();
        if (leadingDot && (alias.charAt(0) != ':')) {
            builder.append(".");
        }
        builder.append(alias).append(":");
    }

    @Override
    public void appendNamespace(StringBuilder builder, boolean trailingColon) {
        throw new InternalException("Cannot append namespace - boundary " + this +
                "does not correspond to namespace");
    }

    @Override
    public String toString() {
        return "EaBoundaryRef{} " + super.toString();
    }
}
