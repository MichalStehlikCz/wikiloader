package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EaLoader {
    @Nonnull
    EaDefaultPackageRef getModel(EaRepositoryImpl eaRepository);

    EaElementRefBase elementRefFromId(int elementId, EaRepositoryImpl eaRepository);

    EaDefaultPackageRef packageRefFromId(int packageId, EaRepositoryImpl eaRepository);

    EaDefaultDiagramRef diagramRefFromId(int diagramId, EaRepositoryImpl eaRepository);

    /**
     * Retrieve ref object corresponding to supplied path
     *
     * @param path is path, with aliases divided by :
     * @param eaRepository is repository from which resulting object should be taken
     * @return ref object that corresponds to supplied path, throw exception when such object is not found
     */
    EaObjectRef getRefObjectByPath(@Nullable String path, EaRepositoryImpl eaRepository);

    EaDefaultDiagram loadDefaultDiagram(EaDiagramRef diagramRef);

    @Nonnull
    EaProductPackageGroup loadProductPackageGroup(EaProductPackageGroupRef packageGroupRef);

    @Nonnull
    EaTechnicalPackageGroup loadTechnicalPackageGroup(EaTechnicalPackageGroupRef packageGroupRef);

    @Nonnull
    EaPackage loadDefaultPackage(EaPackageRef packageRef);

    @Nonnull
    EaProductPackage loadProductPackage(EaProductPackageRef elementRef);

    @Nonnull
    EaTechnicalPackage loadTechnicalPackage(EaTechnicalPackageRef elementRef);

    @Nonnull
    EaMeaning loadMeaning(EaMeaningRef elementRef);

    @Nonnull
    EaFunctionTask loadFunctionTask(EaFunctionTaskRef elementRef);

    @Nonnull
    EaDataObject loadDataObject(EaElementRef elementRef);

    @Nonnull
    EaLeafElement loadLeafElement(EaLeafElementRef elementRef);

    @Nonnull
    EaObject loadNamespaceElement(EaNamespaceElementRef elementRef);
}
