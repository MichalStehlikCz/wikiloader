package com.provys.wikiloader.wikimap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

class WikiSetObjectImpl implements WikiSetObject {

    @Nonnull
    private final String name;
    @Nullable
    private final String id;
    @Nonnull
    private final List<WikiSetObject> children;

    WikiSetObjectImpl(String name, @Nullable String id, List<WikiSetObject> children) {
        this.name = name;
        this.id = Objects.requireNonNull(id);
        this.children = new ArrayList<>(children);
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public Optional<String> getId() {
        return Optional.ofNullable(id);
    }

    @Nonnull
    @Override
    public String getWikiText() {
        return (id == null) ? name : "[[" + id + "]]";
    }

    @Override
    public List<WikiSetObject> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    @SuppressWarnings("squid:S3457") // Sonar doesn't like string format with concat...
    public void appendContent(StringBuilder builder, int level) {
        builder.append(String.format("%" + (level * 2 + 4) + "s", "* ")).append(getWikiText()).append('\n');
        for (var child : children) {
            child.appendContent(builder, level + 1);
        }
    }

    @Override
    public void appendContent(StringBuilder builder) {
        appendContent(builder, 0);
    }
}
