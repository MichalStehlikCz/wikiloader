package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObject;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

class EaNamespaceElementRef extends EaElementRefBase {

    private static final Logger LOG = LogManager.getLogger(EaNamespaceElementRef.class);

    EaNamespaceElementRef(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name,
                          @Nullable String alias, String type, @Nullable String stereotype, int treePos, int elementId)
    {
        super(repository, parent, name, alias, type, stereotype, treePos, elementId);
    }

    @Override
    @Nonnull
    public EaObject getObject() {
        return getRepository().getLoader().loadNamespaceElement(this);
    }

    @Override
    @Nonnull
    public Optional<String> getTopicId() {
        var alias = getAlias();
        if (alias.isEmpty()) {
            return Optional.empty();
        }
        return getParent()
                .map(EaObjectRef::getNamespace) // get namespace from parent
                .orElse(Optional.of("")) // if no parent, use "" as prefix
                .map(ns -> ns + ":" + alias.get() + ":start"); // append this topic's alias, acting as namespace
    }

    @Override
    @Nonnull
    public Optional<String> getNamespace() {
        if (getAlias().isEmpty()) {
            return Optional.empty();
        }
        return getParent()
                    .map(EaObjectRef::getNamespace) // get namespace from parent
                    .orElse(Optional.of("")) // if no parent, use "" as prefix
                    .map(ns -> ns + ":" + getAlias().get()); // append this topic's alias, acting as namespace
    }

    @Override
    public void appendNamespace(StringBuilder builder, boolean trailingColon) {
        var alias = getAlias();
        if (alias.isEmpty()) {
            throw new InternalException(LOG,
                    "Request to append namespace for element that does not translate to namespace " + this);
        }
        getParent().ifPresent(parent -> parent.appendNamespace(builder, true));
        builder.append(alias.get());
        if (trailingColon) {
            builder.append(":");
        }
    }

    @Override
    public void appendParentLinkNoCheck(StringBuilder builder, boolean leadingDot) {
        if (leadingDot) {
            builder.append('.');
        }
        getAlias().ifPresent(builder::append);
        builder.append(":");
    }

    @Override
    public String toString() {
        return "EaNamespaceElementRef{} " + super.toString();
    }
}
