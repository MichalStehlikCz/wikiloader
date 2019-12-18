package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

class WikiSetTopic extends WikiSetObjectBase {

    @Nonnull
    private final EaObjectRef eaObject;

    WikiSetTopic(EaObjectRef eaObject, List<WikiSetObjectBase> children) {
        super(children);
        this.eaObject = Objects.requireNonNull(eaObject);
    }

    @Nonnull
    @Override
    public String getTitle() {
        return eaObject.getName();
    }

    @Nonnull
    @Override
    Optional<String> getWikiText(Function<EaObjectRef, Optional<String>> linkFunction) {
        return Optional.ofNullable(
                linkFunction.apply(eaObject)
                .map(topicId -> "[[" + topicId + "|" + eaObject.getShortTitle() + "]]")
                .orElse(getChildren().isEmpty() ? null : eaObject.getName()));
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof WikiSetTopic)) return false;
        if (!super.equals(o)) return false;
        WikiSetTopic that = (WikiSetTopic) o;
        return eaObject.equals(that.eaObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), eaObject);
    }

    @Override
    public String toString() {
        return "WikiSetTopic{" +
                "eaObject=" + eaObject.getEaDesc() +
                "} " + super.toString();
    }
}
