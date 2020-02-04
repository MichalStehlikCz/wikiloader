package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

class EaDataObjectRef extends EaElementRefBase {

    private static final Logger LOG = LogManager.getLogger(EaDataObjectRef.class);

    EaDataObjectRef(EaRepositoryImpl repository, @Nullable EaObjectRef parent, String name, @Nullable String alias,
                    int treePos, int elementId) {
        super(repository, parent, name, alias, "DataObject", "ArchiMate_DataObject", treePos,
                elementId);
    }

    @Override
    @Nonnull
    public EaDataObject getObject() {
        return getRepository().getLoader().loadDataObject(this);
    }

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
        throw new InternalException(
                "Request to append namespace for element that does not translate to namespace " + this);
    }

    @Override
    public void appendParentLinkNoCheck(StringBuilder builder, boolean leadingDot) {
        getAlias().ifPresent(builder::append);
    }
}
