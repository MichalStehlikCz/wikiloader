package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaObject;
import com.provys.wikiloader.earepository.EaObjectRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
     * Create section for linked objects and append it to startBuilder.
     * General rule is that if there is no linked objects, noEntry string is used. If there is one linked object,
     * singleEntry string + link to this object is appended. If there are multiple entries, last string is used and
     * entries are then exported to bullet list
     *
     * @param noEntry is string used when no entries are in list
     * @param singleEntry is string used when single entry is in list; should not contain trailing space
     * @param multiEntries is string used with multiple entries; should not contain trailing colon
     * @param entries is list of entries to be exported
     */
    void appendList(@Nullable String noEntry, String singleEntry, String multiEntries,
                    List<? extends EaObjectRef> entries) {
        if (entries.isEmpty()) {
            if (noEntry != null) {
                startBuilder.append(noEntry).append("\\\\\n");
            }
        } else if (entries.size() == 1) {
            startBuilder.append(singleEntry).append(" ");
            entries.get(0).appendWikiLink(startBuilder);
            startBuilder.append(".\\\\\n");
        } else {
            startBuilder.append(multiEntries).append(":\\\\\n");
            for (var entry : entries) {
                startBuilder.append("  * ");
                entry.appendWikiLink(startBuilder);
                startBuilder.append("\\\\\n");
            }
        }
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
        getEaObject().getNotes().ifPresent(notes -> startBuilder.append(notes).append("\n"));
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
