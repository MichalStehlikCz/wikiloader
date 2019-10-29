package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObject;
import com.provys.wikiloader.earepository.EaObjectRef;
import com.provys.wikiloader.earepository.EaRepository;
import com.provys.wikiloader.earepository.WikiSetBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
class EaRepositoryImpl implements EaRepository {

    @Nonnull
    private final EaLoaderImpl loader;
    @Nonnull
    private final Map<Integer, EaElementRefBase> elementById = new HashMap<>(50);
    @Nonnull
    private final Map<Integer, EaDefaultPackageRef> packageById = new HashMap<>(30);
    @Nonnull
    private final Map<Integer, EaDefaultDiagramRef> diagramById = new HashMap<>(20);

    @Inject
    public EaRepositoryImpl(EaLoaderImpl loader) {
        this.loader = loader;
        // we also need to initialise model as root package and set its mapping to wiki
        var model = loader.getModel();
        packageById.put(model.getPackageId(), model);
    }

    @Override
    @Nonnull
    public EaElementRefBase getElementRefById(int elementId) {
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
    public EaObjectRef getObjectRefByPath(@Nullable String path) {
        return loader.getRefObjectByPath(path, this);
    }

    @Nonnull
    @Override
    public EaObject getObjectByRef(EaObjectRef objectRef) {
        return null;
    }

    @Override
    public WikiSetBuilder getWikiSetBuilder() {
        return new WikiSetBuilderImpl(this);
    }
}
