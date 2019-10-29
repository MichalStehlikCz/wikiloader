package com.provys.wikiloader.earepository;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * Enables to organize subset of topics into content tree, with given level included in case there is more than given
 * threshold items
 */
public interface WikiSetBuilder {

    /**
     * Sets threshold for keeping heading; default is 5
     */
    @Nonnull
    WikiSetBuilder setThreshold(int threshold);

    /**
     * Method adds package to set
     */
    @Nonnull
    WikiSetBuilder addPackageById(int packageId);

    /**
     * Method adds element to set
     */
    @Nonnull
    WikiSetBuilder addElementById(int elementId);

    /**
     * Method adds list of ea objects to set
     */
    @Nonnull
    WikiSetBuilder addEaObjects(Collection<? extends EaObjectRef> objects);

    /**
     * @return set of items for export, produced by this builder
     */
    @Nonnull
    List<WikiSetObject> build();
}
