package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;
import com.provys.wikiloader.earepository.WikiSetObject;

import javax.annotation.Nonnull;
import java.util.*;

class WikiSetObjectImpl implements WikiSetObject {

    @Nonnull
    private final EaObjectRef eaObject;
    @Nonnull
    private final List<WikiSetObject> children;

    WikiSetObjectImpl(EaObjectRef eaObject, List<WikiSetObject> children) {
        this.eaObject = Objects.requireNonNull(eaObject);
        this.children = new ArrayList<>(children);
    }

    @Nonnull
    @Override
    public String getName() {
        return eaObject.getName();
    }

    @Override
    public void appendWikiText(StringBuilder builder) {
        if (eaObject.hasLink()) {
            builder.append("[[");
            eaObject.appendLink(builder);
            builder.append("]]");
        } else {
            builder.append(eaObject.getName());
        }
    }

    @Override
    public List<WikiSetObject> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    @SuppressWarnings("squid:S3457") // Sonar doesn't like string format with concat...
    public void appendContent(StringBuilder builder, int level) {
        builder.append(String.format("%" + (level * 2 + 4) + "s", "* "));
        appendWikiText(builder);
        builder.append('\n');
        for (var child : children) {
            child.appendContent(builder, level + 1);
        }
    }

    @Override
    public void appendContent(StringBuilder builder) {
        appendContent(builder, 0);
    }
}
