package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

/**
 * Class represents reference to system function (concrete report or concrete interface) in Enterprise Architect.
 *
 * Is based on leaf element and adds description topic.
 */
class EaSysFuncImplRef extends EaCachedLeafElementRef<EaSysFuncImplRef, EaSysFuncImpl> implements EaSysFuncRef {

    static final String DESCRIPTION_NAME = "description";

    EaSysFuncImplRef(EaRepositoryImpl repository, EaObjectRef parent, String name, @Nullable String alias, String type,
                     @Nullable String stereotype, int treePos, int elementId) {
        super(repository, parent, name, alias, type, stereotype, treePos, elementId);
    }

    @Override
    protected synchronized void loadObject() {
        if (object == null) {
            object = getRepository().getLoader().loadSysFunc(this);
        }
    }

    @Override
    @Nonnull
    public Optional<String> getDescriptionTopicName() {
        var alias = getAlias();
        if (alias.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(alias.get() + "." + DESCRIPTION_NAME);
    }

    @Override
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
    public void appendPages(Collection<? super String> pages) {
        super.appendPages(pages);
        getDescriptionTopicName().ifPresent(pages::add);
    }

    @Override
    public String toString() {
        return "EaSysFuncRef{} " + super.toString();
    }
}
