package com.provys.wikiloader.handlers;

import com.provys.wikiloader.impl.Handler;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Interface represents object, used to export single Enterprise Architect object to wiki page.
 * Exporter is used from handler, does actual export of content of given element or package to wiki page.
 */
interface Exporter {

    /**
     * Export specific page and return collection of objects, that should be also exported.
     *
     * @param recursive defines if all sub-objects should be exported (=returned) or only diagrams
     * @return collection of handlers of objects that should be exported, because they are related to given object
     */
    @Nonnull
    Collection<Handler> run(boolean recursive);
}
