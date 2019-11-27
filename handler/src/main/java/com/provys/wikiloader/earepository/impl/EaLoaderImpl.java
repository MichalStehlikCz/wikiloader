package com.provys.wikiloader.earepository.impl;

import com.provys.catalogue.api.CatalogueRepository;
import com.provys.common.exception.InternalException;
import com.provys.common.exception.RegularException;
import com.provys.wikiloader.earepository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.sparx.*;
import org.sparx.Collection;
import org.sparx.Package;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Interface for accessing Enterprise Architect repository and retrieve Ref objects from this repository - the only
 * class that actually interact with Enterprise Architect COM interface
 */
@ApplicationScoped
class EaLoaderImpl {

    private static final Logger LOG = LogManager.getLogger(EaLoaderImpl.class);
    /** Name of model package */
    private static final String MODEL_NAME = "Product Model";
    /** Name of root namespace in wiki */
    private static final String ROOT_NAMESPACE = "eamodel";

    @Nonnull
    private final Repository repository;
    @Nonnull
    private final CatalogueRepository catalogue;

    /**
     * Creates loader, using default repository opened via parameters, retrieved from configuration
     */
    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    EaLoaderImpl(CatalogueRepository catalogue) {
        repository = new Repository();
        Config config = ConfigProvider.getConfig();
        String eaAddress = config
                .getValue("EA_ADDRESS", String.class);
        // Attempt to open the provided file
        LOG.debug("Open Enterprise Architect repository {}", eaAddress);
        if (!repository.OpenFile(eaAddress)) {
            // If the file couldn't be opened then notify the user
            throw new RegularException(LOG, "EALOADER_CANNOTOPENREPOSITORY",
                    "Enterprise Architect was unable to open the file '" + eaAddress + '\'');
        }
        LOG.debug("Enterprise architect repository opened");
        this.catalogue = Objects.requireNonNull(catalogue);
    }

    EaLoaderImpl(Repository repository, CatalogueRepository catalogue) {
        this.repository = Objects.requireNonNull(repository);
        this.catalogue = Objects.requireNonNull(catalogue);
    }

    @Nonnull
    EaDefaultPackageRef getModel(EaRepositoryImpl eaRepository) {
        Collection<Package> models = repository.GetModels();
        try {
            var model = models.GetByName(MODEL_NAME);
            try {
                return new EaDefaultPackageRef(eaRepository, null, MODEL_NAME, ROOT_NAMESPACE,
                        model.GetStereotypeEx(), model.GetTreePos(), model.GetPackageID());
            } finally {
                model.destroy();
            }
        } finally {
            models.destroy();
        }
    }

    @Nonnull
    private EaObjectRefBase getElementParent(Element element, EaRepositoryImpl eaRepository) {
        var parentElement = element.GetParentID();
        return (parentElement > 0) ? eaRepository.getElementRefById(parentElement) :
                eaRepository.getPackageRefById(element.GetPackageID());
    }

    private EaUmlDiagramElementRef createRefUmlDiagramElement(Element element, EaRepositoryImpl eaRepository) {
        Diagram diagram = (Diagram) element.GetCompositeDiagram();
        EaDefaultDiagramRef diagramRef = null;
        if (diagram == null) {
            LOG.info("Linked diagram missing in UMLDiagram element {}", element::GetName);
        } else {
            diagramRef = eaRepository.getDiagramRefById(diagram.GetDiagramID());
        }
        return new EaUmlDiagramElementRef(eaRepository, getElementParent(element, eaRepository), element.GetName(),
                element.GetTreePos(), element.GetElementID(), diagramRef);
    }

    private EaGroupRef loadEaGroupRef(Element element, EaRepositoryImpl eaRepository) {
        return new EaGroupRef(eaRepository, getElementParent(element, eaRepository), element.GetName(),
                element.GetAlias(), element.GetType(), element.GetTreePos(), element.GetElementID());
    }

    private EaProductPackageRef loadEaProductPackageRef(Element element, EaRepositoryImpl eaRepository) {
        return new EaProductPackageRef(eaRepository, getElementParent(element, eaRepository), element.GetName(),
                element.GetAlias(), element.GetTreePos(), element.GetElementID());
    }

