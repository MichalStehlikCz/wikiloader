package com.provys.wikiloader.earepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

/**
 * Common interface for packages and elements as these two objects all together form tree of ea repository objects
 */
public interface EaObject extends Comparable<EaObject> {

    /**
     * @return parent object
     */
    @Nonnull
    Optional<EaObject> getParent();

    /**
     * @return name of given object; note that it is Enterprise Architect name, it does not use handler for lookup
     */
    @Nonnull
    String getName();

    /**
     * @return alias of given object
     */
    @Nonnull
    Optional<String> getAlias();

    /**
     * @return stereotype of given object; for packages, StereotypeEx is used instead
     */
    @Nonnull
    Optional<String> getStereotype();

    /**
     * Retrieve position of given items, defined as list of tree positions, starting from root package (model)
     *
     * @return list with tree positions, starting from root package to given object
     */
    @Nonnull
    List<Integer> getPos();
}
