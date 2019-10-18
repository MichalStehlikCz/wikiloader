package com.provys.wikiloader.wikimap;

import com.provys.wikiloader.earepository.EaRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Diagram;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.Repository;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class provides mapping between EA packages and elements and wiki namespaces and topics
 */
public class WikiMap {

    private static final Logger LOG = LogManager.getLogger(WikiMap.class);

    @Nonnull
    private final EaRepository eaRepository;
    @Nonnull
    private final Map<Integer, WikiElement> elementMap;
    @Nonnull
    private final Map<Integer, WikiPackage> packageMap;

    /**
     * Create new link resolver
     *
     * @param eaRepository is EA repository, used to resolve package or element id to repository element or package
     * @param model is root package, representing whole model. It has to be mapped in constructor to allow subsequent
     *             mapping of its sub-objects
     * @param rootNamespace is namespace model maps to
     */
    public WikiMap(EaRepository eaRepository, Package model, String rootNamespace) {
        this.eaRepository = Objects.requireNonNull(eaRepository);
        this.elementMap = new ConcurrentHashMap<>(10);
        this.packageMap = new ConcurrentHashMap<>(10);
        this.packageMap.put(model.GetPackageID(), new WikiPackage(rootNamespace, false));
    }

    /**
     * @return EA repository wrapper link resolver is used for
     */
    @Nonnull
    public EaRepository getEaRepository() {
        return eaRepository;
    }

    /**
     * @return EA repository link resolver is used for
     */
    @Nonnull
    Repository getRepository() {
        return eaRepository.getRepository();
    }

    /**
     * Create and return wiki set builder
     */
    @Nonnull
    public WikiSetBuilder getSetBuilder() {
        return new WikiSetBuilderImpl(this);
    }

    /**
     * Get information about element
     *
     * @param elementId is EA id of element
     * @return information about element mapping to wiki
     */
    @Nonnull
    private WikiElement getWikiElement(int elementId) {
        return elementMap.computeIfAbsent(elementId, id -> WikiElement.of(id, this));
    }

    /**
     * Get information about element
     *
     * @param element is EA repository element
     * @return information about element mapping to wiki
     */
    @Nonnull
    public WikiElement getWikiElement(Element element) {
        var info = elementMap.get(element.GetElementID());
        if (info == null) {
            info = WikiElement.of(element, this);
            elementMap.put(element.GetElementID(), info);
        }
        return info;
    }

    /**
     * Get wiki link to given element. Link is absolute link to appropriate topic
     *
     * @param elementId is EA id of element
     * @return wiki link to given element's topic, empty optional if element does not generate topic to wiki
     */
    @Nonnull
    public Optional<String> getElementLink(int elementId) {
        return getWikiElement(elementId).getLink();
    }

    /**
     * Get element namespace; return empty optional if element does not map to namespace
     *
     * @param elementId is EA is of element
     * @return namespace without leading or trailing : if element maps to namespace, empty optional otherwise
     */
    public Optional<String> getElementNamespace(int elementId) {
        return getWikiElement(elementId).getNamespace();
    }

    /**
     * Put information about package mapping to cache. Used to provide mapping for top level packages. Should be done
     * right after creation of cache, as change does not affect entries, that might have been created based on original
     * mapping
     *
     * @param  pkg is EA repository package
     * @param namespace is mapping of package to wiki namespace; null if package is not exported to wiki
     * @return self to allow fluent build
     */
    WikiMap putWikiPackage(Package pkg, String namespace, boolean underParent) {
        var id = pkg.GetPackageID();
        var wikiPackage = new WikiPackage(namespace, underParent);
        var old = packageMap.put(id, wikiPackage);
        if (old != null) {
            LOG.warn("Overwriting existing package mapping {} > {}", old, wikiPackage);
        }
        return this;
    }

    /**
     * Get information about package
     *
     * @param packageId is EA id of package
     * @return information about package mapping to wiki
     */
    @Nonnull
    @SuppressWarnings("squid:S3824") // cannot replace get + if with computeIfAbsent, as it does not allow recursive
    // manipulation with map content
    WikiPackage getWikiPackage(int packageId) {
        var info = packageMap.get(packageId);
        if (info == null) {
            info = WikiPackage.of(packageId, this);
            packageMap.put(packageId, info);
        }
        return info;
    }

    /**
     * Get information about package
     *
     * @param pkg is EA repository package
     * @return information about package mapping to wiki
     */
    @Nonnull
    public WikiPackage getWikiPackage(Package pkg) {
        var info = packageMap.get(pkg.GetPackageID());
        if (info == null) {
            info = WikiPackage.of(pkg, this);
            packageMap.put(pkg.GetPackageID(), info);
        }
        return info;
    }

    /**
     * Get wiki namespace for given package. Namespace is without leading or trailing :
     *
     * @param pkg is EA package we want to map
     * @return namespace for given package, empty optional if package is not synchronised to wiki
     */
    @Nonnull
    public Optional<String> getPackageNamespace(Package pkg) {
        return getWikiPackage(pkg).getNamespace();
    }

    /**
     * Get wiki namespace for given package. Namespace is without leading or trailing :
     *
     * @param packageId is EA id of package
     * @return namespace for given package, empty optional if package is not synchronised to wiki
     */
    @Nonnull
    public Optional<String> getPackageNamespace(int packageId) {
        return getWikiPackage(packageId).getNamespace();
    }

    /**
     * Get wiki link to given package. Link is absolute and ends with : (is translated to start page by wiki if used as
     * it is)
     *
     * @param packageId is EA id of package
     * @return wiki link to given package, empty optional if package is not synchronised to wiki
     */
    @Nonnull
    public Optional<String> getPackageLink(int packageId) {
        return getWikiPackage(packageId).getLink();
    }

    /**
     * Constructs topic name for diagram
     *
     * @param diagram is diagram for which we want to get topic name
     * @return topic name (valid wiki topic name that will be sued for given diagram)
     */
    public static String getDiagramName(Diagram diagram) {
        return "dia_" + diagram.GetName().toLowerCase().replace(' ', '_')
                .replace("&", "and").replaceAll("[():]", "");
    }

    /**
     * Get link to wiki topic corresponding to supplied diagram
     *
     * @param diagram is EA repository diagram
     * @return link to topic corresponding to diagram, empty if diagram is not exported to wiki
     */
    @Nonnull
    Optional<String> getDiagramLink(Diagram diagram) {
        Optional<String> namespace;
        if (diagram.GetParentID() > 0) {
            namespace = getElementNamespace(diagram.GetParentID());
        } else {
            namespace = getPackageNamespace(diagram.GetPackageID());
        }
        if (namespace.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(":" + namespace.get() + ":" + getDiagramName(diagram));
    }

    /**
     * Function takes internal name of entity and resolves id of document describing this entity
     */
    @Nonnull
    public Optional<String> getEntityLink(String entityNm) {
        return Optional.empty();
    }

    /**
     * Function takes internal name of reference table and resolves id of document describing this reference table
     */
    @Nonnull
    public Optional<String> getRefTabLink(String refTabNm) {
        return Optional.empty();
    }

    /**
     * Function takes internal name of parameter and resolves id of document describing this parameter
     */
    @Nonnull
    public Optional<String> getParameterLink(String parameterNm) {
        return Optional.of(":generated:parameter:" + parameterNm);
    }

    /**
     * Function takes Provys note and resolves items, contained in text (references to entities, reference tables etc.)
     */
    @Nonnull
    public String formatProvysNote(String note) {
        return note;
    }
}
