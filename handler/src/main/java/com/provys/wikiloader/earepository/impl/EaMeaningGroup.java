package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EaMeaningGroup extends EaParentBase<EaMeaningGroupRef, EaDiagramRef, EaMeaningRef>
        implements EaUGTopic {

    @Nonnull
    private final List<EaTechnicalPackageRef> includedIn;

    EaMeaningGroup(EaMeaningGroupRef objectRef, @Nullable String notes, @Nullable List<EaDiagramRef> diagrams,
                   @Nullable List<EaMeaningRef> elements, List<EaTechnicalPackageRef> includedIn) {
        super(objectRef, notes, diagrams, elements);
        this.includedIn = Objects.requireNonNull(includedIn);
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return null;
    }

    @Nonnull
    @Override
    public List<EaTechnicalPackageRef> getIncludedIn() {
        return includedIn;
    }

    @Nonnull
    @Override
    public Optional<String> getUserGuideTopicName() {
        return getObjectRef().getUserGuideTopicName();
    }

    @Nonnull
    @Override
    public Optional<String> getUserGuideTopicId() {
        return getObjectRef().getUserGuideTopicId();
    }

    @Override
    public int getElementId() {
        return getObjectRef().getElementId();
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof EaMeaningGroup)) return false;
        if (!super.equals(o)) return false;
        EaMeaningGroup that = (EaMeaningGroup) o;
        return getIncludedIn().equals(that.getIncludedIn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getIncludedIn());
    }
}
