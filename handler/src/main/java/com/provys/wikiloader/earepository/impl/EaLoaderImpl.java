package com.provys.wikiloader.earepository.impl;

import com.provys.catalogue.api.CatalogueRepository;
import com.provys.common.exception.InternalException;
import com.provys.common.exception.RegularException;
import com.provys.wikiloader.earepository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.*;
import org.sparx.Collection;
import org.sparx.Package;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Interface for accessing Enterprise Architect repository and retrieve Ref objects from this repository - the only
 * class that actually interact with Enterprise Architect COM interface
 */
@Component
class EaLoaderImpl implements EaLoader {

    private static final Logger LOG = LogManager.getLogger(EaLoaderImpl.class);

    @Nonnull
    private final Repository repository;
    @Nonnull
    private final CatalogueRepository catalogue;

    /**
     * Creates loader, using default repository opened via parameters, retrieved from configuration
     */
    @Autowired
    EaLoaderImpl(EaLoaderConfiguration configuration, CatalogueRepository catalogue) {
        repository = new Repository();
        String eaAddress = configuration.getAddress();
        String eaUser = configuration.getUser();
        // Attempt to open the provided file
        LOG.debug("Open Enterprise Architect repository {}", eaAddress);
        if (eaUser.isEmpty()) {
            if (!repository.OpenFile(eaAddress)) {
                // If the file couldn't be opened then notify the user
                throw new RegularException("EALOADER_CANNOTOPENREPOSITORY",
                        "Enterprise Architect was unable to open the file '" + eaAddress + '\'');
            }
        } else {
            if (!repository.OpenFile2(eaAddress, eaUser, configuration.getPwd())) {
                // If the file couldn't be opened then notify the user
                throw new RegularException("EALOADER_CANNOTOPENREPOSITORY",
                        "Enterprise Architect was unable to open the file '" + eaAddress + "', user " + eaUser);
            }
        }
        LOG.debug("Enterprise architect repository opened");
        this.catalogue = Objects.requireNonNull(catalogue);
    }

    EaLoaderImpl(Repository repository, CatalogueRepository catalogue) {
        this.repository = Objects.requireNonNull(repository);
        this.catalogue = Objects.requireNonNull(catalogue);
    }

    @Override
    @Nonnull
    public EaDefaultPackageRef getModel(EaModel model, EaRepositoryImpl eaRepository) {
        Collection<Package> models = repository.GetModels();
        try {
            var eaModel = models.GetByName(model.getName());
            try {
                return new EaDefaultPackageRef(eaRepository, null, model.getName(), model.getWikiNamespace(),
                        eaModel.GetStereotypeEx(), eaModel.GetTreePos(), eaModel.GetPackageID());
            } finally {
                eaModel.destroy();
            }
        } finally {
            models.destroy();
        }
    }

    @Nonnull
    private EaObjectRef getElementParent(Element element, EaRepositoryImpl eaRepository) {
        var parentElement = element.GetParentID();
        return (parentElement > 0) ? eaRepository.getElementRefById(parentElement) :
                eaRepository.getPackageRefById(element.GetPackageID());
    }

