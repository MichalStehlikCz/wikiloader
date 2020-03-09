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

    EaDefaultDiagramRef(EaRepositoryImpl repository, EaObjectRef parent, String name, @Nullable String stereotype,
                        int diagramId) {
        super(repository, parent, name, getAlias(name), "Diagram", stereotype, -2000);
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

    @Nullable
    byte[] getPicture() {
        return getRepository().getLoader().getDiagramPicture(this);
    }

    @Override
    @Nonnull
    public Optional<String> getTopicId() {
        var alias = getAlias();
        if (alias.isEmpty()) {
            return Optional.empty();
        }
        var parent = getParent().orElseThrow(); // parent is always specified for diagram
        if (!parent.isTopic()) { /// parent not topic -> we are not topic
            return Optional.empty();
        }
        var parentNs = parent.getNamespace();
        if (parentNs.isEmpty()) {
            return Optional.of(parent.getTopicId().orElseThrow() + '.' + alias.get()); // append . + alias to parent topic
        }
        return Optional.of(parentNs.get() + ":" + alias.get()); // append this topic's alias
    }

    @Override
    @Nonnull
    public Optional<String> getNamespace() {
        return Optional.empty(); // diagram is never exported as namespace
    }

    @Override
    public void appendNamespace(StringBuilder builder, boolean trailingColon) {
        throw new InternalException("Diagram cannot be exported as namespace " + this);
    }

    @Override
    public void appendParentLinkNoCheck(StringBuilder builder, boolean leadingDot) {
        var parent = getParent().orElseThrow();
        if (parent.getNamespace().isEmpty()) {
            // diagram under element with no namespace
            parent.appendParentLink(builder);
            builder.append('.').append(getAlias().orElseThrow());
            return;
        }
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
