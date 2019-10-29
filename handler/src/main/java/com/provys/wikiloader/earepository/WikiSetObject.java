package com.provys.wikiloader.earepository;

import javax.annotation.Nonnull;
import java.util.List;

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
    String getName();

    /**
     * Append text that should be inserted to wiki (only link / name, without ident and bullet)
     *
     * @param builder is builder text should be appended to
     */
    void appendWikiText(StringBuilder builder);

    List<WikiSetObject> getChildren();

    /**
     * Append content, produced by this set to StringBuilder
     *
     * @param builder is StringBuilder used to capture content
     * @param level is ident level (0 means two spaces before *)
     */
    void appendContent(StringBuilder builder, int level);

    /**
     * Append content, produced by this set to StringBuilder; variant for top level (level = 0)
     *
     * @param builder is StringBuilder used to capture content
     */
    void appendContent(StringBuilder builder);
}
