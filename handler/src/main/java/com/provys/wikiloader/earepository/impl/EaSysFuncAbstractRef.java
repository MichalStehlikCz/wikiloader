package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

class EaSysFuncAbstractRef extends EaNamespaceElementRef implements EaSysFuncRef {

    static final String DESCRIPTION_NAME = "description";

    public EaSysFuncAbstractRef(EaRepositoryImpl repository, @Nullable EaObjectRef parent, String name,
                                @Nullable String alias, String type, String stereotype, int treePos, int elementId) {
        super(repository, parent, name, alias, type, stereotype, treePos, elementId);
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
    public String toString() {
        return "EaSysFuncAbstractRef{} " + super.toString();
    }
}
