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
                parentInfo.isSync());
    }

    static WikiPackage of(int packageId, WikiMap wikiMap) {
        Package pkg = wikiMap.getEaRepository().GetPackageByID(packageId);
        try {
            return of(pkg, wikiMap);
        } finally {
            pkg.destroy();
        }
    }

    @Nullable
    private String namespace;
    private boolean sync;

    WikiPackage(@Nullable String namespace, boolean sync) {
        this.namespace = namespace;
        this.sync = sync;
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
     * @return true if content of namespace is synchronised with wiki, false otherwise
     */
    public boolean isSync() {
        return sync;
    }

}
