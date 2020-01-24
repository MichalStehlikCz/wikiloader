package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Diagram;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class used by loader to render diagram from Enterprise Architect. One of few classes that actually directly access
 * Enterprise Architect repository COM interface
 */
class EaLoaderDiagram {

    private static final Logger LOG = LogManager.getLogger(EaLoaderDiagram.class);

    /**
     * Used to initialize diagram objects collection
     *
     * @param diagram is diagram we want to get objects of
     * @return collection of diagram objects related to supplied diagram
     */
    @Nonnull
    private static Set<EaDefaultDiagram.DiagramObjectRef> getDiagramObjects(Diagram diagram, EaRepository eaRepository) {
        var diagramObjects = diagram.GetDiagramObjects();
        try {
            var result = new TreeSet<EaDefaultDiagram.DiagramObjectRef>();
            for (var diagramObject : diagramObjects) {
                try {
                    result.add(new EaDefaultDiagram.DiagramObjectRef(diagramObject, eaRepository));
                } finally {
                    diagramObject.destroy();
                }
            }
            return result;
        } finally {
            diagramObjects.destroy();
        }
    }

    @Nonnull
    private static EaDefaultDiagram.ImgPos getImgPos(Collection<EaDefaultDiagram.DiagramObjectRef> diagramObjects) {
        if (diagramObjects.isEmpty()) {
            return EaDefaultDiagram.ImgPos.ofImgPos(0, 30, 0, 30);
        }
        int left = Integer.MAX_VALUE;
        int right = Integer.MIN_VALUE;
        int top = Integer.MAX_VALUE;
        int bottom = Integer.MIN_VALUE;
        for (var diagramObject : diagramObjects) {
            if (left > diagramObject.getImgLeft()) {
                left = diagramObject.getImgLeft();
            }
            if (right < diagramObject.getImgRight()) {
                right = diagramObject.getImgRight();
            }
            if (top > diagramObject.getImgTop()) {
                top = diagramObject.getImgTop();
            }
            if (bottom < diagramObject.getImgBottom()) {
                bottom = diagramObject.getImgBottom();
            }
        }
        left = (left >= 15) ? left - 15 : 0;
        right += 15;
        top = (top >= 15) ? top - 15 : 0;
        bottom += 15;
        return EaDefaultDiagram.ImgPos.ofImgPos(left, right, top, bottom);
    }

    @Nonnull
    private final Diagram diagram;
    @Nonnull
    private final Set<EaDefaultDiagram.DiagramObjectRef> diagramObjects;
    @Nonnull
    private final EaDefaultDiagram.ImgPos imgPos;

    EaLoaderDiagram(Diagram diagram, EaRepository eaRepository) {
        this.diagram = Objects.requireNonNull(diagram);
        this.diagramObjects = getDiagramObjects(diagram, eaRepository);
        this.imgPos = getImgPos(diagramObjects);
    }

    @Nonnull
    Set<EaDefaultDiagram.DiagramObjectRef> getDiagramObjects() {
        return diagramObjects.stream()
                .map(diagramObject -> diagramObject.shiftBy(imgPos.getLeft(), imgPos.getTop()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Nonnull
    private String getFilename() {
        return "export_diagram.png";
    }

    @Nullable
    private BufferedImage getImagePage(int xPage, int yPage, String filename) {
        LOG.debug("Generate diagram {} page {} / {}", diagram::GetName, () -> xPage, () -> yPage);
        if (!diagram.SaveImagePage(xPage, yPage, 0, 0, filename, 0)) {
            if (diagram.GetLastError().equals("x or y is out of range.")) {
                // probably diagram is exported slightly smaller than expected...
                LOG.info("Page out of range reported when generating {} page {} / {}", diagram::GetName,
                        () -> xPage, () -> yPage);
                return null;
            }
            throw new InternalException("Failed to export diagram " + diagram.GetName() + " page "
                    + xPage + ", " + yPage + " to " + filename + ": " + diagram.GetLastError());
        }
        BufferedImage page;
        try {
            page = ImageIO.read(new File(filename));
        } catch (IOException e) {
            throw new InternalException("Failed to read exported file " + diagram.GetName() + " page "
                    + xPage + ", " + yPage + " from " + filename);
        }
        return page;
    }

    /**
     * Render column to graphics and return width that has been used from EA rendered drawing
     *
     * @param g is graphics element picture is rendered to
     * @return width of column in EA rendered diagram
     */
    private int renderColumn(Graphics g, int xPage, int origXPos, String filename) {
        int origYPos = 0;
        int origWidth = 0;
        for (int yPage = 1; origYPos <= imgPos.getBottom(); yPage++) {
            var page = getImagePage(xPage, yPage, filename);
            if (page == null) {
                break;
            }
            int origHeight;
            origWidth = page.getWidth();
            origHeight = page.getHeight();
            if ((origXPos < imgPos.getLeft() + 4) || (origYPos < imgPos.getTop() + 4)) {
                // we do not need whole generated page - need to crop top or left
                int subLeft = Integer.max(imgPos.getLeft() - origXPos, 4);
                int subTop = Integer.max(imgPos.getTop() - origYPos, 4);
                int subWidth = Integer.min(page.getWidth() - subLeft, imgPos.getRight() - origXPos + 1);
                int subHeight = Integer.min(page.getHeight() - subTop, imgPos.getBottom() - origYPos + 1);
                if ((subWidth <= 0) || (subHeight <= 0)) {
                    // nothing to use from this page
                    page = null;
                } else {
                    page = page.getSubimage(subLeft, subTop, subWidth, subHeight);
                }
            } else {
                origWidth -= 4;
                origHeight -= 4;
                page = page.getSubimage(4, 4, origWidth, origHeight);
            }
            if (page != null) {
                // move position to already painted; on the first picture, start from 0, 0
                g.drawImage(page, Integer.max(origXPos - imgPos.getLeft(), 0),
                        Integer.max(origYPos - imgPos.getTop(), 0), null);
            }
            origYPos += origHeight;
        }
        return origWidth;
    }

    @Nonnull
    byte[] getDiagram() {
        BufferedImage image = new BufferedImage(imgPos.getWidth(), imgPos.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // now iterate through pages and merge exported pages...
        String filename = "c:\\temp\\" + getFilename();
        try {
            int origXPos = 0;
            for (int xPage = 1; origXPos <= imgPos.getRight(); xPage++) {
                int origWidth = renderColumn(g, xPage, origXPos, filename);
                if (origWidth == 0) {
                    // we didn't render anything, probably because x position is out of range...
                    break;
                }
                origXPos += origWidth;
            }
        } finally {
            // if we created temporary file, we should delete it...
            try {
                Files.deleteIfExists(Paths.get(filename));
            } catch (IOException e) {
                LOG.warn("Failed to delete exported diagram {}", filename);
            }
        }
        try (var outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new InternalException("Error exporting image for diagram " + diagram.GetName(), e);
        }
    }
}
