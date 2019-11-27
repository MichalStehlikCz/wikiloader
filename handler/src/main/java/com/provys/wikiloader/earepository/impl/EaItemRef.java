package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nonnull;

interface EaItemRef extends EaElementRef {
    /**
     * @return shortened title that is used in context of enclosing group
     */
    @Nonnull
    String getTitleInGroup();
}
