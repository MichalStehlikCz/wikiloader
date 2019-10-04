package com.provys.wikiloader.elementhandlers;

import com.provys.common.exception.InternalException;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.impl.ElementHandler;
import com.provys.wikiloader.wikimap.WikiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Diagram;
import org.sparx.DiagramObject;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class implements export of diagram to Wiki
 */
public class DiagramHandler implements ElementHandler {

    private static final Logger LOG = LogManager.getLogger(DiagramHandler.class);

    /**
     * Used to initialize diagram objects collection
     *
     * @param diagram is diagram we want to get objects of
     * @return collection of diagram objects related to supplied diagram
     */
    @Nonnull
    private static Set<DiagramObjectRef> getDiagramObjects(Diagram diagram) {
        var diagramObjects = diagram.GetDiagramObjects();
        var result = new TreeSet<DiagramObjectRef>();
        for (var diagramObject : diagramObjects) {
            result.add(new DiagramObjectRef(diagramObject));
            diagramObject.destroy();
        }
        diagramObjects.destroy();
        return result;
    }

    @Nonnull
    private static ImgPos getImgPos(Collection<DiagramObjectRef> diagramObjects) {
        if (diagramObjects.isEmpty()) {
            return ImgPos.ofImgPos(0, 30, 0, 30);
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
        return ImgPos.ofImgPos(left, right, top, bottom);
    }

    @Nonnull
    private final Diagram diagram;
    @Nonnull
    private final WikiMap wikiMap;
    @Nonnull
    private final String name;
    @Nonnull
    private final Set<DiagramObjectRef> diagramObjects;
    @Nonnull
    private final ImgPos imgPos;

    public DiagramHandler(Diagram diagram, WikiMap wikiMap) {
        this.diagram = Objects.requireNonNull(diagram);
        this.wikiMap = Objects.requireNonNull(wikiMap);
        this.name = WikiMap.getDiagramName(diagram);
        this.diagramObjects = getDiagramObjects(diagram);
        this.imgPos = getImgPos(diagramObjects);
    }

    @Nonnull
    public String getRelLink() {
        return name;
    }

    @Nonnull
    private String getFilename() {
        return name + ".png";
    }

    @Nonnull
    private String getDocument(WikiMap wikiMap) {
        var builder = new StringBuilder().append("===== ").append(diagram.GetName()).append(" =====\n")
                .append("{{map>").append(getFilename()).append("|Diagram ").append(diagram.GetName()).append("}}\n");
        for (var diagramObject : diagramObjects) {
            wikiMap.getElementLink(diagramObject.getElementId())
                    .ifPresent(pageId -> builder.append("  * [[").append(pageId).append('|').append(pageId).append('@')
                            .append(diagramObject.pos.left - imgPos.left).append(',')
                            .append(diagramObject.pos.top - imgPos.top).append(',')
                            .append(diagramObject.pos.right - imgPos.left).append(',')
                            .append(diagramObject.pos.bottom - imgPos.top).append("]]\n"));
        }
        builder.append("{{<map}}")
                .append(diagram.GetNotes());
        return builder.toString();
    }

    private static class ImgPos implements Comparable<ImgPos> {

        static ImgPos ofEaPos(int left, int right, int top, int bottom) {
            return new ImgPos((int) Math.round(1.32 * left), (int) Math.round(1.32 * right),
                    (int) -Math.round(1.32 * top), (int) -Math.round(1.32 * bottom));
        }

        static ImgPos ofImgPos(int left, int right, int top, int bottom) {
            return new ImgPos(left, right, top, bottom);
        }

        private final int left;
        private final int right;
        private final int top;
        private final int bottom;

        private ImgPos(int left, int right, int top, int bottom) {
            this.left = left;
            this.right = right;
            this.top = top;
            this.bottom = bottom;
        }

        int getWidth() {
            return right - left;
        }

        int getHeight() {
            return bottom - top;
        }

        /**
         * Comparison is used to order elements in map. We want smaller items first (on top of bigger ones),
         * within the same size we go left to right and top to bottom
         *
         * @param o is other object to be compared to
         * @return  a negative integer, zero, or a positive integer as this object
         *          is less than, equal to, or greater than the specified object.
         */
        @Override
        public int compareTo(ImgPos o) {
            var result = Integer.compare(left - right + top - bottom,
                    o.left - o.right + o.top - o.bottom);
            if ((result == 0) && !equals(o)) {
                result = Integer.compare(left - right, o.left - o.right);
                if (result == 0) {
                    result = Integer.compare(left, o.left);
                    if (result == 0) {
                        result = Integer.compare(top, o.top);
                    }
                }
            }
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImgPos imgPos = (ImgPos) o;
            return left == imgPos.left &&
                    right == imgPos.right &&
                    top == imgPos.top &&
                    bottom == imgPos.bottom;
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right, top, bottom);
        }

        @Override
        public String toString() {
            return "ImgPos{" +
                    "left=" + left +
                    ", right=" + right +
                    ", top=" + top +
                    ", bottom=" + bottom +
                    '}';
        }
    }

