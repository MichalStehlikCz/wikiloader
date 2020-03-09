package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaRepository;
import com.provys.wikiloader.earepository.impl.EaDefaultDiagram.DiagramObjectRef;
import com.provys.wikiloader.earepository.impl.EaDefaultDiagram.ImgPos;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Diagram;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

/**
 * Class used by loader to render diagram from Enterprise Architect. One of few classes that actually directly access
 * Enterprise Architect repository COM interface
 */
final class EaLoaderDiagram {

    /**
     * Used to initialize diagram objects collection; coordinates of diagram objects are original EA coordinates.
     *
     * @param diagram is diagram we want to get objects of
     * @return collection of diagram objects related to supplied diagram
     */
    @Nonnull
    private static Collection<DiagramObjectRef> getEaDiagramObjects(Diagram diagram, EaRepository eaRepository) {
        var diagramObjects = diagram.GetDiagramObjects();
        try {
            var result = new ArrayList<DiagramObjectRef>(diagramObjects.GetCount());
            for (var diagramObject : diagramObjects) {
                try {
                    result.add(new EaDefaultDiagram.DiagramObjectRef(ImgPos
                        .ofImgPos(diagramObject.GetLeft(), diagramObject.GetRight(), diagramObject.GetTop(),
                            diagramObject.GetBottom()), eaRepository.getElementRefById(diagramObject.GetElementID())));
                } finally {
                    diagramObject.destroy();
                }
            }
            return result;
        } finally {
            diagramObjects.destroy();
        }
    }

    /**
     * Evaluate diagram's image position - e.g. area defined by top / bottom / left / right most
     * element of diagram.
     *
     * @param diagramObjects is collection of diagram objects
     * @return area encompassing all diagram objects
     */
    @Nonnull
    private static EaDefaultDiagram.ImgPos getImgPos(Collection<EaDefaultDiagram.DiagramObjectRef> diagramObjects) {
        if (diagramObjects.isEmpty()) {
            return EaDefaultDiagram.ImgPos.ofImgPos(0, 30, 0, 30);
        }
        int left = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;
        int top = Integer.MIN_VALUE;
        int bottom = Integer.MAX_VALUE;
        for (var diagramObject : diagramObjects) {
            if (left > diagramObject.getImgLeft()) {
                left = diagramObject.getImgLeft();
            }
            if (right < diagramObject.getImgRight()) {
                right = diagramObject.getImgRight();
            }
            if (top < diagramObject.getImgTop()) {
                top = diagramObject.getImgTop();
            }
            if (bottom > diagramObject.getImgBottom()) {
                bottom = diagramObject.getImgBottom();
            }
        }
        return EaDefaultDiagram.ImgPos.ofImgPos(left, right, top, bottom);
    }

    /**
     * Used to initialize diagram objects collection. Returned objects have coordinates, adjusted to
     * exported picture.
     *
     * @param diagram is diagram we want to get objects of
     * @return collection of diagram objects related to supplied diagram
     */
    @Nonnull
    private static List<DiagramObjectRef> getDiagramObjects(Diagram diagram,
        EaRepository eaRepository) {
        var origDiagramObjects = getEaDiagramObjects(diagram, eaRepository);
        var imgPos = getImgPos(origDiagramObjects);
        var ratio = 1.363;
        var left = 48;
        var top = 59;
        return origDiagramObjects.stream()
            .map(diagramObject -> new EaDefaultDiagram.DiagramObjectRef(diagramObject.getPos()
                .transform(imgPos.getLeft(), imgPos.getTop(), ratio, left, top),
                diagramObject.getElementRef()))
            .sorted()
            .collect(Collectors.toList());
    }

    @Nonnull
    private final List<EaDefaultDiagram.DiagramObjectRef> diagramObjects;
    @Nonnull
    private final String diagramGUID;
    @Nonnull
    private final String name;

    EaLoaderDiagram(Diagram diagram, EaRepository eaRepository) {
        this.diagramObjects = getDiagramObjects(diagram, eaRepository);
        this.name = diagram.GetName();
        this.diagramGUID = diagram.GetDiagramGUID();
    }

    @Nonnull
    List<EaDefaultDiagram.DiagramObjectRef> getDiagramObjects() {
        return diagramObjects;
    }

    @Override
    public String toString() {
        return "EaLoaderDiagram{"
            + "diagramObjects=" + diagramObjects
            + ", diagramGUID='" + diagramGUID + '\''
            + ", name='" + name + '\''
            + '}';
    }
}
