package com.provys.wikiloader.elementhandlers;

import com.provys.common.exception.InternalException;
import com.provys.dokuwiki.PageIdParser;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.impl.ElementHandler;
import com.provys.wikiloader.impl.LinkResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Element;

import javax.annotation.Nonnull;
import java.util.Objects;

abstract class ElementHandlerBase implements ElementHandler {

    private static final Logger LOG = LogManager.getLogger(ElementHandlerBase.class);

    @Nonnull
    private final Element element;
    @Nonnull
    private final String id;
    @Nonnull
    private final String name;

    ElementHandlerBase(Element element, String id) {
        this.element = Objects.requireNonNull(element);
        if (element.GetAlias().isEmpty()) {
            throw new InternalException(LOG, "Cannot process element with empty alias");
        }
        this.id = id;
        this.name = new PageIdParser().getName(id);
    }

    /**
     * @return element this handler is used for
     */
    @Nonnull
    Element getElement() {
        return element;
    }

    /**
     * @return name (lower-case of alias) used as topic name
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Append title (using name of object)
     *
     * @param builder is {@code StringBuilder} title should be appended to
     */
    void appendTitle(StringBuilder builder) {
        builder.append("===== ").append(getElement().GetName()).append(" =====\n");
    }

    /**
     * Append alias (line with Alias: <<alias>>).
     *
     * @param builder is {@code StringBuilder} alias should be appended to
     */
    void appendAlias(StringBuilder builder) {
        if (!getElement().GetAlias().isEmpty()) {
            builder.append("Alias: ").append(getElement().GetAlias()).append("\\\\\n");
        }
    }

    /**
     * Append notes.
     *
     * @param builder is {@code StringBuilder} notes should be appended to
     */
    void appendNotes(StringBuilder builder) {
        if (!getElement().GetNotes().isEmpty()) {
            builder.append(getElement().GetNotes());
        }
    }

    /**
     * Get document (wiki page)
     */
    @Nonnull
    abstract String getDocument(LinkResolver linkResolver);

    /**
     * Synchronize wiki page corresponding to given element
     */
    @Override
    public void sync(ProvysWikiClient wikiClient, LinkResolver linkResolver) {
        LOG.info("Synchronize document {}", id);
        wikiClient.putPage(id, getDocument(linkResolver));
    }
}
