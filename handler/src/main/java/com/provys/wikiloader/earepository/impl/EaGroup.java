package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.util.Optional;

class EaGroup extends EaObjectBase<EaGroupRef> {

    EaGroup(EaGroupRef objectRef) {
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
