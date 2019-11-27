package com.provys.wikiloader.earepository;

/**
 * Represents diagram in Enterprise Architect repository
 */
public interface EaDiagramRef extends EaObjectRef {

    /**
     * @return Enterprise Architect id of this diagram
     */
    int getDiagramId();
}
