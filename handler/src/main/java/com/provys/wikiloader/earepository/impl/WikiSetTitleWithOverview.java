package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

class WikiSetTitleWithOverview extends WikiSetTitle {

    private final EaObjectRef overview;

    WikiSetTitleWithOverview(String title, EaObjectRef overview, List<WikiSetObjectBase> children) {
        super(title, children);
        this.overview = overview;
    }

    @Nonnull
    @Override
    Optional<String> getWikiText(Function<EaObjectRef, Optional<String>> linkFunction) {
        return Optional.of(
                linkFunction.apply(overview)
                        .map(overviewTopicId -> "[[" + overviewTopicId + "|" + getTitle() + "]]")
                        .orElse(getTitle()));
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof WikiSetTitleWithOverview)) return false;
        if (!super.equals(o)) return false;
        WikiSetTitleWithOverview that = (WikiSetTitleWithOverview) o;
        return Objects.equals(overview, that.overview);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), overview);
    }

    @Override
    public String toString() {
        return "WikiSetTitleWithOverview{" +
                "overview=" + overview +
                "} " + super.toString();
    }
}