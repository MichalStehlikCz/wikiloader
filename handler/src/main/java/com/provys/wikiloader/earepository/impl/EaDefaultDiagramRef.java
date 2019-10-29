package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

class EaDefaultDiagramRef extends EaObjectRefBase implements EaDiagramRef {

    private static final Logger LOG = LogManager.getLogger(EaDefaultDiagramRef.class);

    /**
     * Constructs topic name for diagram
     *
     * @param name is diagram name
     * @return topic name (valid wiki topic name that will be used for given diagram)
     */
    private static String getAlias(String name) {
        return "dia_" + name.toLowerCase().replace(' ', '_')
                .replace("&", "and").replaceAll("[():]", "");
    }

    private final int diagramId;

    EaDefaultDiagramRef(EaObjectRefBase parent, String name, @Nullable String stereotype, int diagramId) {
        super(parent, name, getAlias(name), stereotype, 0);
        Objects.requireNonNull(parent);
        this.diagramId = diagramId;
    }

    @Override
    public int getDiagramId() {
        return diagramId;
    }

    @Override
    public boolean isTopic() {
        if (getAlias().isEmpty()) {
            LOG.info("Diagram {} {} is not exported - alias is empty", this::getStereotype, this::getName);
            return false;
        }
        return getParent().orElseThrow().hasLink();
    }

    @Override
    public Optional<String> getTopicId() {
        if (getAlias().isEmpty()) {
            return Optional.empty();
        }
        return getParent()
                .map(EaObjectRef::getNamespace) // get namespace from parent
                .orElse(Optional.of("")) // if no parent, use "" as prefix
                .map(ns -> ns + getAlias().get()); // append this topic's alias
    }

    @Override
    public Optional<String> getNamespace() {
        return Optional.empty(); // diagram is never exported as namespace
    }

    @Override
    public void appendNamespace(StringBuilder builder) {
        throw new InternalException(LOG, "Diagram cannot is not exported as namespace " + this);
    }

    @Override
    @SuppressWarnings("squid:S3655") // sonar does not recognise Optional.isEmpty
    public void appendParentLink(StringBuilder builder) {
        if (!hasLink() || getAlias().isEmpty()) {
            throw new InternalException(LOG, "Cannot append diagram link - diagram, not exported " + this);
        }
        builder.append(getAlias().get());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaDefaultDiagramRef that = (EaDefaultDiagramRef) o;
        return diagramId == that.diagramId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), diagramId);
    }

    @Override
    public String toString() {
        return "EaDefaultDiagramRef{" +
                "diagramId=" + diagramId +
                "} " + super.toString();
    }
}
