package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObject;

/**
 * Interface represents object, used to export single Enterprise Architect object to wiki page.
 * Exporter is created by EaObject to perform actual export of content of given element or package to wiki page.
 */
interface Exporter {

    /**
     * Retrieve Enterprise Architect object this exporter is used for
     *
     * @return Enterprise Architect object this exporter services
     */
    EaObject getEaObject();

    /**
     * Add page to list of pages that should be kept. Used when some pages are created from outside to namespace being
     * generated in this exporter. Has no real effect if exporter does not synchronize namespace
     *
     * @param page is name of page to be added
     * @return self to support fluent configuration
     */
    @SuppressWarnings("UnusedReturnValue")
    Exporter addPage(String page);

    /**
     * Export specific page and return collection of objects, that should be also exported.
     *
     * @param recursive defines if all sub-objects should be exported or only diagrams
     */
    void run(boolean recursive);
}
