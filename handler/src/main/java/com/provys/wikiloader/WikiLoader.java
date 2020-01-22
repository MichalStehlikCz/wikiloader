package com.provys.wikiloader;

import javax.annotation.Nullable;

/**
 * WikiLoader represents class that can be used to execute synchronisation of wiki with enterprise architect
 */
public interface WikiLoader {

    /**
     * Run synchronisation of wiki or its section with Enterprise Architect
     *
     * @param path is root document that should be synchronised
     * @param recursive indicates if only single document is to be synchronised or whole sub-tree
     * @param flush indicates that enterprise architect repository (cache) should be flushed prior to execution. Used
     *             when there is no other mechanism that would ensure that data from repository are up-to-date
     */
    void run(@Nullable String model, @Nullable String path, boolean recursive, boolean flush);
}
