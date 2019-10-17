package com.provys.wikiloader.wikimap;

import org.sparx.Package;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Class holds information about package mapping to wiki
 */
public class WikiPackage {

    private static final WikiPackage EMPTY_PACKAGE_INFO = new WikiPackage(null, false);

    static WikiPackage of(Package pkg, WikiMap wikiMap) {
        if (pkg.GetAlias().isEmpty()) {
            return EMPTY_PACKAGE_INFO;
        }
        var parentInfo = wikiMap.getWikiPackage(pkg.GetParentID()); // we expect root package has been
        // registered as part of link resolver creation
        return new WikiPackage(
                parentInfo.getNamespace()
                        .map(parent -> parent + ":" + pkg.GetAlias().toLowerCase())
                        .orElse(null),
                true);
    }

    static WikiPackage of(int packageId, WikiMap wikiMap) {
        Package pkg = wikiMap.getRepository().GetPackageByID(packageId);
        try {
            return of(pkg, wikiMap);
        } finally {
            pkg.destroy();
        }
    }

    @Nullable
    private final String namespace;
    private final boolean underParent;

    WikiPackage(@Nullable String namespace, boolean underParent) {
        this.namespace = namespace;
        this.underParent = underParent;
    }

    /**
     * @return true if package is exported to wiki, false otherwise
     */
    public boolean isExported() {
        return (namespace != null);
    }

    /**
     * @return namespace corresponding to package on wiki, empty if package is not exported
     */
    @Nonnull
    public Optional<String> getNamespace() {
        return Optional.ofNullable(namespace);
    }

    @Nonnull
    Optional<String> getLink() {
        return getNamespace().map(ns -> ":" + ns + (ns.isEmpty() ? "" : ":"));
    }

    /**
     * @return true if package is placed under its parent package in wiki, false otherwise. Used when exporting links in
     * parent package
     */
    public boolean isUnderParent() {
        return underParent;
    }
}
