package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.Optional;

class EaBoundary extends EaObjectBase<EaBoundaryRef> {

    EaBoundary(EaBoundaryRef objectRef) {
        super(objectRef);
    }

    @Override
    @Nonnull
    public Optional<String> getNotes() {
        return Optional.empty();
    }

    @Override
    Level getLogLevel() {
        return Level.DEBUG;
    }

    @Override
    public void sync(ProvysWikiClient wikiClient, boolean recursive) {
        logNotExported();
    }
}
