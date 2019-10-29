package com.provys.wikiloader.earepository.impl;

/**
 * Interface represents object, used to export single Enterprise Architect object to wiki page.
 * Exporter is used from handler, does actual export of content of given element or package to wiki page.
 */
interface Exporter {

    /**
     * Export specific page and return collection of objects, that should be also exported.
     *
     * @param recursive defines if all sub-objects should be exported or only diagrams
     */
    void run(boolean recursive);
}
