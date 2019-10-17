package com.provys.wikiloader.earepository;

import org.sparx.Repository;

/**
 * Maps objects in Enterprise Architect repository
 */
public interface EaRepository {

    /**
     * @return Enterprise Architect repository object, covered by this wrapper
     */
    Repository getRepository();

    /**
     * @param elementId is id of element in enterprise architect repository
     * @return element with specified id
     */
    EaElement getElementById(int elementId);

    /**
     * @param packageId is id of element in enterprise architect repository
     * @return element with specified id
     */
    EaPackage getPackageById(int packageId);
}
