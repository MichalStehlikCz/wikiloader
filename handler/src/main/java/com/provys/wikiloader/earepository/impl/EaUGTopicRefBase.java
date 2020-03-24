package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

/**
 * Base class for reference to element, that is kept in functional model and used in user guide.
 */
abstract class EaUGTopicRefBase<R extends EaUGTopicRefBase<R, T>, T extends EaUGTopicBase<R, T>>
        extends EaCachedLeafElementRef<R, T> implements EaUGTopicRef {

    private static final String USER_GUIDE_POSTFIX = "user_guide";

    EaUGTopicRefBase(EaRepositoryImpl repository, EaObjectRef parent, String name, @Nullable String alias,
                     String type, @Nullable String stereotype, int treePos, int elementId) {
        super(repository, parent, name, alias, type, stereotype, treePos, elementId);
    }

    /**
     * @return topic name of user guide topic
     */
    @Override
    @Nonnull
    public Optional<String> getUserGuideTopicName() {
        var alias = getAlias();
        if (alias.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(alias.get() + "." + USER_GUIDE_POSTFIX);
    }

    /**
     * @return full topicId of user guide topic
     */
    @Override
    @Nonnull
    public Optional<String> getUserGuideTopicId() {
        return getUserGuideTopicName()
                .flatMap(topicName ->
                        getParent()
                                .map(EaObjectRef::getNamespace) // get namespace from parent
                                .orElseThrow(() -> new InternalException("User guide topic should have parent")) // if no parent, use "" as prefix)
                                .map(ns -> ns + ":" + topicName));
    }

    @Override
    public void appendPages(Collection<? super String> pages) {
        super.appendPages(pages);
        getUserGuideTopicName().ifPresent(pages::add);
    }
}
