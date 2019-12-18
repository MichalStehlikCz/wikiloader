package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

abstract class EaObjectRefBase implements EaObjectRef {

    private static final Logger LOG = LogManager.getLogger(EaObjectRefBase.class);

    @Nonnull
    private final EaRepositoryImpl repository;
    @Nullable
    private final EaObjectRefBase parent;
    @Nonnull
    private final String name;
    @Nullable
    private final String alias;
    @Nonnull
    private final String type;
    @Nullable
    private final String stereotype;
    private final int treePos;

    EaObjectRefBase(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name, @Nullable String alias,
                    String type, @Nullable String stereotype, int treePos) {
        this.repository = Objects.requireNonNull(repository);
        this.parent = parent;
        this.name = Objects.requireNonNull(name);
        this.alias = ((alias == null) || alias.isEmpty()) ? null : alias.toLowerCase();
        this.type = Objects.requireNonNull(type);
        this.stereotype = ((stereotype == null) || stereotype.isEmpty()) ? null : stereotype;
        this.treePos = treePos;
    }

    @Nonnull
    @Override
    public EaRepositoryImpl getRepository() {
        return repository;
    }

    @Override
    @Nonnull
    public Optional<EaObjectRef> getParent() {
        return Optional.ofNullable(parent);
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return getName();
    }

    @Nonnull
    @Override
    public String getShortTitle() {
        return getName();
    }

    @Override
    @Nonnull
    public Optional<String> getAlias() {
        return Optional.ofNullable(alias);
    }

    @Nonnull
    @Override
    public String getType() {
        return type;
    }

    @Nonnull
    @Override
    public Optional<String> getStereotype() {
        return Optional.ofNullable(stereotype);
    }

    @Nonnull
    public String getEaDesc() {
        return type + ((stereotype == null) ? "" : " " + stereotype) + " " + name;
    }

    @Override
    public boolean isIgnoredType() {
        return false;
    }

    @Override
    public boolean isTopic() {
        if (isIgnoredType()) {
            LOG.debug("{} is not exportable type", this::getEaDesc);
            return false;
        }
        if ((parent != null) && (!parent.isTopic())) {
            LOG.debug("{} not exported - parent not exported", this::getEaDesc);
            return false;
        }
        if (alias == null) {
            LOG.info("{} not exported - alias missing", this::getEaDesc);
            return false;
        }
        return true;
    }

    @Override
    public boolean hasLink() {
        return isTopic();
    }

    @Nonnull
    @Override
    public Optional<String> getParentLink() {
        if (!hasLink()) {
            return Optional.empty();
        }
        var builder = new StringBuilder();
        appendParentLink(builder);
        return Optional.of(builder.toString());
    }

    void appendLinkNoCheck(StringBuilder builder) {
        builder.append(":");
        if (parent != null) {
            parent.appendNamespace(builder, true);
        }
        appendParentLinkNoCheck(builder, false);
    }

    @Override
    public void appendLink(StringBuilder builder) {
        if (!hasLink()) {
            throw new InternalException(LOG, "Cannot append link - boundary not exported " + this);
        }
        appendLinkNoCheck(builder);
    }

    abstract void appendParentLinkNoCheck(StringBuilder builder, boolean leadingDot);

    @Override
    public void appendParentLink(StringBuilder builder) {
        if (!hasLink()) {
            throw new InternalException(LOG, "Cannot append link - boundary not exported " + this);
        }
        appendParentLinkNoCheck(builder, true);
    }

    public void appendPages(Collection<String> pages) {
        getParentLink().ifPresent(pages::add);
    }

    /**
     * Retrieve position of given items, defined as list of tree positions, starting from root package (model).
     * At the moment, list is modifiable; it is strongly recommended not to rely on this behaviour outside this method's
     * implementation, it is not part of interface contract.
     *
     * @return list with tree positions, starting from root package to given object
     */
    @Override
    @Nonnull
    public List<Integer> getPos() {
        List<Integer> result;
        if (parent != null) {
            result = parent.getPos();
        } else {
            result = new ArrayList<>(10);
        }
        result.add(treePos);
        return result;
    }

    @Override
    public int compareTo(EaObjectRef o) {
        if (equals(o)) {
            return 0;
        }
        var pos = getPos();
        var otherPos = o.getPos();
        for (var i = 0; (i < pos.size()) && (i<otherPos.size()); i++) {
            if (pos.get(i) < otherPos.get(i)) {
                return -1;
            } else if (pos.get(i) > otherPos.get(i)) {
                return 1;
            }
        }
        return Integer.compare(pos.size(), otherPos.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EaObjectRefBase eaObject = (EaObjectRefBase) o;
        return repository == eaObject.repository &&
                treePos == eaObject.treePos &&
                Objects.equals(parent, eaObject.parent) &&
                (name.equals(eaObject.name)) &&
                Objects.equals(alias, eaObject.alias) &&
                Objects.equals(stereotype, eaObject.stereotype);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, name, alias, treePos, stereotype);
    }

    @Override
    public String toString() {
        return "EaObjectImpl{" +
                "name='" + name + '\'' +
                "alias='" + alias + '\'' +
                ", parent=" + parent +
                '}';
    }
}
