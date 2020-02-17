package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Represents functional system component - report or interface implementation
 */
interface EaSysFuncRef extends EaElementRef {

    @Nonnull
    Optional<String> getDescriptionTopicName();

    @Nonnull
    Optional<String> getDescriptionTopicId();
}
