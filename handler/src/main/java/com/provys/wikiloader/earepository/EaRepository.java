package com.provys.wikiloader.earepository;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Holds repository of Enterprise Architect object (package, element, diagram) references and provides access to them
 * via their Enterprise Architect Id. Is meant to fully encapsulate access to Enterprise Architect API
 */
public interface EaRepository {

    /**
     * @param elementId is id of element in enterprise architect repository
     * @return element with specified id
     */
    @Nonnull
    EaElementRef getElementRefById(int elementId);

    /**
     * @param packageId is id of package in enterprise architect repository
     * @return package with specified id
     */
    @Nonnull
    EaPackageRef getPackageRefById(int packageId);

    /**
     * @param diagramId is id of diagram in enterprise architect repository
     * @return diagram with specified id
     */
    @Nonnull
    EaDiagramRef getDiagramRefById(int diagramId);

    /**
     * Get repository object on given path.
     *
     * @param model is model wiki path
     * @param path is path,
     */
    @Nonnull
    EaObjectRef getObjectRefByPath(EaModel model, @Nullable String path);

    /**
     * Flush all content of repository to force reload
     */
    void flush();

    /**
     * @return new Wiki set builder
     */
    WikiSetBuilder getWikiSetBuilder();
}
