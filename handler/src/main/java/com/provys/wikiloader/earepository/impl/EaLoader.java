package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface EaLoader {

    @Nonnull
    EaDefaultPackageRef getModel(EaModel model, EaRepositoryImpl eaRepository);

    EaElementRef elementRefFromId(int elementId, EaRepositoryImpl eaRepository);

    EaDefaultPackageRef packageRefFromId(int packageId, EaRepositoryImpl eaRepository);

    EaDefaultDiagramRef diagramRefFromId(int diagramId, EaRepositoryImpl eaRepository);

    /**
     * Retrieve ref object corresponding to supplied path
     *
     * @param model is model in which path is looked up
     * @param path is path, with aliases divided by :
     * @param eaRepository is repository from which resulting object should be taken
     * @return ref object that corresponds to supplied path, throw exception when such object is not found
     */
    EaObjectRef getRefObjectByPath(EaModel model, @Nullable String path, EaRepositoryImpl eaRepository);

    EaDefaultDiagram loadDefaultDiagram(EaDefaultDiagramRef diagramRef);

    @Nullable
    byte[] getDiagramPicture(EaDiagramRef diagramRef);

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
    EaMeaningItem loadMeaningItem(EaMeaningItemRef elementRef);

    @Nonnull
    EaMeaningGroup loadMeaningGroup(EaMeaningGroupRef elementRef);

    @Nonnull
    EaFunctionTask loadFunctionTask(EaFunctionTaskRef elementRef);

    @Nonnull
    EaReportAbstract loadReportAbstract(EaReportAbstractRef elementRef);

    @Nonnull
    EaInterfaceAbstract loadInterfaceAbstract(EaInterfaceAbstractRef elementRef);

    @Nonnull
    EaSysFuncImpl loadSysFunc(EaSysFuncImplRef elementRef);

    @Nonnull
    EaDataObject loadDataObject(EaElementRef elementRef);

    @Nonnull
    EaLeafElement loadLeafElement(EaLeafElementRef elementRef);

    @Nonnull
    EaObject loadNamespaceElement(EaNamespaceElementRef elementRef);
}
