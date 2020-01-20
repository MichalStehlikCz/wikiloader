package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

abstract class EaLeafElementRefBase<T extends EaLeafElementRefBase<T>> extends EaElementRefBase {

    private static final Logger LOG = LogManager.getLogger(EaLeafElementRefBase.class);

    EaLeafElementRefBase(EaRepositoryImpl repository, EaObjectRef parent, String name, @Nullable String alias,
                         String type, @Nullable String stereotype, int treePos, int elementId) {
        super(repository, parent, name, alias, type, stereotype, treePos, elementId);
    }

    @Override
    @Nonnull
    public abstract EaLeafElementBase<T> getObject();

    @Override
    public boolean isTopic() {
        if (getAlias().isEmpty()
                && getStereotype().filter(stereotype -> stereotype.equals("ArchiMate_BusinessEvent")).isPresent()
                && (getName().equals("START") || getName().equals("END"))) {
            // only debug for start / end of process
            LOG.debug("Skip element {} with empty alias", this::getEaDesc);
            return false;
        }
        return super.isTopic();
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
                .map(ns -> ns + ":" + alias.get()); // append this topic's alias, acting as namespace
    }

    @Override
    @Nonnull
    public Optional<String> getNamespace() {
        return Optional.empty();
    }

    @Override
    public void appendNamespace(StringBuilder builder, boolean trailingColon) {
        throw new InternalException(LOG,
                "Request to append namespace for element that does not translate to namespace " + this);
    }

    @Override
    public void appendParentLinkNoCheck(StringBuilder builder, boolean leadingDot) {
        getAlias().ifPresent(builder::append);
    }

    @Override
    public void appendPages(Collection<String> pages) {
        super.appendPages(pages);
        /* Diagrams are placed in the same namespace as this element... */
        for (var diagram : getObject().getDiagrams()) {
            diagram.appendPages(pages);
        }
    }
}
