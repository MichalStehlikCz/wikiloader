package com.provys.wikiloader.impl;

import org.sparx.Element;
import org.sparx.Package;
import org.sparx.Repository;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LinkResolver {

    @Nonnull
    private final Repository eaRepository;
    @Nonnull
    private final Map<Integer, Optional<String>> elementIdCache;
    @Nonnull
    private final Map<Integer, Optional<String>> packageNamespaceCache;

    LinkResolver(Repository eaRepository, Package model, String rootNamespace) {
        this.eaRepository = Objects.requireNonNull(eaRepository);
        this.elementIdCache = new ConcurrentHashMap<>(10);
        this.packageNamespaceCache = new ConcurrentHashMap<>(10);
        this.packageNamespaceCache.put(model.GetPackageID(), Optional.of(rootNamespace));
    }

    private Optional<String> calcElementId(Element element) {
        if (element.GetAlias().isEmpty()) {
            // element without alias is not exported -> no link
            return Optional.empty();
        }
        if (element.GetType().equals("Boundary")) {
            // Boundary is not exported, but if it has alias, it links to given package...
            return getPackageNamespace(element.GetPackageID()).map(ns -> ns + ":" + element.GetAlias().toLowerCase() +
                    ":");
        }
        return getPackageNamespace(element.GetPackageID()).map(ns -> ns + ":" + element.GetType().toLowerCase() +
                "_" + element.GetAlias().toLowerCase());
    }

    private Optional<String> calcElementId(int elementId) {
        Element element = eaRepository.GetElementByID(elementId);
        try {
            return calcElementId(element);
        } finally {
            element.destroy();
        }
    }

    /**
     * Get topic Id for given element.
     *
     * @param elementId is EA id of element
     * @return Id of wiki topic, corresponding to given element, empty optional if topic is not exported to wiki
     */
    Optional<String> getElementId(int elementId) {
        return elementIdCache.computeIfAbsent(elementId, this::calcElementId);
    }

    /**
     * Get topic Id for given element.
     *
     * @param element is EA repository element
     * @return Id of wiki topic, corresponding to given element, empty optional if topic is not exported to wiki
     */
    @SuppressWarnings("squid:S2789") // we have map with optional values, comparison with null is ok...
    public Optional<String> getElementId(Element element) {
        var id = elementIdCache.get(element.GetElementID());
        //noinspection OptionalAssignedToNull
        if (id == null) {
            id = calcElementId(element);
            elementIdCache.put(element.GetElementID(), id);
        }
        return id;
    }

    /**
     * Get wiki link to given element. Link is absolute link to appropriate topic
     *
     * @param elementId is EA id of element
     * @return wiki link to given element's topic, empty optional if element does not generate topic to wiki
     */
    Optional<String> getElementLink(int elementId) {
        return getElementId(elementId).map(id -> ":" + id);
    }

    private Optional<String> calcPackageNamespace(Package pkg) {
        if (pkg.GetAlias().isEmpty()) {
            return Optional.empty();
        }
        return getPackageNamespace(pkg.GetParentID()).map(parentns -> parentns + ":" + pkg.GetAlias().toLowerCase());
    }

    private Optional<String> calcPackageNamespace(int packageId) {
        Package pkg = eaRepository.GetPackageByID(packageId);
        try {
            return calcPackageNamespace(pkg);
        } finally {
            pkg.destroy();
        }
    }

    /**
     * Get wiki namespace for given package. Namespace is without leading or trailing :
     *
     * @param pkg is EA package we want to map
     * @return namespace for given package, empty optional if package is not synchronised to wiki
     */
    @Nonnull
    @SuppressWarnings("squid:S2789") // we have map with optional values, comparison with null is ok...
    Optional<String> getPackageNamespace(Package pkg) {
        var namespace = packageNamespaceCache.get(pkg.GetPackageID());
        //noinspection OptionalAssignedToNull
        if (namespace == null) {
            namespace = calcPackageNamespace(pkg);
            packageNamespaceCache.put(pkg.GetPackageID(), namespace);
        }
        return namespace;
    }

    /**
     * Get wiki namespace for given package. Namespace is without leading or trailing :
     *
     * @param packageId is EA id of package
     * @return namespace for given package, empty optional if package is not synchronised to wiki
     */
    // we have map with optional values, comparison with null is ok...
    // we cannot use computeIfAbsent as  it does not allow recursive updates to map
    @SuppressWarnings({"squid:S2789", "squid:S3824"})
    Optional<String> getPackageNamespace(int packageId) {
        var namespace = packageNamespaceCache.get(packageId);
        //noinspection OptionalAssignedToNull
        if (namespace == null) {
            namespace = calcPackageNamespace(packageId);
            packageNamespaceCache.put(packageId, namespace);
        }
        return namespace;
    }

    /**
     * Get wiki link to given package. Link is absolute and ends with : (is translated to start page by wiki if used as
     * it is)
     *
     * @param packageId is EA id of package
     * @return wiki link to given package, empty optional if package is not synchronised to wiki
     */
    public Optional<String> getPackageLink(int packageId) {
        return getPackageNamespace(packageId).map(ns -> ":" + ns + (ns.isEmpty() ? "" : ":"));
    }

    /**
     * Function takes internal name of entity and resolves id of document describing this entity
     */
    public Optional<String> getEntityLink(String entityNm) {
        return Optional.empty();
    }

    /**
     * Function takes internal name of reference table and resolves id of document describing this reference table
     */
    public Optional<String> getRefTabLink(String refTabNm) {
        return Optional.empty();
    }

    /**
     * Function takes internal name of parameter and resolves id of document describing this parameter
     */
    public Optional<String> getParameterLink(String parameterNm) {
        return Optional.of(":generated:parameter:" + parameterNm);
    }

    /**
     * Function takes Provys note and resolves items, contained in text (references to entities, reference tables etc.)
     */
    public String formatProvysNote(String note) {
        return note;
    }
}
