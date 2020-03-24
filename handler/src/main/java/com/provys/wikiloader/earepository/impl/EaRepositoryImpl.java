package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
class EaRepositoryImpl implements EaRepository {

    @Nonnull
    private final EaLoader loader;
    @Nonnull
    private final Map<Integer, EaElementRef> elementById = new HashMap<>(50);
    @Nonnull
    private final Map<Integer, EaDefaultPackageRef> packageById = new HashMap<>(30);
    @Nonnull
    private final Map<Integer, EaDefaultDiagramRef> diagramById = new HashMap<>(20);

    private void initModel() {
        // we need to initialise models as root packages and set their mapping to wiki
        for (var model : EaModel.values()) {
            var modelPackage = loader.getModel(model, this);
            packageById.put(modelPackage.getPackageId(), modelPackage);
        }
    }

    @Autowired
    EaRepositoryImpl(EaLoader loader) {
        this.loader = loader;
        // we need to initialize model (root packages) and their mapping to wiki
        initModel();
    }

    @Nonnull
    EaLoader getLoader() {
        return loader;
    }

    @Override
    @Nonnull
    public EaElementRef getElementRefById(int elementId) {
        var result = elementById.get(elementId);
        if (result == null) {
            result = loader.elementRefFromId(elementId, this);
            var old = elementById.putIfAbsent(elementId, result);
            if (old != null) {
                result = old;
            }
        }
        return result;
    }

    @Override
    @Nonnull
    public EaDefaultPackageRef getPackageRefById(int packageId) {
        var result = packageById.get(packageId);
        if (result == null) {
            result = loader.packageRefFromId(packageId, this);
            var old = packageById.putIfAbsent(packageId, result);
            if (old != null) {
                result = old;
            }
        }
        return result;
    }

    @Override
    @Nonnull
    public EaDefaultDiagramRef getDiagramRefById(int diagramId) {
        var result = diagramById.get(diagramId);
        if (result == null) {
            result = loader.diagramRefFromId(diagramId, this);
            var old = diagramById.putIfAbsent(diagramId, result);
            if (old != null) {
                result = old;
            }
        }
        return result;
    }

    @Override
    @Nonnull
    public EaObjectRef getObjectRefByPath(EaModel model, @Nullable String path) {
        return loader.getRefObjectByPath(model, path, this);
    }

    @Override
    public void flush() {
        elementById.clear();
        packageById.clear();
        diagramById.clear();
        initModel();
    }

    @Override
    public WikiSetBuilder getWikiSetBuilder() {
        return new WikiSetBuilderImpl(this);
    }
}
