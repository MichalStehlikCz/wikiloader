package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

abstract class EaObjectRefBase implements EaObjectRef {

    @Nullable
    private final EaObjectRefBase parent;
    @Nonnull
    private final String name;
    @Nullable
    private final String alias;
    @Nullable
    private final String stereotype;
    private final int treePos;

    EaObjectRefBase(@Nullable EaObjectRefBase parent, String name, @Nullable String alias, @Nullable String stereotype,
                    int treePos) {
        this.parent = parent;
        this.name = Objects.requireNonNull(name);
        this.alias = ((alias == null) || alias.isEmpty()) ? null : alias;
        this.stereotype = ((stereotype == null) || stereotype.isEmpty()) ? null : stereotype;
        this.treePos = treePos;
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

    @Override
    @Nonnull
    public Optional<String> getAlias() {
        return Optional.ofNullable(alias);
    }

    @Nonnull
    @Override
    public Optional<String> getStereotype() {
        return Optional.ofNullable(stereotype);
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
        appendLink(builder);
        return Optional.of(builder.toString());
    }

    @Override
    public void appendLink(StringBuilder builder) {
        builder.append(":");
        if (parent != null) {
            parent.appendNamespace(builder);
        }
        appendParentLink(builder);
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
        return treePos == eaObject.treePos &&
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
