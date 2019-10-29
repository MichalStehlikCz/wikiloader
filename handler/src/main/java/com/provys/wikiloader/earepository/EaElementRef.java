package com.provys.wikiloader.earepository;

/**
 * Represents element in Enterprise Architect repository
 */
public interface EaElementRef extends EaObjectRef {

    /**
     * @return Enterprise Architect id of this element
     */
    int getElementId();
}
