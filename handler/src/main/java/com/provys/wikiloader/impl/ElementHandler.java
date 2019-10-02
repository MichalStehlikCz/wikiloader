package com.provys.wikiloader.impl;

import com.provys.provyswiki.ProvysWikiClient;

import javax.annotation.Nonnull;

/**
 * Object handler represents object in Enterprise Architect and can export it as wiki page. Specific implementations
 * can be made for individual object types
 */
public interface ElementHandler {

    /**
     * @return link to element, relative from enclosing package / element
     */
    @Nonnull
    String getRelLink();

    /**
     * Synchronize wiki page, corresponding to given element
     */
    void sync(ProvysWikiClient wikiClient);
}