    private EaTechnicalPackageRef loadEaTechnicalPackageRef(Element element, EaRepositoryImpl eaRepository) {
        return new EaTechnicalPackageRef(eaRepository, getElementParent(element, eaRepository), element.GetName(),
                element.GetAlias(), element.GetTreePos(), element.GetElementID());
    }

    private EaDefaultElementRef createEaRefDefaultElement(Element element, EaRepositoryImpl eaRepository) {
        boolean leaf = true;
        var subElements = element.GetElements();
        if (subElements.GetCount() > 0) {
            leaf = false;
        }
        subElements.destroy();
        var diagrams = element.GetDiagrams();
        if (diagrams.GetCount() > 0) {
            leaf = false;
        }
        diagrams.destroy();
        return new EaDefaultElementRef(eaRepository, getElementParent(element, eaRepository), element.GetName(),
                element.GetAlias(), element.GetType(), element.GetStereotype(), element.GetTreePos(),
                element.GetElementID(), leaf);
    }

    /**
     * Create element from supplied Enterprise Architect repository element
     *
     * @param elementId is Enterprise Architect repository element identifier
     * @return new element reference
     */
    EaElementRefBase elementRefFromId(int elementId, EaRepositoryImpl eaRepository) {
        var element = repository.GetElementByID(elementId);
        try {
            if (element.GetType().equals("UMLDiagram")) {
                return createRefUmlDiagramElement(element, eaRepository);
            } else if (element.GetType().equals("Boundary") || element.GetType().equals("Grouping")) {
                return loadEaGroupRef(element, eaRepository);
            } else if (element.GetStereotype().equals("ArchiMate_Product")) {
                return loadEaProductPackageRef(element, eaRepository);
            } else if (element.GetStereotype().equals("ArchiMate_ApplicationComponent")
                    && eaRepository.getPackageRefById(element.GetPackageID())
                    .getStereotype()
                    .filter(stereotype -> stereotype.equals("provys_technical_package_group"))
                    .isPresent()) {
                return loadEaTechnicalPackageRef(element, eaRepository);
            } else {
                return createEaRefDefaultElement(element, eaRepository);
            }
        } finally {
            element.destroy();
        }
    }

    private EaProductPackageGroupRef loadEaProductPackageGroupRef(Package pkg, EaRepositoryImpl eaRepository) {
        var parentId = pkg.GetParentID();
        var parent = (parentId > 0) ? eaRepository.getPackageRefById(parentId) : null;
        return new EaProductPackageGroupRef(eaRepository, parent, pkg.GetName(), pkg.GetAlias(), pkg.GetStereotypeEx(),
                pkg.GetTreePos(), pkg.GetPackageID());
    }

    private EaItemGroupRef loadEaTechnicalPackageGroupRef(Package pkg, EaRepositoryImpl eaRepository) {
        var parentId = pkg.GetParentID();
        var parent = (parentId > 0) ? eaRepository.getPackageRefById(parentId) : null;
        return new EaTechnicalPackageGroupRef(eaRepository, parent, pkg.GetName(), pkg.GetAlias(),
                pkg.GetStereotypeEx(), pkg.GetTreePos(), pkg.GetPackageID());
    }

    private EaDefaultPackageRef loadEaDefaultPackageRef(Package pkg, EaRepositoryImpl eaRepository) {
        var parentId = pkg.GetParentID();
        var parent = (parentId > 0) ? eaRepository.getPackageRefById(parentId) : null;
        return new EaDefaultPackageRef(eaRepository, parent, pkg.GetName(), pkg.GetAlias(), pkg.GetStereotypeEx(),
                pkg.GetTreePos(), pkg.GetPackageID());
    }

    /**
     * Create package from supplied Enterprise Architect repository package
     *
     * @param packageId is Enterprise Architect repository package identifier
     * @return new package reference
     */
    EaDefaultPackageRef packageRefFromId(int packageId, EaRepositoryImpl eaRepository) {
        var pkg = repository.GetPackageByID(packageId);
        try {
            if (pkg.GetStereotypeEx().equals("provys_product_package_group")) {
                return loadEaProductPackageGroupRef(pkg, eaRepository);
            } else if (pkg.GetStereotypeEx().equals("provys_technical_package_group")) {
                return loadEaTechnicalPackageGroupRef(pkg, eaRepository);
            }
            if (!pkg.GetStereotypeEx().isEmpty()) {
                LOG.debug("Unrecognized stereotype {} in package {}", pkg::GetStereotypeEx, pkg::GetName);
            }
            return loadEaDefaultPackageRef(pkg, eaRepository);
        } finally {
            pkg.destroy();
        }
    }

