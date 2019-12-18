package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

/**
 * Base class for reference to element, that is kept in functional model and used in user guide.
 */
abstract class EaUGTopicRef<R extends EaUGTopicRef<R, T>, T extends EaUGTopic<R, T>>
        extends EaCachedLeafElementRef<R, T> implements EaItemRef {

    private static final Logger LOG = LogManager.getLogger(EaUGTopicRef.class);

    private static final String USER_GUIDE_POSTFIX = "user_guide";

    EaUGTopicRef(EaRepositoryImpl repository, EaObjectRefBase parent, String name, @Nullable String alias,
                 String type, @Nullable String stereotype, int treePos, int elementId) {
        super(repository, parent, name, alias, type, stereotype, treePos, elementId);
    }

    /**
     * @return topic name of user guide topic
     */
    @Nonnull
    Optional<String> getUserGuideTopicName() {
        var alias = getAlias();
        if (alias.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(alias.get() + "." + USER_GUIDE_POSTFIX);
    }

    /**
     * @return full topicId of user guide topic
     */
    @Nonnull
    Optional<String> getUserGuideTopicId() {
        return getUserGuideTopicName()
                .flatMap(topicName ->
                        getParent()
                                .map(EaObjectRef::getNamespace) // get namespace from parent
                                .orElseThrow(() -> new InternalException(LOG, "User guide topic should have parent")) // if no parent, use "" as prefix)
                                .map(ns -> ns + ":" + topicName));
    }

    @Override
    public void appendPages(Collection<String> pages) {
        super.appendPages(pages);
        getUserGuideTopicName().ifPresent(pages::add);
    }
}
