package com.provys.wikiloader.impl;

import com.provys.provyswiki.ProvysWikiClient;

import javax.annotation.Nonnull;

/**
 * Object handler represents object in Enterprise Architect and can export it as wiki page. Specific implementations
 * can be made for individual object types
 */
public interface ElementHandler {

    /**
     * @return name of object used as topic name in wiki; usually lower-case of alias
     */
    @Nonnull
    String getName();

    /**
     * Synchronize wiki page, corresponding to given element
     */
    void sync(ProvysWikiClient wikiClient, LinkResolver linkResolver);
}
