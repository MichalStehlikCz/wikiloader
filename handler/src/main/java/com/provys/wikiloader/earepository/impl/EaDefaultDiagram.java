package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;
import com.provys.wikiloader.earepository.EaRepository;
import org.sparx.DiagramObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * Class represents diagram; it holds rendered image and coordinates of objects on that diagram
 */
class EaDefaultDiagram extends EaObjectRegularBase<EaDiagramRef> {

    private final byte[] diagram;
    private final Set<DiagramObjectRef> diagramObjects;

    EaDefaultDiagram(EaDiagramRef objectRef, @Nullable String notes, byte[] diagram,
                     Collection<DiagramObjectRef> diagramObjects) {
        super(objectRef, notes);
        this.diagram = Objects.requireNonNull(diagram);
        this.diagramObjects = Set.copyOf(diagramObjects);
    }

    byte[] getDiagram() {
        return diagram;
    }

    Set<DiagramObjectRef> getDiagramObjects() {
        return diagramObjects;
    }

    @Nonnull
    @Override
    Exporter getExporter(ProvysWikiClient wikiClient) {
        return new EaDefaultDiagramExporter(this, wikiClient);
    }

    static class ImgPos implements Comparable<ImgPos> {

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

        int getLeft() {
            return left;
        }

        int getRight() {
            return right;
        }

        int getTop() {
            return top;
        }

        int getBottom() {
            return bottom;
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
            var result = Integer.compare(right - left + bottom - top,
                    o.right - o.left + o.bottom - o.top);
            if ((result == 0) && !equals(o)) {
                result = Integer.compare(right - left, o.right - o.left);
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
        public boolean equals(@Nullable Object o) {
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

    static class DiagramObjectRef implements Comparable<DiagramObjectRef> {
        @Nonnull
        private final ImgPos pos;
        @Nonnull
        private final EaElementRef elementRef;

        DiagramObjectRef(DiagramObject diagramObject, EaRepository repository) {
            this.pos = ImgPos.ofEaPos(diagramObject.GetLeft(), diagramObject.GetRight(), diagramObject.GetTop(),
                    diagramObject.GetBottom());
            this.elementRef = repository.getElementRefById(diagramObject.GetElementID());
        }

        private DiagramObjectRef(ImgPos pos, EaElementRef eaElementRef) {
            this.pos = pos;
            this.elementRef = eaElementRef;
        }

        int getImgLeft() {
            return pos.left;
        }

        int getImgRight() {
            return pos.right;
        }

        int getImgTop() {
            return pos.top;
        }

        int getImgBottom() {
            return pos.bottom;
        }

        @Nonnull
        EaElementRef getElementRef() {
            return elementRef;
        }

        @Nonnull
        DiagramObjectRef shiftBy(int left, int top) {
            return new DiagramObjectRef(
                    ImgPos.ofImgPos(getImgLeft() - left, getImgRight() - left, getImgTop() - top, getImgBottom() - top),
                    elementRef);
        }

        @Override
        public int compareTo(DiagramObjectRef o) {
            var result = pos.compareTo(o.pos);
            if ((result == 0) && !equals(o)) {
                result = Integer.compare(elementRef.getElementId(), o.getElementRef().getElementId());
            }
            return result;
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DiagramObjectRef that = (DiagramObjectRef) o;
            return elementRef.equals(that.elementRef) &&
                    pos.equals(that.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, elementRef);
        }

        @Override
        public String toString() {
            return "DiagramObjectRef{" +
                    "pos=" + pos +
                    ", elementRef=" + elementRef +
                    '}';
        }
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaDefaultDiagram that = (EaDefaultDiagram) o;
        return Arrays.equals(diagram, that.diagram) &&
                Objects.equals(diagramObjects, that.diagramObjects);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), diagramObjects);
        result = 31 * result + Arrays.hashCode(diagram);
        return result;
    }
}