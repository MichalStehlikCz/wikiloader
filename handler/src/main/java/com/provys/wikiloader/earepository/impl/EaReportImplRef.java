package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

class EaReportImplRef extends EaCachedLeafElementRef<EaReportImplRef, EaReportImpl> implements EaReportRef {

    static final String DESCRIPTION_NAME = "description";

    EaReportImplRef(EaRepositoryImpl repository, EaObjectRef parent, String name, @Nullable String alias,
                     int treePos, int elementId) {
        super(repository, parent, name, alias, "Representation", "ArchiMate_Representation", treePos,
                elementId);
    }

    @Nonnull
    @Override
    public String getTitle() {
        return "Report " + getPlainName();
    }

    @Nonnull
    public Optional<String> getDescriptionTopicName() {
        var alias = getAlias();
        if (alias.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(alias.get() + "." + DESCRIPTION_NAME);
    }

    @Nonnull
    public Optional<String> getDescriptionTopicId() {
        return getDescriptionTopicName()
                .flatMap(topicName ->
                        getParent()
                                .map(EaObjectRef::getNamespace) // get namespace from parent
                                .orElseThrow(() -> new InternalException("User guide topic should have parent")) // if no parent, use "" as prefix)
                                .map(ns -> ns + ":" + topicName));
    }

    @Override
    protected synchronized void loadObject() {
        if (object == null) {
            object = getRepository().getLoader().loadReportImpl(this);
        }
    }

    @Override
    public void appendPages(Collection<String> pages) {
        super.appendPages(pages);
        getDescriptionTopicName().ifPresent(pages::add);
    }
}