    /**
     * Create diagram from supplied Enterprise Architect repository diagram
     *
     * @param diagramId is Enterprise Architect repository diagram identifier
     * @return new diagram reference
     */
    EaDefaultDiagramRef diagramRefFromId(int diagramId, EaRepositoryImpl eaRepository) {
        var diagram = repository.GetDiagramByID(diagramId);
        try {
            var parentElement = diagram.GetParentID();
            var parent = (parentElement > 0) ? eaRepository.getElementRefById(parentElement) :
                    eaRepository.getPackageRefById(diagram.GetPackageID());
            return new EaDefaultDiagramRef(eaRepository, parent, diagram.GetName(), diagram.GetStereotype(), diagramId);
        } finally {
            diagram.destroy();
        }
    }

    /**
     * Get child package with specified alias.
     *
     * @param parent is parent package
     * @param alias  is required alias
     * @return package if one exists, empty optional if no such package is found
     */
    private Optional<Package> getChildPackageByAlias(Package parent, String alias) {
        Collection<Package> children = parent.GetPackages();
        try {
            for (var child : children) {
                if (child.GetAlias().equalsIgnoreCase(alias)) {
                    return Optional.of(child);
                }
                child.destroy();
            }
        } finally {
            children.destroy();
        }
        return Optional.empty();
    }

    /**
     * Get element with given alias from collection
     */
    private Optional<Element> getElementByAlias(Collection<Element> elements, String alias) {
        for (var element : elements) {
            if (element.GetAlias().equalsIgnoreCase(alias)) {
                return Optional.of(element);
            }
            element.destroy();
        }
        return Optional.empty();
    }

    /**
     * Get element in package with specified alias.
     *
     * @param pkg   is package
     * @param alias is required alias
     * @return element if one exists, empty optional if no such element is found
     */
    private Optional<Element> getElementByAlias(Package pkg, String alias) {
        Collection<Element> elements = pkg.GetElements();
        try {
            return getElementByAlias(elements, alias);
        } finally {
            elements.destroy();
        }
    }

    /**
     * Get child element with specified alias.
     *
     * @param parent is parent element
     * @param alias  is required alias
     * @return element if one exists, empty optional if no such element is found
     */
    private Optional<Element> getChildElementByAlias(Element parent, String alias) {
        Collection<Element> elements = parent.GetElements();
        try {
            return getElementByAlias(elements, alias);
        } finally {
            elements.destroy();
        }
    }

    /**
     * Retrieve ref object corresponding to supplied path
     *
     * @param path is path, with aliases divided by :
     * @param eaRepository is repository from which resulting object should be taken
     * @return ref object that corresponds to supplied path, throw exception when such object is not found
     */
    EaObjectRef getRefObjectByPath(@Nullable String path, EaRepositoryImpl eaRepository) {
        LOG.debug("Lookup page {}", path);
        Package rootPackage = repository.GetPackageByID(getModel(eaRepository).getPackageId());
        Element rootElement = null;
        try {
            if (path != null) {
                // we will parse path and try to find appropriate package or element
                for (var part : path.split(":")) {
                    if (rootPackage != null) {
                        // first try to find subpackage
                        var pkg = getChildPackageByAlias(rootPackage, part);
                        if (pkg.isPresent()) {
                            rootPackage.destroy();
                            rootPackage = pkg.get();
                        } else {
                            // if not successful, check if there is appropriate element
                            var element = getElementByAlias(rootPackage, part);
                            rootPackage.destroy();
                            rootPackage = null;
                            rootElement = element
                                    .orElseThrow(
                                            () -> new InternalException(LOG, "Package or element " + part + " not found"));
                        }
                    } else {
                        assert rootElement != null;
                        var element = getChildElementByAlias(rootElement, part);
                        rootElement.destroy();
                        rootElement = element
                                .orElseThrow(() -> new InternalException(LOG, "Element " + part + " not found"));
                    }
                }
            }
            if (rootElement != null) {
                LOG.debug("Found element {}", rootElement::GetName);
                return eaRepository.getElementRefById(rootElement.GetElementID());
            }
            assert rootPackage != null;
            LOG.debug("Found package {}", rootPackage::GetName);
            return eaRepository.getPackageRefById(rootPackage.GetPackageID());
        } finally {
            if (rootPackage != null) {
                rootPackage.destroy();
            }
            if (rootElement != null) {
                rootElement.destroy();
            }
        }
    }

