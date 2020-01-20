package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Extends default element reference with capability to cache created object
 *
 * @param <T> is type of object associated with this object reference
 */
@SuppressWarnings("squid:S2160") // object is only cached -> no need to include it in equals
abstract class EaCachedLeafElementRef<R extends EaCachedLeafElementRef<R, T>, T extends EaLeafElementBase<R>>
        extends EaLeafElementRefBase<R> {

    @Nullable
    protected T object;

    EaCachedLeafElementRef(EaRepositoryImpl repository, EaObjectRef parent, String name, @Nullable String alias,
                           String type, @Nullable String stereotype, int treePos, int elementId) {
        super(repository, parent, name, alias, type, stereotype, treePos, elementId);
    }

    protected abstract void loadObject();

    @Override
    @Nonnull
    public T getObject() {
        if (object == null) {
            loadObject();
        }
        assert (object != null);
        return object;
    }
}
