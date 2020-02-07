package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class EaReportAbstractRef extends EaNamespaceElementRef
        implements EaReportRef {

    public static final String DESCRIPTION_NAME = "description";

    EaReportAbstractRef(EaRepositoryImpl repository, @Nullable EaObjectRef parent, String name,
                      @Nullable String alias, int treePos, int elementId) {
        super(repository, parent, name, alias, "Representation", "ArchiMate_Representation", treePos,
                elementId);
    }

    @Nonnull
    public Optional<String> getDescriptionTopicName() {
        return Optional.of(DESCRIPTION_NAME);
    }

    @Nonnull
    public Optional<String> getDescriptionTopicId() {
        return getNamespace().map(ns -> ns + ":" + DESCRIPTION_NAME);
    }

    @Override
    @Nonnull
    public EaReportAbstract getObject() {
        return getRepository().getLoader().loadReportAbstract(this);
    }
}