    private static List<EaDiagramRef> getDiagrams(Supplier<Collection<Diagram>> diagramSrc, EaRepository eaRepository) {
        var diagrams = diagramSrc.get();
        try {
            var result = new ArrayList<EaDiagramRef>(diagrams.GetCount());
            for (var diagram : diagrams) {
                try {
                    result.add(eaRepository.getDiagramRefById(diagram.GetDiagramID()));
                } finally {
                    diagram.destroy();
                }
            }
            return result;
        } finally {
            diagrams.destroy();
        }
    }

    private static boolean hasDiagrams(Supplier<Collection<Diagram>> diagramSrc) {
        var diagrams = diagramSrc.get();
        try {
            return (diagrams.GetCount() > 0);
        } finally {
            diagrams.destroy();
        }
    }

    private static List<com.provys.wikiloader.earepository.EaPackageRef> getPackages(Supplier<Collection<Package>> packageSrc, EaRepository eaRepository) {
        var packages = packageSrc.get();
        try {
            var result = new ArrayList<com.provys.wikiloader.earepository.EaPackageRef>(packages.GetCount());
            for (var pkg : packages) {
                try {
                    result.add(eaRepository.getPackageRefById(pkg.GetPackageID()));
                } finally {
                    pkg.destroy();
                }
            }
            return result;
        } finally {
            packages.destroy();
        }
    }

    private static List<EaElementRef> getElements(Supplier<Collection<Element>> elementSrc, EaRepository eaRepository) {
        var elements = elementSrc.get();
        try {
            var result = new ArrayList<EaElementRef>(elements.GetCount());
            for (var element : elements) {
                try {
                    result.add(eaRepository.getElementRefById(element.GetElementID()));
                } finally {
                    element.destroy();
                }
            }
            return result;
        } finally {
            elements.destroy();
        }
    }

    private static boolean hasElements(Supplier<Collection<Element>> elementSrc) {
        var elements = elementSrc.get();
        try {
            return (elements.GetCount() > 0);
        } finally {
            elements.destroy();
        }
    }

    EaDefaultDiagram loadDefaultDiagram(EaDiagramRef diagramRef) {
        var diagram = repository.GetDiagramByID(diagramRef.getDiagramId());
        try {
            var diagramLoader = new EaLoaderDiagram(diagram, diagramRef.getRepository());
            return new EaDefaultDiagram(diagramRef, diagram.GetNotes(), diagramLoader.getDiagram(),
                    diagramLoader.getDiagramObjects());
        } finally {
            diagram.destroy();
        }
    }

    private static <E extends EaDefaultElementRef> List<E> getPackageGroupElements(Package pkg,
                                                                                   EaItemGroupRef packageGroupRef,
                                                                                   Class<E> clazz) {
        return getElements(pkg::GetElements, packageGroupRef.getRepository())
                .stream()
                .filter(elementRef -> (!elementRef.isIgnoredType())) // we can safely ignore boundaries and similar
                .collect(Collectors.collectingAndThen(
                        Collectors.partitioningBy(clazz::isInstance),
                        map -> {
                            if (map.get(false) != null) { // we will log elements different than product packages
                                map.get(false).forEach(element -> LOG.warn(
                                        "Element {} of type different than product package is ignored in " +
                                                "package group {}",
                                        element::getName, packageGroupRef::getName));
                            }
                            return map.get(true).stream() // and only register product packages
                                    .map(clazz::cast)
                                    .collect(Collectors.toList());
                        }
                ));
    }

