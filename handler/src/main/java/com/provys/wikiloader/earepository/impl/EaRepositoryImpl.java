package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaRepository;
import org.sparx.Repository;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EaRepositoryImpl implements EaRepository {

    @Nonnull
    private final Repository repository;
    @Nonnull
    private final Map<Integer, EaElementImpl> elementById = new HashMap<>(50);
    @Nonnull
    private final Map<Integer, EaPackageImpl> packageById = new HashMap<>(30);

    public EaRepositoryImpl(Repository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    @Nonnull
    public Repository getRepository() {
        return repository;
    }

    @Override
    @Nonnull
    public EaElementImpl getElementById(int elementId) {
        var result = elementById.get(elementId);
        if (result == null) {
            result = EaElementImpl.ofElement(repository.GetElementByID(elementId), this);
            var old = elementById.putIfAbsent(elementId, result);
            if (old != null) {
                result = old;
            }
        }
        return result;
    }

    @Override
    @Nonnull
    public EaPackageImpl getPackageById(int packageId) {
        var result = packageById.get(packageId);
        if (result == null) {
            result = EaPackageImpl.ofPackage(repository.GetPackageByID(packageId), this);
            var old = packageById.putIfAbsent(packageId, result);
            if (old != null) {
                result = old;
            }
        }
        return result;
    }
}
