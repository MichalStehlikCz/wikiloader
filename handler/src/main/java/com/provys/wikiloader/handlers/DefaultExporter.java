package com.provys.wikiloader.handlers;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.impl.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Class represents ancestor of different exporters. It is mutable class, used to build wiki page and export it
 *
 * @param <T> is handler this exporter exports to wiki
 */
class DefaultExporter<T extends HandlerInt> implements Exporter {

    private static final Logger LOG = LogManager.getLogger(DefaultExporter.class);

    @Nonnull
    private final T handler;
    @Nonnull
    private final ProvysWikiClient wikiClient;
    @Nonnull
    final StringBuilder startBuilder = new StringBuilder();
    @Nonnull
    final List<String> contentBuilder = new ArrayList<>(10);
    @Nonnull
    final List<String> pages = new ArrayList<>(10);
    @Nonnull
    private final Collection<Handler> diagrams;
    @Nonnull
    private final Collection<Handler> subPackages;
    @Nonnull
    private final Collection<Handler> elements;

    DefaultExporter(T handler, ProvysWikiClient wikiClient) {
        this.handler = Objects.requireNonNull(handler);
        this.wikiClient = Objects.requireNonNull(wikiClient);
        this.diagrams = handler.getDiagrams();
        this.subPackages = handler.getSubPackages();
        this.elements = handler.getElements();
    }

    @Nonnull
    T getHandler() {
        return handler;
    }

    @Nonnull
    ProvysWikiClient getWikiClient() {
        return wikiClient;
    }

    @Nonnull
    Collection<Handler> getDiagrams() {
        return Collections.unmodifiableCollection(diagrams);
    }

    @Nonnull
    Collection<Handler> getSubPackages() {
        return Collections.unmodifiableCollection(subPackages);
    }

    @Nonnull
    Collection<Handler> getElements() {
        return Collections.unmodifiableCollection(elements);
    }

    /**
     * Append tags, appropriate for given element. Empty in this ancestor, but might be overriden in ancestors to
     * insert tags, needed by given document type
     */
    void appendTags() {
        // empty in this ancestor
    }

    /**
     * Get title of exported topic. As a default, we just take title as defined in Enterprise Architect
     *
     * @return topic title
     */
    @Nonnull
    String getTitle() {
        return handler.getEaName();
    }

    /**
     * Append title to page being built
     */
    void appendTitle() {
        startBuilder.append("====== ").append(getTitle()).append(" ======\n");
    }

    /**
     * Append alias (line with Alias: <<alias>>).
     */
    void appendAlias() {
        var alias = getHandler().getEaAlias();
        if (!alias.isEmpty()) {
            startBuilder.append("Alias: ").append(alias).append("\\\\\n");
        }
    }

    /**
     * Place item inline inside page
     *
     * @param element is handler for item to be added
     * @param showHeader defines if header should be used or removed
     */
    void inlineElement(Handler element, boolean showHeader) {
        startBuilder.append("{{page>")
                .append(element.getRelLink())
                .append(showHeader ? "" : "&noheader")
                .append("}}\n");
        element.appendPages(pages);
    }

    /**
     * Append link to item to page
     *
     * @param element is handler for item to be added
     */
    void appendElement(Handler element) {
        startBuilder.append("  * [[").append(element.getRelLink()).append("]]\n");
        contentBuilder.add(element.getRelLink());
        element.appendPages(pages);
    }

    /**
     * Append diagrams to document. By default, inserts all diagrams inline. If tehre is more than one diagram, show
     * diagram headers, in case of single diagram assumes that it is overview and clips title
     */
    void appendDiagrams() {
        for (var diagram : getDiagrams()) {
            inlineElement(diagram, (getDiagrams().size() > 1));
        }
    }

    /**
     * Append notes.
     */
    void appendNotes() {
        var notes = getHandler().getEaNotes();
        if (!notes.isEmpty()) {
            startBuilder.append(notes);
        }
    }

    /**
     * Append document - middle section of body
     */
    void appendDocument() {
        appendNotes();
    }

    void appendBody() {
        // handle diagrams
        appendDiagrams();
        // insert own content
        appendDocument();
        // handle sub-packages
        if (!subPackages.isEmpty()) {
            startBuilder.append("\n===== Areas =====\n");
            for (var subPackage : subPackages) {
                appendElement(subPackage);
            }
        }
        // handle elements
        if (!elements.isEmpty()) {
            startBuilder.append("\n===== Objects =====\n");
            if (!contentBuilder.isEmpty()) {
                contentBuilder.add("\\\\");
            }
            for (var element : elements) {
                appendElement(element);
            }
        }
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
        wikiClient.putPage(getHandler().getId(), startBuilder.toString());
        getHandler().getNamespace().ifPresent(this::syncNamespace);
    }

    @Nonnull
    public Collection<Handler> run(boolean recursive) {
        LOG.info("Synchronise {} to {} using {}", () -> getHandler().getEaName(), () -> getHandler().getId(),
                this::getClass);
        appendTags();
        appendTitle();
        appendBody();
        syncWiki();
        var postSync = new ArrayList<Handler>(diagrams.size()
                + (recursive ? elements.size() + subPackages.size() : 0));
        postSync.addAll(diagrams);
        if (recursive) {
            postSync.addAll(elements);
            postSync.addAll(subPackages);
        }
        return postSync;
    }
}
