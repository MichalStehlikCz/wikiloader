package com.provys.wikiloader.earepository;

import com.provys.provyswiki.ProvysWikiClient;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface EaObject extends EaObjectRef {

    /**
     * @return EaRepository this object uses to resolve references to sub-objects
     */
    @Nonnull
    EaRepository getRepository();

    /**
     * @return title of main page of given object on wiki
     */
    @Nonnull
    String getTitle();

    /**
     * @return notes attached to object
     */
    Optional<String> getNotes();

    /**
     * Synchronize wiki page, corresponding to given object
     *
     * @param wikiClient is wikipedia client, used for export
     * @param recursive defines if we should export even sub-packages and elements or only given object and associated
     *                 diagrams
     */
    void sync(ProvysWikiClient wikiClient, boolean recursive);
}
