package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObject;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents UMLDiagram element - it is link to diagram used in master diagram
 */
class EaUmlDiagramElementRef extends EaElementRefBase {

    private static final Logger LOG = LogManager.getLogger(EaUmlDiagramElementRef.class);

    @Nullable
    private final EaDefaultDiagramRef diagram;

    EaUmlDiagramElementRef(EaRepositoryImpl eaRepository, @Nullable EaObjectRef parent, String name, int treePos,
                           int elementId, @Nullable EaDefaultDiagramRef diagram) {
        super(eaRepository, parent, name, null, "UMLDiagram", null, treePos, elementId);
        this.diagram = diagram;
    }

    @Override
    @Nonnull
    public EaObject getObject() {
        return new EaUmlDiagramElement(this);
    }

    @Override
    public boolean isIgnoredType() {
        return true;
    }

    @Override
    @Nonnull
    public Optional<String> getTopicId() {
        return Optional.empty(); // never exported on its own
    }

    @Override
    @Nonnull
    public Optional<String> getNamespace() {
        return Optional.empty(); // not exported, and thus not exported to namespace
    }

    @Override
    public boolean hasLink() {
        return (diagram != null) && diagram.hasLink();
    }

    @Override
    public void appendNamespace(StringBuilder builder, boolean trailingColon) {
        throw new InternalException(LOG, "Cannot append namespace - diagram reference not exported " + this);
    }

    @Override
    public void appendLinkNoCheck(StringBuilder builder) {
        assert (diagram != null);
        diagram.appendLink(builder);
    }

    @Override
    public void appendParentLinkNoCheck(StringBuilder builder, boolean leadingDot) {
        appendLinkNoCheck(builder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaUmlDiagramElementRef that = (EaUmlDiagramElementRef) o;
        return Objects.equals(diagram, that.diagram);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), diagram);
    }

    @Override
    public String toString() {
        return "EaUmlDiagramElementRef{" +
                "diagram=" + diagram +
                "} " + super.toString();
    }
}
