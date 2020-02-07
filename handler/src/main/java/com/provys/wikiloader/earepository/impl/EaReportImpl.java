package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class EaReportImpl extends EaLeafElementBase<EaReportImplRef> {

    @Nonnull
    private final List<EaUGTopicRef> usedIn;

    EaReportImpl(EaReportImplRef objectRef, @Nullable String notes, List<EaDiagramRef> diagrams,
                  List<EaUGTopicRef> usedIn) {
        super(objectRef, notes, diagrams);
        this.usedIn = List.copyOf(usedIn);
    }

    @Nonnull
    public List<EaUGTopicRef> getUsedIn() {
        return usedIn;
    }

    @Nonnull
    public Optional<String> getDescriptionTopicName() {
        return getObjectRef().getDescriptionTopicName();
    }

    @Nonnull
    public Optional<String> getDescriptionTopicId() {
        return getObjectRef().getDescriptionTopicId();
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaReportImplExporter(this, wikiClient);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof EaReportImpl)) return false;
        if (!super.equals(o)) return false;
        EaReportImpl eaReport = (EaReportImpl) o;
        return getUsedIn().equals(eaReport.getUsedIn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getUsedIn());
    }
}
