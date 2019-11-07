package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaObject;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
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

    EaDefaultDiagramRef(EaRepositoryImpl repository, EaObjectRefBase parent, String name, @Nullable String stereotype,
                        int diagramId) {
        super(repository, parent, name, getAlias(name), "Diagram", stereotype, 0);
        Objects.requireNonNull(parent);
        this.diagramId = diagramId;
    }

    @Override
    public int getDiagramId() {
        return diagramId;
    }

    @Override
    @Nonnull
    public EaObject getObject() {
        return getRepository().getLoader().loadDefaultDiagram(this);
    }

    @Override
    @Nonnull
    public Optional<String> getTopicId() {
        if (getAlias().isEmpty()) {
            return Optional.empty();
        }
        return getParent()
                .map(EaObjectRef::getNamespace) // get namespace from parent
                .orElse(Optional.of("")) // if no parent, use "" as prefix
                .map(ns -> ns + ":" + getAlias().get()); // append this topic's alias
    }

    @Override
    @Nonnull
    public Optional<String> getNamespace() {
        return Optional.empty(); // diagram is never exported as namespace
    }

    @Override
    public void appendNamespace(StringBuilder builder, boolean trailingColon) {
        throw new InternalException(LOG, "Diagram cannot is not exported as namespace " + this);
    }

    @Override
    public void appendParentLinkNoCheck(StringBuilder builder, boolean leadingDot) {
        builder.append(getAlias().orElseThrow());
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
