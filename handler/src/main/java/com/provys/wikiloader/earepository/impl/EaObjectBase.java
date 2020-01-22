package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaModel;
import com.provys.wikiloader.earepository.EaObject;
import com.provys.wikiloader.earepository.EaObjectRef;
import com.provys.wikiloader.earepository.EaRepository;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

abstract class EaObjectBase<T extends EaObjectRef> implements EaObject {

    private static final Logger LOG = LogManager.getLogger(EaObjectBase.class);

    @Nonnull
    private final T objectRef;

    EaObjectBase(T objectRef) {
        this.objectRef = Objects.requireNonNull(objectRef);
    }

    @Override
    @Nonnull
    public EaRepository getRepository() {
        return objectRef.getRepository();
    }

    @Nonnull
    T getObjectRef() {
        return objectRef;
    }

    @Override
    @Nonnull
    public Optional<EaObjectRef> getParent() {
        return objectRef.getParent();
    }

    @Override
    @Nonnull
    public EaModel getModel() {
        return objectRef.getModel();
    }

    @Override
    @Nonnull
    public String getName() {
        return objectRef.getName();
    }

    @Nonnull
    @Override
    public String getTitle() {
        return objectRef.getTitle();
    }

    @Nonnull
    @Override
    public String getShortTitle() {
        return objectRef.getShortTitle();
    }

    @Override
    @Nonnull
    public Optional<String> getAlias() {
        return objectRef.getAlias();
    }

    @Override
    @Nonnull
    public String getType() {
        return objectRef.getType();
    }

    @Override
    @Nonnull
    public Optional<String> getStereotype() {
        return objectRef.getStereotype();
    }

    @Override
    @Nonnull
    public String getEaDesc() {
        return objectRef.getEaDesc();
    }

    @Override
    @Nonnull
    public EaObject getObject() {
        return this;
    }

    @Override
    public boolean isIgnoredType() {
        return objectRef.isIgnoredType();
    }

    @Override
    public boolean isTopic() {
        return objectRef.isTopic();
    }

    @Override
    @Nonnull
    public Optional<String> getTopicId() {
        return objectRef.getTopicId();
    }

    @Override
    @Nonnull
    public Optional<String> getNamespace() {
        return objectRef.getNamespace();
    }

    @Override
    @Nonnull
    public Optional<String> getParentLink() {
        return objectRef.getParentLink();
    }

    @Override
    public boolean hasLink() {
        return objectRef.hasLink();
    }

    @Override
    public void appendNamespace(StringBuilder builder, boolean trailingColon) {
        objectRef.appendNamespace(builder, trailingColon);
    }

    @Override
    public void appendLink(StringBuilder builder) {
        objectRef.appendLink(builder);
    }

    @Override
    public void appendParentLink(StringBuilder builder) {
        objectRef.appendParentLink(builder);
    }

    @Override
    public void appendPages(Collection<String> pages) {
        objectRef.appendPages(pages);
    }

    @Override
    @Nonnull
    public List<Integer> getPos() {
        return objectRef.getPos();
    }

    @Override
    public int compareTo(EaObjectRef o) {
        return objectRef.compareTo(o);
    }

    Level getLogLevel() {
        return Level.INFO;
    }

    void logNotExported() {
        LOG.log(getLogLevel(), "Synchronisation to wiki skipped for {}, object type {}", this::getName,
                this::getClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EaObjectBase<?> that = (EaObjectBase<?>) o;
        return Objects.equals(objectRef, that.objectRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectRef);
    }

    @Override
    public String toString() {
        return "EaObjectBase{" +
                "objectRef=" + objectRef +
                '}';
    }
}
