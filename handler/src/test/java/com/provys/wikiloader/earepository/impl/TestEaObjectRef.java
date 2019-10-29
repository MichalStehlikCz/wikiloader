package com.provys.wikiloader.earepository.impl;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Class used to mock parent in tests of other classes. Only implements methods needed for testing (e.g. which are
 * invoked on parent)
 */
class TestEaObjectRef extends EaObjectRefBase {

    TestEaObjectRef(@Nullable String alias) {
        super(null, "Mock Parent", alias, null, 1);
    }

    @Override
    public Optional<String> getTopicId() {
        throw new RuntimeException("Method not implemented in mock");
    }

    @Override
    public Optional<String> getNamespace() {
        return getAlias().map(alias -> alias + ":");
    }

    @Override
    public boolean hasLink() {
        return getAlias().isPresent();
    }

    @Override
    public void appendNamespace(StringBuilder builder) {
        if (getAlias().isEmpty()) {
            throw new RuntimeException("Cannot append namespace - alias is empty");
        }
        builder.append(getAlias().get()).append(":");
    }

    @Override
    public void appendParentLink(StringBuilder builder) {
        throw new RuntimeException("Method not implemented in mock");
    }
}
