package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

class EaDefaultElementRef extends EaElementRefBase {

    private static final Logger LOG = LogManager.getLogger(EaDefaultElementRef.class);

    private final boolean leaf;

    EaDefaultElementRef(@Nullable EaObjectRefBase parent, String name, @Nullable String alias,
                        @Nullable String stereotype, int treePos, int elementId, boolean leaf) {
        super(parent, name, alias, stereotype, treePos, elementId);
        this.leaf = leaf;
    }

    boolean isLeaf() {
        return leaf;
    }

    @Override
    public boolean isTopic() {
        if (getAlias().isEmpty()) {
            if (getName().equals("START") || getName().equals("END")) {
                LOG.debug("Skip element {} {} with empty alias", this::getStereotype, this::getName);
            } else {
                LOG.info("Skip element {} {} with empty alias", this::getStereotype, this::getName);
            }
            return false;
        }
        return getParent().map(EaObjectRef::hasLink).orElse(true);
    }

    @Override
    public Optional<String> getTopicId() {
        if (getAlias().isEmpty()) {
            return Optional.empty();
        }
        @SuppressWarnings("squid:S3655") // sonar does not recognise Optional.isEmpty...
        var name = leaf ? getAlias().get() : getAlias().get() + ":start";
        return getParent()
                .map(EaObjectRef::getNamespace) // get namespace from parent
                .orElse(Optional.of("")) // if no parent, use "" as prefix
                .map(ns -> ns + name); // append this topic's alias, acting as namespace
    }

    @Override
    public Optional<String> getNamespace() {
        if (leaf || getAlias().isEmpty()) {
            return Optional.empty();
        }
        return getParent()
                    .map(EaObjectRef::getNamespace) // get namespace from parent
                    .orElse(Optional.of("")) // if no parent, use "" as prefix
                    .map(ns -> ns + getAlias().get() + ":"); // append this topic's alias, acting as namespace
    }

    @Override
    @SuppressWarnings("squid:S3655") // sonar does not recognise Optional.isEmpty...
    public void appendNamespace(StringBuilder builder) {
        if (leaf || getAlias().isEmpty()) {
            throw new InternalException(LOG,
                    "Request to append namespace for element that does not translate to namespace " + this);
        }
        getParent().ifPresent(parent -> parent.appendNamespace(builder));
        builder.append(getAlias().get()).append(":");
    }

    @Override
    public void appendParentLink(StringBuilder builder) {
        if (!hasLink()) {
            throw new InternalException(LOG, "Cannot append link - element not exported to wiki");
        }
        getAlias().ifPresent(builder::append);
        if (!leaf) {
            builder.append(":");
        }
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaDefaultElementRef that = (EaDefaultElementRef) o;
        return leaf == that.leaf;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), leaf);
    }

    @Override
    public String toString() {
        return "EaDefaultElementRef{" +
                "leaf=" + leaf +
                "} " + super.toString();
    }
}
