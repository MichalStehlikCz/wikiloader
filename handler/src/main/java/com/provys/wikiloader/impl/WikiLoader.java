package com.provys.wikiloader.impl;

import com.provys.provyswiki.ProvysWikiClient;
import org.sparx.Repository;

import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;

/**
 * Stateless bean, capable of running synchronisation (creates RunHandler for actual execution)
 */
@ApplicationScoped
public class WikiLoader {

    private final ElementHandlerFactory elementHandlerFactory;

    @Inject
    public WikiLoader(ElementHandlerFactory elementHandlerFactory) {
        this.elementHandlerFactory = Objects.requireNonNull(elementHandlerFactory);
    }

    public void run(Repository eaRepository, ProvysWikiClient wikiClient, @Nullable String path, boolean recursive) {
        new RunHandler(eaRepository, wikiClient, elementHandlerFactory, path, recursive).run();
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
