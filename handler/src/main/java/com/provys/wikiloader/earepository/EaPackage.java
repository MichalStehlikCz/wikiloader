package com.provys.wikiloader.earepository;

import org.sparx.Package;

import javax.annotation.Nonnull;

/**
 * Interface represents package in enterprise architect repository
 */
public interface EaPackage extends EaObject {

    /**
     * @return Enterprise Architect Id of package
     */
    int getPackageId();
}