    private EaUmlDiagramElementRef createRefUmlDiagramElement(Element element, EaRepositoryImpl eaRepository) {
        var diagramId = element.MiscData(0);
        EaDefaultDiagramRef diagramRef = null;
        if ((diagramId == null) || (diagramId.isEmpty())) {
            LOG.warn("Linked diagram missing in UMLDiagram element {}", element::GetName);
        } else {
            try {
                diagramRef = eaRepository.getDiagramRefById(Integer.parseInt(diagramId));
            } catch (Exception e) {
                LOG.warn("Cannot retrieve linked diagram for UMLDiagram element {}", element::GetName);
                diagramRef = null;
            }
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

    private EaMeaningRef loadEaMeaningRef(Element element, EaRepositoryImpl eaRepository) {
        boolean leaf = true;
        var subElements = element.GetElements();
        try {
            if (subElements.GetCount() > 0) {
                leaf = false;
            }
        } finally {
            subElements.destroy();
        }
        if (leaf) {
            return new EaMeaningItemRef(eaRepository, getElementParent(element, eaRepository), element.GetName(),
                    element.GetAlias(), element.GetTreePos(), element.GetElementID());
        } else {
            return new EaMeaningGroupRef(eaRepository, getElementParent(element, eaRepository), element.GetName(),
                    element.GetAlias(), element.GetTreePos(), element.GetElementID());
        }

    }

    private EaElementRefBase loadEaFunctionRef(Element element, EaRepositoryImpl eaRepository) {
        boolean leaf = true;
        var subElements = element.GetElements();
        try {
            if (subElements.GetCount() > 0) {
                leaf = false;
            }
        } finally {
            subElements.destroy();
        }
        if (leaf) {
            return new EaFunctionTaskRef(eaRepository, getElementParent(element, eaRepository), element.GetName(),
                    element.GetAlias(), element.GetTreePos(), element.GetElementID());
        } else {
            return new EaFunctionAbstractRef(eaRepository, getElementParent(element, eaRepository), element.GetName(),
                    element.GetAlias(), element.GetTreePos(), element.GetElementID());
        }
    }

    private EaDataObjectRef loadEaDataObjectRef(Element element, EaRepositoryImpl eaRepository) {
        return new EaDataObjectRef(eaRepository, getElementParent(element, eaRepository), element.GetName(),
                element.GetAlias(), element.GetTreePos(), element.GetElementID());
    }

    private EaElementRefBase loadEaDefaultElementRef(Element element, EaRepositoryImpl eaRepository) {
        boolean leaf = true;
        var subElements = element.GetElements();
        try {
            if (subElements.GetCount() > 0) {
                leaf = false;
            }
        } finally {
            subElements.destroy();
        }
        if (leaf) {
            return new EaLeafElementRef(eaRepository, getElementParent(element, eaRepository), element.GetName(),
                    element.GetAlias(), element.GetType(), element.GetStereotype(), element.GetTreePos(),
                    element.GetElementID());
        } else {
            return new EaNamespaceElementRef(eaRepository, getElementParent(element, eaRepository), element.GetName(),
                    element.GetAlias(), element.GetType(), element.GetStereotype(), element.GetTreePos(),
                    element.GetElementID());
        }
    }

    /**
     * Create element from supplied Enterprise Architect repository element
     *
     * @param elementId is Enterprise Architect repository element identifier
     * @return new element reference
     */
    @Override
    public EaElementRef elementRefFromId(int elementId, EaRepositoryImpl eaRepository) {
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
            } else if (element.GetStereotype().equals("ArchiMate_Meaning")) {
                return loadEaMeaningRef(element, eaRepository);
            } else if (element.GetStereotype().equals("ArchiMate_BusinessService") &&
                    (eaRepository.getPackageRefById(element.GetPackageID()).getModel() == EaModel.PRODUCT_MODEL)) {
                return loadEaFunctionRef(element, eaRepository);
            } else if (element.GetStereotype().equals("ArchiMate_DataObject") &&
                    (eaRepository.getPackageRefById(element.GetPackageID()).getModel() == EaModel.PRODUCT_MODEL)) {
                return loadEaDataObjectRef(element, eaRepository);
            } else {
                return loadEaDefaultElementRef(element, eaRepository);
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

    private EaItemGroupRef<EaTechnicalPackageRef, EaTechnicalPackageGroupRef, EaTechnicalPackageGroup>
    loadEaTechnicalPackageGroupRef(Package pkg, EaRepositoryImpl eaRepository) {
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
    @Override
    public EaDefaultPackageRef packageRefFromId(int packageId, EaRepositoryImpl eaRepository) {
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
    @Override
    public EaDefaultDiagramRef diagramRefFromId(int diagramId, EaRepositoryImpl eaRepository) {
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

    @Nonnull
    @Override
    public EaObjectRef getRefObjectByPath(EaModel model, @Nullable String path, EaRepositoryImpl eaRepository) {
        LOG.debug("Lookup page {}", path);
        Package rootPackage = repository.GetPackageByID(getModel(model, eaRepository).getPackageId());
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
                                            () -> new InternalException("Package or element " + part + " not found"));
                        }
                    } else {
                        assert rootElement != null;
                        var element = getChildElementByAlias(rootElement, part);
                        rootElement.destroy();
                        rootElement = element
                                .orElseThrow(() -> new InternalException("Element " + part + " not found"));
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

    private static List<EaPackageRef> getPackages(Supplier<Collection<Package>> packageSrc, EaRepository eaRepository) {
        var packages = packageSrc.get();
        try {
            var result = new ArrayList<EaPackageRef>(packages.GetCount());
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

    @Override
    public EaDefaultDiagram loadDefaultDiagram(EaDiagramRef diagramRef) {
        var diagram = repository.GetDiagramByID(diagramRef.getDiagramId());
        try {
            var diagramLoader = new EaLoaderDiagram(diagram, diagramRef.getRepository());
            return new EaDefaultDiagram(diagramRef, diagram.GetNotes(), diagramLoader.getDiagram(),
                    diagramLoader.getDiagramObjects());
        } finally {
            diagram.destroy();
        }
    }

    private static <E extends EaNamespaceElementRef> List<E> getPackageGroupElements(Package pkg,
                                                                                     EaItemGroupRef<?, ?, ?> packageGroupRef,
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

    private static <G extends EaItemGroupRef<?, ?, ?>> List<G> getPackageGroupPackages(Package pkg, G packageGroupRef,
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

    @Override
    @Nonnull
    public EaProductPackageGroup loadProductPackageGroup(EaProductPackageGroupRef packageGroupRef) {
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

    @Override
    @Nonnull
    public EaTechnicalPackageGroup loadTechnicalPackageGroup(EaTechnicalPackageGroupRef packageGroupRef) {
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

    @Override
    @Nonnull
    public EaPackage loadDefaultPackage(EaPackageRef packageRef) {
        var pkg = repository.GetPackageByID(packageRef.getPackageId());
        try {
            return new EaPackage(packageRef, pkg.GetNotes(),
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

    @Override
    @Nonnull
    public EaProductPackage loadProductPackage(EaProductPackageRef elementRef) {
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

    @Nonnull
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

    private static Stream<EaElementRef> getMeaningGroupContent(EaMeaningGroupRef meaningGroupRef) {
        return Stream.concat(
                Stream.of(meaningGroupRef),
                meaningGroupRef.getObject().getElements().stream().flatMap(EaLoaderImpl::mapMeaningContent));
    }

    private static Stream<EaElementRef> mapMeaningContent(EaElementRef meaning) {
        return (meaning instanceof EaMeaningGroupRef) ? (getMeaningGroupContent((EaMeaningGroupRef) meaning))
                : Stream.of(meaning);
    }

    /**
     * Return elements, associated with technical package via archimate association. In case associated object is
     * meaning group, all items inside this group are included
     *
     * @param element is EA element representing technical package
     * @param eaRepository is repository we are working in
     * @return resulting list of associated elements
     */
    private List<EaElementRef> getTechnicalPackageFunctions(Element element, EaRepository eaRepository) {
        return getRelElements(element, eaRepository, false, "Association",
                "ArchiMate_Association", EaUGTopicRef.class)
                .stream()
                .flatMap(EaLoaderImpl::mapMeaningContent)
                .collect(Collectors.toList());
    }

    private List<EaProductPackageRef> getTechnicalPackageContainedIn(Element element, EaRepository eaRepository) {
        return getRelElements(element, eaRepository, true, "Association",
                "ArchiMate_Aggregation", EaProductPackageRef.class);
    }

    private List<EaTechnicalPackageRef> getTechnicalPackagePrerequisities(Element element, EaRepository eaRepository) {
        return getRelElements(element, eaRepository, false, "Dependency", "",
                EaTechnicalPackageRef.class);
    }

    @Override
    @Nonnull
    public EaTechnicalPackage loadTechnicalPackage(EaTechnicalPackageRef elementRef) {
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

    private List<EaTechnicalPackageRef> getUGTopicIncludedIn(Element element, EaRepository eaRepository) {
        return getRelElements(element, eaRepository, true, "Association",
                "ArchiMate_Association", EaTechnicalPackageRef.class);
    }

    @Override
    @Nonnull
    public EaMeaningItem loadMeaningItem(EaMeaningItemRef elementRef) {
        var element = repository.GetElementByID(elementRef.getElementId());
        try {
            var diagrams = getDiagrams(element::GetDiagrams, elementRef.getRepository());
            if (hasElements(element::GetElements)) {
                LOG.warn("Elements under ArchiMate_Meaning element {} are ignored - Meaning should be leaf",
                        element::GetName);
            }
            return new EaMeaningItem(elementRef, element.GetNotes(), diagrams,
                    getUGTopicIncludedIn(element, elementRef.getRepository()));
        } finally {
            element.destroy();
        }
    }

    @Override
    @Nonnull
    public EaMeaningGroup loadMeaningGroup(EaMeaningGroupRef elementRef) {
        var element = repository.GetElementByID(elementRef.getElementId());
        try {
            var diagrams = getDiagrams(element::GetDiagrams, elementRef.getRepository());
            var elements = getElements(element::GetElements, elementRef.getRepository());
            var meaningElements = elements.stream()
                    .filter(el -> el instanceof EaMeaningRef)
                    .map(el -> (EaMeaningRef) el)
                    .collect(Collectors.toList());
            return new EaMeaningGroup(elementRef, element.GetNotes(), diagrams, meaningElements,
                    getUGTopicIncludedIn(element, elementRef.getRepository()));
        } finally {
            element.destroy();
        }
    }

    @Override
    @Nonnull
    public EaFunctionTask loadFunctionTask(EaFunctionTaskRef elementRef) {
        var element = repository.GetElementByID(elementRef.getElementId());
        try {
            var diagrams = getDiagrams(element::GetDiagrams, elementRef.getRepository());
            if (hasElements(element::GetElements)) {
                throw new InternalException("BusinessService element " + element.GetName() +
                        "with children should map to FunctionAbstract, not FunctionTask");
            }
            return new EaFunctionTask(elementRef, element.GetNotes(), diagrams,
                    getUGTopicIncludedIn(element, elementRef.getRepository()));
        } finally {
            element.destroy();
        }
    }

    @Override
    @Nonnull
    public EaDataObject loadDataObject(EaElementRef elementRef) {
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

    @Override
    @Nonnull
    public EaLeafElement loadLeafElement(EaLeafElementRef elementRef) {
        var element = repository.GetElementByID(elementRef.getElementId());
        try {
            var diagrams = getDiagrams(element::GetDiagrams, elementRef.getRepository());
            return new EaLeafElement(elementRef, element.GetNotes(), diagrams);
        } finally {
            element.destroy();
        }
    }

    @Override
    @Nonnull
    public EaObject loadNamespaceElement(EaNamespaceElementRef elementRef) {
        var element = repository.GetElementByID(elementRef.getElementId());
        try {
            var diagrams = getDiagrams(element::GetDiagrams, elementRef.getRepository());
            var elements = getElements(element::GetElements, elementRef.getRepository());
            return new EaNamespaceElement(elementRef, element.GetNotes(), diagrams, elements);
        } finally {
            element.destroy();
        }
    }
}
