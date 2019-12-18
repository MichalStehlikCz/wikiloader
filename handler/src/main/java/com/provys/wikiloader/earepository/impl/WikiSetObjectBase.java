package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;
import com.provys.wikiloader.earepository.WikiSetObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract class WikiSetObjectBase implements WikiSetObject {
    @Nonnull
    protected final List<WikiSetObjectBase> children;

    public WikiSetObjectBase(List<WikiSetObjectBase> children) {
        this.children = List.copyOf(children);
    }

    @Nonnull
    @Override
    public List<WikiSetObject> getChildren() {
        // children is unmodifiable itself, but unmodifiable list wrapper allows type cast
        return Collections.unmodifiableList(children);
    }

    /**
     * Get text that should be inserted to wiki (only link / name, without ident and bullet)
     *
     * @param linkFunction is function used to extract topic id from EA topic reference
     */
    @Nonnull
    abstract Optional<String> getWikiText(Function<EaObjectRef, Optional<String>> linkFunction);

    @SuppressWarnings("squid:S3457") // Sonar doesn't like string format with concat...
    void appendContent(StringBuilder builder, int level, Function<EaObjectRef, Optional<String>> linkFunction) {
        getWikiText(linkFunction)
                .ifPresent(wikiText ->
                        builder.append(String.format("%" + (level * 2 + 4) + "s", "* "))
                                .append(wikiText)
                                .append('\n'));
        for (var child : children) {
            child.appendContent(builder, level + 1, linkFunction);
        }
    }

    @Override
    public void appendContent(StringBuilder builder) {
        appendContent(builder, 0, EaObjectRef::getTopicId);
    }

    @Override
    public void appendContent(StringBuilder builder, Function<EaObjectRef, Optional<String>> linkFunction) {
        appendContent(builder, 0, linkFunction);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof WikiSetObjectBase)) return false;
        WikiSetObjectBase that = (WikiSetObjectBase) o;
        return getChildren().equals(that.getChildren());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChildren());
    }

    @Override
    public String toString() {
        return "WikiSetObjectBase{" +
                "children=" + children.stream().map(WikiSetObject::getTitle).collect(Collectors.joining()) +
                '}';
    }
}
