package com.provys.wikiloader.earepository.impl;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface EaUGTopicRef extends EaItemRef {
    @Nonnull
    Optional<String> getUserGuideTopicName();

    @Nonnull
    Optional<String> getUserGuideTopicId();

    @Nonnull
    @Override
    EaUGTopic getObject();
}
