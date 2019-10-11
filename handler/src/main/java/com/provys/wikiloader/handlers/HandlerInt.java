package com.provys.wikiloader.handlers;

import com.provys.wikiloader.impl.Handler;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

/**
 * Interface extends Handler ler with methods, needed by exporter
 */
interface HandlerInt extends Handler {

    /**
     * @return diagrams belonging to given handler (e.g. package or element)
     */
    @Nonnull
    Collection<Handler> getDiagrams();

    /**
     * @return subpackages belonging to given handler
     */
    @Nonnull
    Collection<Handler> getSubPackages();

    /**
     * @return elements belonging to given handler
     */
    @Nonnull
    Collection<Handler> getElements();

    /**
     * @return name of topic in Enterprise Architect (it is often used as title)
     */
    @Nonnull
    String getEaName();

    /**
     * @return alias of topic in Enterprise Architect
     */
    @Nonnull
    String getEaAlias();

    /**
     * @return notes attached to topic in Enterprise Architect
     */
    @Nonnull
    String getEaNotes();

    /**
     * @return wiki namespace if item exports to namespace, empty optional if it is exported to single topic
     */
    @Nonnull
    Optional<String> getNamespace();

    /**
     * @return Id of generated topic
     */
    @Nonnull
    String getId();
}
