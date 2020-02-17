package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

abstract class EaSysFuncAbstract<R extends EaSysFuncAbstractRef, E extends EaSysFuncRef>
        extends EaParentBase<R, EaDiagramRef, E>
        implements EaElementRef {

    EaSysFuncAbstract(R objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
                      @Nullable List<E> elements) {
        super(objectRef, notes, diagrams, elements);
    }

    @Nonnull
    public Optional<String> getDescriptionTopicName() {
        return getObjectRef().getDescriptionTopicName();
    }

    @Nonnull
    public Optional<String> getDescriptionTopicId() {
        return getObjectRef().getDescriptionTopicId();
    }

    @Override
    public int getElementId() {
        return getObjectRef().getElementId();
    }
}
