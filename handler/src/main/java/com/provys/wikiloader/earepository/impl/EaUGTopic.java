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
@SuppressWarnings("squid:S2160") // we do not need to include links to technical packages in comparison - they should
                                 // not differ for given element...
abstract class EaUGTopic<R extends EaUGTopicRef<R, T>, T extends EaUGTopic<R, T>> extends EaLeafElementBase<R> {

    private final List<EaTechnicalPackageRef> includedIn;

    EaUGTopic(R objectRef, @Nullable String notes, List<EaDiagramRef> diagrams, List<EaTechnicalPackageRef> includedIn)
    {
        super(objectRef, notes, diagrams);
        this.includedIn = Objects.requireNonNull(includedIn);
    }

    /**
     * @return list of technical packages this topic is included in
     */
    public List<EaTechnicalPackageRef> getIncludedIn() {
        return includedIn;
    }

    @Nonnull
    Optional<String> getUserGuideTopicName() {
        return getObjectRef().getUserGuideTopicName();
    }

    @Nonnull
    Optional<String> getUserGuideTopicId() {
        return getObjectRef().getUserGuideTopicId();
    }
}
