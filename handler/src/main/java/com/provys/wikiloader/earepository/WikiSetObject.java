package com.provys.wikiloader.earepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents item in object set
 */
public interface WikiSetObject {

    /**
     * Get topic name. Note that method takes name from Enterprise Architect, it does not invoke handler for name
     * resolution. it is recommended to be used only in case topic is not exported to wiki
     *
     * @return topic name
     */
    @Nonnull
    String getTitle();

    /**
     * @return ordered list of children of this set object
     */
    @Nonnull
    List<WikiSetObject> getChildren();

    /**
     * Append content, produced by this set to StringBuilder; variant for top level (level = 0)
     *
     * @param builder is StringBuilder used to capture content
     */
    void appendContent(StringBuilder builder);

    /**
     * Append content, produced by this set to StringBuilder; variant with function used to retrieve link from EA topic
     *
     * @param builder is StringBuilder content will be appended to
     * @param linkFunction is function used to extract topic id from EA topic reference
     */
    void appendContent(StringBuilder builder, Function<EaObjectRef, Optional<String>> linkFunction);
}
