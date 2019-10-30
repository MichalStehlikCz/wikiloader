package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaObjectRef;

import javax.annotation.Nullable;
import java.util.Optional;

abstract class EaObjectRegularBase<T extends EaObjectRef> extends EaObjectBase<T> {
    @Nullable
    protected final String notes;

    EaObjectRegularBase(T objectRef, @Nullable String notes) {
        super(objectRef);
        this.notes = ((notes == null) || notes.isEmpty()) ? null : notes;
    }

    abstract Exporter getExporter(ProvysWikiClient wikiClient);

    @Override
    public void sync(ProvysWikiClient wikiClient, boolean recursive) {
        getTopicId().ifPresentOrElse(title -> getExporter(wikiClient).run(recursive), this::logNotExported);
    }

    @Override
    public Optional<String> getNotes() {
        return Optional.ofNullable(notes);
    }
}
