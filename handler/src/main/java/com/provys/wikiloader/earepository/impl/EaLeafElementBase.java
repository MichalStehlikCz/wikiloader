package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaDiagramRef;
import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Common ancestor for leaf elements - e.g. ones that do not include sub-elements, but might include diagram(s).
 */
abstract class EaLeafElementBase<T extends EaElementRef> extends EaDiagramOwnerBase<T, EaDiagramRef>
        implements EaElementRef {

    EaLeafElementBase(T objectRef, @Nullable String notes, List<EaDiagramRef> diagrams) {
        super(objectRef, notes, diagrams);
    }

    @Override
    public int getElementId() {
        return getObjectRef().getElementId();
    }

}