    private static <G extends EaItemGroupRef> List<G> getPackageGroupPackages(Package pkg, G packageGroupRef,
                                                                              Class<G> clazz) {
        return getPackages(pkg::GetPackages, packageGroupRef.getRepository())
                .stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.partitioningBy(clazz::isInstance),
                        map -> {
                            if (map.get(false) != null) { // we will log elements different than product packages
                                map.get(false).forEach(pack -> LOG.warn(
                                        "Subpackage {} of type different than package group is ignored in " +
                                                "package group {}",
                                        pack::getName, packageGroupRef::getName));
                            }
                            return map.get(true).stream() // and only register product packages
                                    .map(clazz::cast)
                                    .collect(Collectors.toList());
                        }
                ));
    }

    @Nonnull
    EaProductPackageGroup loadProductPackageGroup(EaProductPackageGroupRef packageGroupRef) {
        var pkg = repository.GetPackageByID(packageGroupRef.getPackageId());
        try {
            return new EaProductPackageGroup(packageGroupRef, pkg.GetNotes(),
                    getDiagrams(pkg::GetDiagrams, packageGroupRef.getRepository()),
                    getPackageGroupElements(pkg, packageGroupRef, EaProductPackageRef.class),
                    getPackageGroupPackages(pkg, packageGroupRef, EaProductPackageGroupRef.class));
        } finally {
            pkg.destroy();
        }
    }

    @Nonnull
    EaTechnicalPackageGroup loadTechnicalPackageGroup(EaTechnicalPackageGroupRef packageGroupRef) {
        var pkg = repository.GetPackageByID(packageGroupRef.getPackageId());
        try {
            return new EaTechnicalPackageGroup(packageGroupRef, pkg.GetNotes(),
                    getDiagrams(pkg::GetDiagrams, packageGroupRef.getRepository()),
                    getPackageGroupElements(pkg, packageGroupRef, EaTechnicalPackageRef.class),
                    getPackageGroupPackages(pkg, packageGroupRef, EaTechnicalPackageGroupRef.class));
        } finally {
            pkg.destroy();
        }
    }

    @Nonnull
    EaParentBase loadDefaultPackage(EaPackageRef packageRef) {
        var pkg = repository.GetPackageByID(packageRef.getPackageId());
        try {
            return new EaParent(packageRef, pkg.GetNotes(),
                    getDiagrams(pkg::GetDiagrams, packageRef.getRepository()),
                    getElements(pkg::GetElements, packageRef.getRepository()),
                    getPackages(pkg::GetPackages, packageRef.getRepository()));
        } finally {
            pkg.destroy();
        }
    }

    private List<EaTechnicalPackageRef> getProductPackageTechnicalPackages(Element element, EaRepository eaRepository) {
        var result = new ArrayList<EaTechnicalPackageRef>(10);
        var connectors = element.GetConnectors();
        try {
            for (var connector : connectors) {
                try {
                    if (connector.GetStereotype().equals("ArchiMate_Aggregation")) {
                        var elementRef = eaRepository.getElementRefById(connector.GetSupplierID());
                        if (elementRef instanceof EaTechnicalPackageRef) {
                            result.add((EaTechnicalPackageRef) elementRef);
                        } else {
                            LOG.warn("Unexpected aggregation relation from product package {} to {}",
                                    element::GetName, elementRef::getEaDesc);
                        }
                    }
                } finally {
                    connector.destroy();
                }
            }
        } finally {
            connectors.destroy();
        }
        result.sort(null);
        return result;
    }

    @Nonnull
    EaProductPackage loadProductPackage(EaProductPackageRef elementRef) {
        var element = repository.GetElementByID(elementRef.getElementId());
        try {
            var diagrams = getDiagrams(element::GetDiagrams, elementRef.getRepository());
            if (hasElements(element::GetElements)) {
                LOG.warn("Elements under ArchiMate_Product element {} are ignored - Product Package should be" +
                        " leaf", element::GetName);
            }
            return new EaProductPackage(elementRef, element.GetNotes(), diagrams,
                    getProductPackageTechnicalPackages(element, elementRef.getRepository()));
        } finally {
            element.destroy();
        }
    }

    private <T> List<T> getRelElements(Element element, EaRepository eaRepository, boolean fromSupplier,
                                       String type, String stereotype, Class<T> relType) {
        var result = new ArrayList<T>(10);
        var elementId = element.GetElementID();
        var connectors = element.GetConnectors();
        try {
            for (var connector : connectors) {
                try {
                    if (((fromSupplier ? connector.GetSupplierID() : connector.GetClientID()) == elementId) &&
                            (connector.GetType().equals(type)) &&
                            (connector.GetStereotype().equals(stereotype))) {
                        var elementRef = eaRepository.getElementRefById(
                                fromSupplier ? connector.GetClientID() : connector.GetSupplierID());
                        if (relType.isInstance(elementRef)) {
                            result.add(relType.cast(elementRef));
                        } else {
                            if (fromSupplier) {
                                LOG.warn("Unexpected {} relation from {} to {} {}", () -> stereotype,
                                        elementRef::getEaDesc, relType::getCanonicalName, element::GetName);
                            } else {
                                LOG.warn("Unexpected {} relation from {} {} to {}", () -> stereotype,
                                        relType::getCanonicalName, element::GetName, elementRef::getEaDesc);
                            }
                        }
                    }
                } finally {
                    connector.destroy();
                }
            }
        } finally {
            connectors.destroy();
        }
        result.sort(null);
        return result;
    }

    private List<EaElementRef> getTechnicalPackageFunctions(Element element, EaRepository eaRepository) {
        return getRelElements(element, eaRepository, false, "Association",
                "ArchiMate_Association", EaElementRef.class);
    }

    private List<EaProductPackageRef> getTechnicalPackageContainedIn(Element element, EaRepository eaRepository) {
        return getRelElements(element, eaRepository, true, "Association",
                "ArchiMate_Aggregation", EaProductPackageRef.class);
    }

    private List<EaTechnicalPackageRef> getTechnicalPackagePrerequisities(Element element, EaRepository eaRepository) {
        return getRelElements(element, eaRepository, true, "Dependency", "",
                EaTechnicalPackageRef.class);
    }

    @Nonnull
    EaTechnicalPackage loadTechnicalPackage(EaTechnicalPackageRef elementRef) {
        var element = repository.GetElementByID(elementRef.getElementId());
        try {
            var diagrams = getDiagrams(element::GetDiagrams, elementRef.getRepository());
            if (hasElements(element::GetElements)) {
                LOG.warn("Elements under ArchiMate_Product element {} are ignored - Product Package should be" +
                        " leaf", element::GetName);
            }
            return new EaTechnicalPackage(elementRef, element.GetNotes(), diagrams,
                    getTechnicalPackageFunctions(element, elementRef.getRepository()),
                    getTechnicalPackageContainedIn(element, elementRef.getRepository()),
                    getTechnicalPackagePrerequisities(element, elementRef.getRepository()));
        } finally {
            element.destroy();
        }
    }

    private EaObject loadDataObject(EaElementRef elementRef) {
        var element = repository.GetElementByID(elementRef.getElementId());
        try {
            if (hasDiagrams(element::GetDiagrams)) {
                LOG.warn("Diagrams under ArchiMate_DataObject element {} are ignored - DataObject should be" +
                        " leaf", element::GetName);
            }
            if (hasElements(element::GetElements)) {
                LOG.warn("Elements under ArchiMate_DataObject element {} are ignored - DataObject should be" +
                        " leaf", element::GetName);
            }
            var entity = elementRef.getAlias()
                    .map(String::toUpperCase)
                    .flatMap(alias -> catalogue.getEntityManager().getByNameNmIfExists(alias))
                    .orElse(null);
            return new EaDataObject(elementRef, entity, element.GetNotes());
        } finally {
            element.destroy();
        }
    }

    private EaObject loadDefaultElement(EaElementRef elementRef) {
        var element = repository.GetElementByID(elementRef.getElementId());
        try {
            var diagrams = getDiagrams(element::GetDiagrams, elementRef.getRepository());
            var elements = getElements(element::GetElements, elementRef.getRepository());
            if (diagrams.isEmpty() && elements.isEmpty()) {
                return new EaObjectRegular(elementRef, element.GetNotes());
            } else {
                return new EaParent(elementRef, element.GetNotes(), diagrams, elements, null);
            }
        } finally {
            element.destroy();
        }
    }

    @Nonnull
    EaObject loadElement(EaElementRef elementRef) {
        switch (elementRef.getStereotype().orElse("")) {
            case "ArchiMate_DataObject":
                return loadDataObject(elementRef);
            default:
                return loadDefaultElement(elementRef);
        }
    }
}
