package com.provys.wikiloader.impl;

import com.provys.catalogue.api.CatalogueRepository;
import com.provys.common.exception.InternalException;
import com.provys.provyswiki.ProvysWikiClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Collection;
import org.sparx.Package;
import org.sparx.Repository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;

@ApplicationScoped
public class WikiLoader {

    private static final Logger LOG = LogManager.getLogger(WikiLoader.class);

    private static final String ROOT_NAMESPACE = "eamodel";

    private final ElementHandlerFactory elementHandlerFactory;

    @Inject
    public WikiLoader(ElementHandlerFactory elementHandlerFactory) {
        this.elementHandlerFactory = Objects.requireNonNull(elementHandlerFactory);
    }

    public void run(Repository eaRepository, ProvysWikiClient wikiClient) {
        Collection<Package> models = eaRepository.GetModels();
        Package model = models.GetByName("Product Model");
        LinkResolver linkResolver = new LinkResolver(eaRepository, model, ROOT_NAMESPACE);

        PackageHandler.ofPackage(model, elementHandlerFactory, linkResolver).ifPresentOrElse(
                pkg -> pkg.sync(wikiClient),
                () -> {throw new InternalException(LOG, "Root package not evaluated for synchronisation");});
        model.destroy();
        models.destroy();
/*        var diagram = eaRepository.GetDiagramByID(98);
        var diagramHandler = new DiagramHandler(diagram);
        diagramHandler.sync(wikiClient, linkResolver);*/
/*
        DocumentGenerator documentGenerator = eaRepository.CreateDocumentGenerator();
        documentGenerator.NewDocument("");
        if (!documentGenerator.DocumentDiagram(90, 0, "Diagram Export")) {
            throw new RuntimeException("Error inserting diagram: " + documentGenerator.GetLastError());
        }
        documentGenerator.SaveDocument("c:\\temp\\document.html", 1);
        documentGenerator.destroy();*/
    }
}
