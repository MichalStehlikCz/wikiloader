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
 * Class represents diagram; it holds rendered image and coordinates of objects on that diagram.
 */
class EaDefaultDiagram extends EaObjectRegularBase<EaDiagramRef> {

  private static final double PIXEL_RATIO = 1.363;

  private final byte[] diagram;
  private final List<DiagramObjectRef> diagramObjects;

  EaDefaultDiagram(EaDiagramRef objectRef, @Nullable String notes, byte[] diagram,
      Collection<DiagramObjectRef> diagramObjects) {
    super(objectRef, notes);
    this.diagram = Objects.requireNonNull(diagram);
    this.diagramObjects = List.copyOf(diagramObjects);
  }

  byte[] getDiagram() {
    return diagram;
  }

  List<DiagramObjectRef> getDiagramObjects() {
    return diagramObjects;
  }

  @Nonnull
  @Override
  Exporter getExporter(ProvysWikiClient wikiClient) {
    return new EaDefaultDiagramExporter(this, wikiClient);
  }

  static final class ImgPos implements Comparable<ImgPos> {

    static ImgPos ofEaPos(int left, int right, int top, int bottom) {
      return new ImgPos((int) Math.round(PIXEL_RATIO * left), (int) Math.round(PIXEL_RATIO * right),
          (int) -Math.round(PIXEL_RATIO * top), (int) -Math.round(PIXEL_RATIO * bottom));
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
     * Transforms image position - used for recalculation from EA coordinates to picture coordinates. Calculation is as follows:
     * <p>invert y coordinate</p>
     * <p>deduct left / top offset</p>
     * <p>multiply by ratio and round</p>
     * <p>shift by left / top shift</p>
     *
     * @param leftOffset is offset deducted from left and right coordinates
     * @param topOffset is offset deducted from top and bottom coordinates
     * @param ratio is coefficient between EA and picture coordinate
     * @param leftShift is added to left and right coordinates after ratio adjustment
     * @param topShift is added to top and bottom coordinates after ratio adjustment
     * @return adjusted coordinates
     */
    ImgPos transform(int leftOffset, int topOffset, double ratio, int leftShift, int topShift) {
      return ofImgPos((int) Math.round((left - leftOffset) * ratio) + leftShift,
          (int) Math.round((right - leftOffset) * ratio) + leftShift,
          (int) Math.round(-(top - topOffset) * ratio) + topShift,
          (int) Math.round(-(bottom - topOffset) * ratio) + topShift);
    }

    /**
     * Comparison is used to order elements in map. We want smaller items first (on top of bigger
     * ones), within the same size we go left to right and top to bottom
     *
     * @param o is other object to be compared to
     * @return a negative integer, zero, or a positive integer as this object is less than, equal
     * to, or greater than the specified object.
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
      //noinspection ReturnSeparatedFromComputation
      return result;
    }

    @Override
    public boolean equals(@Nullable Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof ImgPos)) {
        return false;
      }
      ImgPos imgPos = (ImgPos) o;
      return left == imgPos.left
          && right == imgPos.right
          && top == imgPos.top
          && bottom == imgPos.bottom;
    }

    @Override
    public int hashCode() {
      int result = left;
      result = 31 * result + right;
      result = 31 * result + top;
      result = 31 * result + bottom;
      return result;
    }

    @Override
    public String toString() {
      return "ImgPos{"
          + "left=" + left
          + ", right=" + right
          + ", top=" + top
          + ", bottom=" + bottom
          + '}';
    }
  }

  static final class DiagramObjectRef implements Comparable<DiagramObjectRef> {

    @Nonnull
    private final ImgPos pos;
    @Nonnull
    private final EaElementRef elementRef;

    DiagramObjectRef(ImgPos pos, EaElementRef elementRef) {
      this.pos = pos;
      this.elementRef = elementRef;
    }

    @Nonnull
    ImgPos getPos() {
      return pos;
    }

    int getImgLeft() {
      return pos.getLeft();
    }

    int getImgRight() {
      return pos.getRight();
    }

    int getImgTop() {
      return pos.getTop();
    }

    int getImgBottom() {
      return pos.getBottom();
    }

    @Nonnull
    EaElementRef getElementRef() {
      return elementRef;
    }

    @Nonnull
    DiagramObjectRef shiftBy(int left, int top) {
      return new DiagramObjectRef(
          ImgPos.ofImgPos(getImgLeft() - left, getImgRight() - left, getImgTop() - top,
              getImgBottom() - top), elementRef);
    }

    @Override
    public int compareTo(DiagramObjectRef o) {
      var result = pos.compareTo(o.pos);
      if ((result == 0) && !equals(o)) {
        result = Integer.compare(elementRef.getElementId(), o.getElementRef().getElementId());
      }
      //noinspection ReturnSeparatedFromComputation
      return result;
    }

    @Override
    public boolean equals(@Nullable Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      DiagramObjectRef that = (DiagramObjectRef) o;
      return pos.equals(that.pos)
          && elementRef.equals(that.elementRef);
    }

    @Override
    public int hashCode() {
      int result = pos.hashCode();
      result = 31 * result + elementRef.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "DiagramObjectRef{"
          + "pos=" + pos
          + ", elementRef=" + elementRef
          + '}';
    }
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    EaDefaultDiagram that = (EaDefaultDiagram) o;
    return Arrays.equals(diagram, that.diagram)
        && Objects.equals(diagramObjects, that.diagramObjects);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + Arrays.hashCode(diagram);
    result = 31 * result + (diagramObjects != null ? diagramObjects.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "EaDefaultDiagram{"
        + "diagramObjects=" + diagramObjects
        + ", " + super.toString() + '}';
  }
}
