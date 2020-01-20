package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *  Base class for description of element in functional model, used to export topics to user guide. Elements are
 *  referenced by technical packages they are included in / relevant for.
 * @param <T> is reference class this object represents
 */
abstract class EaUGTopicBase<R extends EaUGTopicRefBase<R, T>, T extends EaUGTopicBase<R, T>>
        extends EaLeafElementBase<R> implements EaUGTopic {

    @Nonnull
    private final List<EaTechnicalPackageRef> includedIn;

    EaUGTopicBase(R objectRef, @Nullable String notes, List<EaDiagramRef> diagrams,
                  List<EaTechnicalPackageRef> includedIn) {
        super(objectRef, notes, diagrams);
        this.includedIn = Objects.requireNonNull(includedIn);
    }

    /**
     * @return list of technical packages this topic is included in
     */
    @Override
    @Nonnull
    public List<EaTechnicalPackageRef> getIncludedIn() {
        return includedIn;
    }

    @Override
    @Nonnull
    public Optional<String> getUserGuideTopicName() {
        return getObjectRef().getUserGuideTopicName();
    }

    @Override
    @Nonnull
    public Optional<String> getUserGuideTopicId() {
        return getObjectRef().getUserGuideTopicId();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof EaUGTopicBase)) return false;
        if (!super.equals(o)) return false;
        EaUGTopicBase<?, ?> that = (EaUGTopicBase<?, ?>) o;
        return getIncludedIn().equals(that.getIncludedIn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIncludedIn());
    }
}