    private static class DiagramObjectRef implements Comparable<DiagramObjectRef> {
        private final ImgPos pos;
        private final int elementId;

        DiagramObjectRef(DiagramObject diagramObject) {
            this.pos = ImgPos.ofEaPos(diagramObject.GetLeft(), diagramObject.GetRight(), diagramObject.GetTop(),
                    diagramObject.GetBottom());
            this.elementId = diagramObject.GetElementID();
        }

        private int getImgLeft() {
            return pos.left;
        }

        private int getImgRight() {
            return pos.right;
        }

        private int getImgTop() {
            return pos.top;
        }

        private int getImgBottom() {
            return pos.bottom;
        }

        private int getElementId() {
            return elementId;
        }

        @Override
        public int compareTo(DiagramObjectRef o) {
            var result = pos.compareTo(o.pos);
            if ((result == 0) && !equals(o)) {
                result = Integer.compare(elementId, o.elementId);
            }
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DiagramObjectRef that = (DiagramObjectRef) o;
            return elementId == that.elementId &&
                    Objects.equals(pos, that.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, elementId);
        }

        @Override
        public String toString() {
            return "DiagramObjectRef{" +
                    "pos=" + pos +
                    ", elementId=" + elementId +
                    '}';
        }
    }

    private BufferedImage getImagePage(int xpage, int ypage, String filename) {
        if (!diagram.SaveImagePage(xpage, ypage, 0, 0, filename, 0)) {
            throw new InternalException(LOG, "Failed to export diagram " + diagram.GetName() + " page "
                    + xpage + ", " + ypage + " to " + filename + ": " + diagram.GetLastError());
        }
        BufferedImage page;
        try {
            page = ImageIO.read(new File(filename));
        } catch (IOException e) {
            throw new InternalException(LOG, "Failed to read exported file " + diagram.GetName() + " page "
                    + xpage + ", " + ypage + " from " + filename);
        }
        return page;
    }

    @Nonnull
    private byte[] getDiagram() {
        BufferedImage image = new BufferedImage(imgPos.getWidth(), imgPos.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // now iterate through pages and merge exported pages...
        String filename = "c:\\temp\\" + getFilename();
        try {
            int origXPos = 0;
            for (int xPage = 1; origXPos <= imgPos.right; xPage++) {
                int origYPos = 0;
                int origWidth = 0;
                for (int yPage = 1; origYPos <= imgPos.bottom; yPage++) {
                    var page = getImagePage(xPage, yPage, filename);
                    origWidth = page.getWidth();
                    int origHeight = page.getHeight();
                    if ((origXPos < imgPos.left + 4) || (origYPos < imgPos.top + 4)) {
                        // we do not need whole generated page - need to crop top or left
                        int subLeft = Integer.max(imgPos.left - origXPos, 4);
                        int subTop = Integer.max(imgPos.top - origYPos, 4);
                        int subWidth = Integer.min(page.getWidth() - subLeft, imgPos.right - origXPos + 1);
                        int subHeight = Integer.min(page.getHeight() - subTop, imgPos.bottom - origYPos + 1);
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
                        g.drawImage(page, Integer.max(origXPos - imgPos.left, 0),
                                Integer.max(origYPos - imgPos.top, 0), null);
                    }
                    origYPos += origHeight;
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
            throw new InternalException(LOG, "Error exporting image for diagram " + diagram.GetName(), e);
        }
    }

    public void sync(ProvysWikiClient wikiClient) {
        String namespace;
        if (diagram.GetParentID() > 0) {
            namespace = wikiMap.getElementNamespace(diagram.GetParentID())
                    .orElseThrow(() -> new InternalException(LOG,
                            "Cannot synchronise diagram - parent element namespace not resolved"));
        } else {
            namespace = wikiMap.getPackageNamespace(diagram.GetPackageID())
                    .orElseThrow(() -> new InternalException(LOG,
                            "Cannot synchronise diagram - package namespace not resolved"));
        }
        LOG.info("Synchronize diagram {}:{}", namespace, name);
        wikiClient.putPage(namespace + ":" + name, getDocument(wikiMap));
        wikiClient.putAttachment(namespace + ":" + getFilename(), getDiagram(), true, true);
    }
}
