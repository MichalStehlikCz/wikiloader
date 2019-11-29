package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObject;
import com.provys.wikiloader.earepository.EaRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Class used to mock parent in tests of other classes. Only implements methods needed for testing (e.g. which are
 * invoked on parent)
 */
class TestEaObjectRef extends EaObjectRefBase {

    TestEaObjectRef(EaRepositoryImpl repository, @Nullable String alias) {
        super(repository, null, "Mock Parent", alias, "Type", null, 1);
    }

    @Override
    @Nonnull
    public EaObject getObject() {
        throw new RuntimeException("Method not implemented in mock");
    }

    @Override
    public boolean isTopic() {
        return getAlias().isPresent();
    }

    @Override
    @Nonnull
    public Optional<String> getTopicId() {
        throw new RuntimeException("Method not implemented in mock");
    }

    @Override
    @Nonnull
    public Optional<String> getNamespace() {
        return getAlias().map(alias -> alias);
    }

    @Override
    public boolean hasLink() {
        return getAlias().isPresent();
    }

    @Override
    void appendParentLinkNoCheck(StringBuilder builder, boolean leadingDot) {
        throw new RuntimeException("Method not implemented in mock");
    }

    @Override
    public void appendNamespace(StringBuilder builder, boolean trailingColon) {
        if (getAlias().isEmpty()) {
            throw new RuntimeException("Cannot append namespace - alias is empty");
        }
        builder.append(getAlias().get());
        if (trailingColon) {
            builder.append(":");
        }
    }
}
