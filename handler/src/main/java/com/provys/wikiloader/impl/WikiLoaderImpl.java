package com.provys.wikiloader.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.WikiLoader;
import com.provys.wikiloader.earepository.EaModel;
import com.provys.wikiloader.earepository.EaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Stateless bean, capable of running synchronisation (creates RunHandler for actual execution)
 */
@Component
class WikiLoaderImpl implements WikiLoader {

    private final EaRepository eaRepository;
    private final ProvysWikiClient wikiClient;

    @Autowired
    WikiLoaderImpl(EaRepository eaRepository, ProvysWikiClient wikiClient) {
        this.eaRepository = Objects.requireNonNull(eaRepository);
        this.wikiClient = Objects.requireNonNull(wikiClient);
    }

    @Override
    public void run(@Nullable String model, @Nullable String path, boolean recursive, boolean flush) {
        if (flush) {
            eaRepository.flush();
        }
        if (model == null) {
            if (path != null) {
                throw new IllegalArgumentException("Model has to be specified when path is specified");
            }
            for (var eaModel : EaModel.values()) {
                eaRepository.getObjectRefByPath(eaModel, null).getObject().sync(wikiClient, recursive);
            }
        } else {
            eaRepository.getObjectRefByPath(EaModel.getByWikiNamespace(model), path).getObject().sync(wikiClient, recursive);
        }
    }
}
