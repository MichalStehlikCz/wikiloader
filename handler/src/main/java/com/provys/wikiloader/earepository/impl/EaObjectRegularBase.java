package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaObjectRef;
import com.provys.wikiloader.earepository.EaRepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

class EaObjectRegularBase<T extends EaObjectRef> extends EaObjectBase<T> {

    @Nullable
    private final String notes;

    EaObjectRegularBase(EaRepository repository, T objectRef, @Nullable String notes) {
        super(repository, objectRef);
        this.notes = ((notes == null) || notes.isEmpty()) ? null : notes;
    }

    @Nonnull
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaObjectRegularExporter<>(this, wikiClient);
    }

    @Override
    public void sync(ProvysWikiClient wikiClient, boolean recursive) {
        getTopicId().ifPresentOrElse(title -> getExporter(wikiClient).run(recursive), this::logNotExported);
    }

    @Override
    public Optional<String> getNotes() {
        return Optional.ofNullable(notes);
    }
}
