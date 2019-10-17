package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EaObjectImpl implements EaObject {

    @Nullable
    private final EaObjectImpl parent;
    @Nonnull
    private final String name;
    @Nullable
    private final String alias;
    @Nullable
    private final String stereotype;
    private final int treePos;

    EaObjectImpl(@Nullable EaObjectImpl parent, String name, @Nullable String alias, @Nullable String stereotype,
                 int treePos) {
        this.parent = parent;
        this.name = Objects.requireNonNull(name);
        this.alias = ((alias == null) || alias.isEmpty()) ? null : alias;
        this.stereotype = ((stereotype == null) || stereotype.isEmpty()) ? null : stereotype;
        this.treePos = treePos;
    }

    @Override
    @Nonnull
    public Optional<EaObject> getParent() {
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
    public int compareTo(EaObject o) {
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
        EaObjectImpl eaObject = (EaObjectImpl) o;
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
