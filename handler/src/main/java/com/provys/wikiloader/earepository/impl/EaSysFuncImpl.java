package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

class EaSysFuncImpl extends EaLeafElementBase<EaSysFuncImplRef> {

    @Nonnull
    private final List<EaUGTopicRef> usedIn;

    EaSysFuncImpl(EaSysFuncImplRef objectRef, @Nullable String notes, List<EaDiagramRef> diagrams,
                  List<EaUGTopicRef> usedIn) {
        super(objectRef, notes, diagrams);
        this.usedIn = List.copyOf(usedIn);
    }

    @Nonnull
    List<EaUGTopicRef> getUsedIn() {
        return usedIn;
    }

    @Nonnull
    Optional<String> getDescriptionTopicName() {
        return getObjectRef().getDescriptionTopicName();
    }

    @Nonnull
    Optional<String> getDescriptionTopicId() {
        return getObjectRef().getDescriptionTopicId();
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaSysFuncImplExporter(this, wikiClient);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof EaSysFuncImpl)) return false;
        if (!super.equals(o)) return false;
        EaSysFuncImpl eaReport = (EaSysFuncImpl) o;
        return getUsedIn().equals(eaReport.getUsedIn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getUsedIn());
    }

    @Override
    public String toString() {
        return "EaSysFunc{" +
                "usedIn=" + usedIn +
                "} " + super.toString();
    }
}
