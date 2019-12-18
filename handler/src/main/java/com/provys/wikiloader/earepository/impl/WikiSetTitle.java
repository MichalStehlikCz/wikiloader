package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class WikiSetTitle extends WikiSetObjectBase {

    @Nonnull
    private final String title;

    WikiSetTitle(String title, List<WikiSetObjectBase> children) {
        super(children);
        this.title = Objects.requireNonNull(title);
    }

    @Nonnull
    @Override
    public String getTitle() {
        return title;
    }

    @Nonnull
    @Override
    Optional<String> getWikiText(Function<EaObjectRef, Optional<String>> linkFunction) {
        return Optional.of(title);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof WikiSetTitle)) return false;
        if (!super.equals(o)) return false;
        WikiSetTitle that = (WikiSetTitle) o;
        return getTitle().equals(that.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTitle());
    }

    @Override
    public String toString() {
        return "WikiSetName{" +
                "name='" + title + '\'' +
                "} " + super.toString();
    }
}
