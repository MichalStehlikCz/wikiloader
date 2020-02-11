package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaObject;
import com.provys.wikiloader.earepository.EaObjectRef;
import com.provys.wikiloader.earepository.EaPackageRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class EaDefaultPackageRef extends EaObjectRefBase implements EaPackageRef {

    private static final Logger LOG = LogManager.getLogger(EaDefaultPackageRef.class);

    private final int packageId;

    EaDefaultPackageRef(EaRepositoryImpl repository, @Nullable EaObjectRefBase parent, String name,
                        @Nullable String alias, @Nullable String stereotype, int treePos, int packageId) {
        super(repository, parent, name, alias, "Package", stereotype, treePos);
        this.packageId = packageId;
    }

    @Override
    public int getPackageId() {
        return packageId;
    }

    @Override
    @Nonnull
    public EaObject getObject() {
        return getRepository().getLoader().loadDefaultPackage(this);
    }

    @Override
    public boolean isTopic() {
        if (getAlias().isEmpty()) {
            LOG.warn("Package {} is not exported - alias is empty", this::getName);
            return false;
        }
        return getParent().map(EaObjectRef::hasLink).orElse(true);
    }

    @Override
    @Nonnull
    public Optional<String> getTopicId() {
        return getNamespace()
                .map(ns -> ns + ":start");
    }

    @Override
    @Nonnull
    public Optional<String> getNamespace() {
        if (getAlias().isEmpty()) {
            return Optional.empty();
        }
        return getParent()
                .map(parent -> parent.getNamespace().map(ns -> ns + ":")) // get namespace from parent
                .orElse(Optional.of("")) // if no parent, use "" as prefix
                .map(ns -> ns + getAlias().get()); // append this topic's alias, acting as namespace
    }

    @Override
    @SuppressWarnings("squid:S3655") // sonar does not recognise Optional.isEmpty...
    public void appendNamespace(StringBuilder builder, boolean trailingColon) {
        if (getAlias().isEmpty()) {
            throw new InternalException(
                    "Request to append namespace for element that does not translate to namespace " + this);
        }
        getParent().ifPresent(parent -> parent.appendNamespace(builder, true));
        builder.append(getAlias().get());
        if (trailingColon) {
            builder.append(':');
        }
    }

    @Override
    public void appendParentLinkNoCheck(StringBuilder builder, boolean leadingDot) {
        if (leadingDot) {
            builder.append('.');
        }
        getAlias().ifPresent(builder::append);
        builder.append(":");
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaDefaultPackageRef eaPackage = (EaDefaultPackageRef) o;
        return (packageId == eaPackage.packageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), packageId);
    }

    @Override
    public String toString() {
        return "EaPackageImpl{" +
                "packageId=" + packageId +
                "} " + super.toString();
    }
}
