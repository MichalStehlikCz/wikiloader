package com.provys.wikiloader.impl;

import com.provys.provyswiki.ProvysWikiClient;
import org.sparx.Repository;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;

/**
 * Stateless bean, capable of running synchronisation (creates RunHandler for actual execution)
 */
@ApplicationScoped
public class WikiLoader {

    private final HandlerFactory handlerFactory;

    @Inject
    public WikiLoader(HandlerFactory handlerFactory) {
        this.handlerFactory = Objects.requireNonNull(handlerFactory);
    }

    public void run(Repository eaRepository, ProvysWikiClient wikiClient, @Nullable String path, boolean recursive) {
        new RunHandler(eaRepository, wikiClient, handlerFactory, path, recursive).run();
    }
}
