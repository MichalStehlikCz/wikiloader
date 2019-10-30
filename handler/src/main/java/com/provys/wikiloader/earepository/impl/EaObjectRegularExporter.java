package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaObject;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Class represents ancestor of different exporters. It is mutable class, used to build wiki page and export it
 *
 * @param <T> is handler this exporter exports to wiki
 */
class EaObjectRegularExporter<T extends EaObject> implements Exporter {

    private static final Logger LOG = LogManager.getLogger(EaObjectRegularExporter.class);

    @Nonnull
    private final T eaObject;
    @Nonnull
    private final ProvysWikiClient wikiClient;
    @Nonnull
    final StringBuilder startBuilder = new StringBuilder();
    @Nonnull
    final List<String> contentBuilder = new ArrayList<>(10);
    @Nonnull
    final List<String> pages = new ArrayList<>(10);
    @Nonnull
    final List<EaObjectRef> subObjects = new ArrayList<>(20);

    EaObjectRegularExporter(T eaObject, ProvysWikiClient wikiClient) {
        this.eaObject = Objects.requireNonNull(eaObject);
        this.wikiClient = Objects.requireNonNull(wikiClient);
    }

    @Nonnull
    T getEaObject() {
        return eaObject;
    }

    @Nonnull
    ProvysWikiClient getWikiClient() {
        return wikiClient;
    }

    /**
     * Append tags, appropriate for given element. Empty in this ancestor, but might be overriden in ancestors to
     * insert tags, needed by given document type
     */
    void appendTags() {
        // empty in this ancestor
    }

    /**
     * Append title to page being built
     */
    void appendTitle() {
        startBuilder.append("====== ").append(eaObject.getTitle()).append(" ======\n");
    }

    /**
     * Append alias (line with Alias: <<alias>>).
     */
    void appendAlias() {
        eaObject.getAlias().ifPresent(alias -> startBuilder.append("Alias: ").append(alias).append("\\\\\n"));
    }

    /**
     * Place item inline inside page. It is expected that object is link-able
     *
     * @param objectRef is handler for item to be added
     * @param showHeader defines if header should be used or removed
     */
    void inlineObject(EaObjectRef objectRef, boolean showHeader) {
        startBuilder.append("{{page>");
        objectRef.appendParentLink(startBuilder);
        startBuilder.append(showHeader ? "" : "&noheader")
                .append("}}\n");
        objectRef.appendPages(pages);
    }

    /**
     * Append link to item to page. It is expected that object is link-able
     *
     * @param objectRef is handler for item to be added
     */
    void linkObject(EaObjectRef objectRef) {
        startBuilder.append("  * [[");
        objectRef.appendParentLink(startBuilder);
        startBuilder.append("]]\n");
        contentBuilder.add(objectRef.getParentLink().orElseThrow());
        objectRef.appendPages(pages);
    }

    /**
     * Append notes.
     */
    void appendNotes() {
        getEaObject().getNotes().ifPresent(startBuilder::append);
    }

    /**
     * Append document - middle section of body
     */
    void appendDocument() {
        appendNotes();
    }

    void appendBody() {
        // insert own content
        appendDocument();
    }

    void syncNamespace(String namespace) {
        if (!contentBuilder.isEmpty()) {
            wikiClient.syncSidebar(namespace);
            wikiClient.syncContent(namespace, contentBuilder);
        } else {
            wikiClient.deleteSidebarIfExists(namespace);
            wikiClient.deleteContentIfExists(namespace);
        }
        wikiClient.deleteUnusedNamespaces(namespace, pages);
        wikiClient.deleteUnusedPages(namespace, pages);
    }

    void syncWiki() {
        wikiClient.putGeneratedPage(getEaObject().getTopicId().orElseThrow(), startBuilder.toString());
        getEaObject().getNamespace().ifPresent(this::syncNamespace);
    }

    @Override
    public void run(boolean recursive) {
        LOG.info("Synchronise {} to {} using {}", () -> getEaObject().getName(),
                () -> getEaObject().getTopicId().orElseThrow(), this::getClass);
        appendTags();
        appendTitle();
        appendBody();
        syncWiki();
        if (recursive) {
            for (var subObject : subObjects) {
                subObject.getObject().sync(wikiClient, true);
            }
        }
    }
}
