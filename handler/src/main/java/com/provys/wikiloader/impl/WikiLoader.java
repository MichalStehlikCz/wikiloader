package com.provys.wikiloader.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaRepository;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;

/**
 * Stateless bean, capable of running synchronisation (creates RunHandler for actual execution)
 */
@ApplicationScoped
public class WikiLoader {

    private final EaRepository eaRepository;

    @SuppressWarnings("CdiUnproxyableBeanTypesInspection")
    @Inject
    public WikiLoader(EaRepository eaRepository) {
        this.eaRepository = Objects.requireNonNull(eaRepository);
    }

    public void run(ProvysWikiClient wikiClient, @Nullable String path, boolean recursive) {
        eaRepository.getObjectRefByPath(path).sync(wikiClient, recursive);
    }
}
