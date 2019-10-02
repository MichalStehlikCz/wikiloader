package com.provys.wikiloader.elementhandlers;

import com.provys.common.exception.InternalException;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.impl.*;
import com.provys.wikiloader.wikimap.WikiElement;
import com.provys.wikiloader.wikimap.WikiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Element;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

abstract class ElementHandlerBase implements ElementHandler {

    private static final Logger LOG = LogManager.getLogger(ElementHandlerBase.class);

    @Nonnull
    private final Element element;
    @Nonnull
    private final WikiElement info;
    @Nonnull
    private final ElementHandlerFactory elementHandlerFactory;
    @Nonnull
    private final WikiMap wikiMap;

    ElementHandlerBase(Element element, WikiElement info, ElementHandlerFactory elementHandlerFactory,
                       WikiMap wikiMap) {
        this.element = Objects.requireNonNull(element);
        if (element.GetAlias().isEmpty()) {
            throw new InternalException(LOG, "Cannot process element with empty alias");
        }
        this.info = Objects.requireNonNull(info);
        this.elementHandlerFactory = Objects.requireNonNull(elementHandlerFactory);
        this.wikiMap = wikiMap;
    }

    /**
     * @return element this handler is used for
     */
    @Nonnull
    Element getElement() {
        return element;
    }

    /**
     * @return topic id
     */
    @Nonnull
    private String getId() {
        return info.getTopicId().orElseThrow();
    }

    /**
     * @return name (lower-case of alias) used as topic name
     */
    @Nonnull
    @Override
    public String getRelLink() {
        return info.getRelLink().orElseThrow();
    }

    /**
     * @return namespace for element mapped to namespace
     */
    @Nonnull
    private Optional<String> getNamespace() {
        return info.getNamespace();
    }

    @Nonnull
    ElementHandlerFactory getElementHandlerFactory() {
        return elementHandlerFactory;
    }

    @Nonnull
    WikiMap getWikiMap() {
        return wikiMap;
    }

    /**
     * Append title (by default using name of object)
     *
     * @param builder is {@code StringBuilder} title should be appended to
     */
    void appendTitle(StringBuilder builder) {
        builder.append("===== ").append(getElement().GetName()).append(" =====\n");
    }

    private void appendDiagram(StringBuilder builder, ProvysWikiClient wikiClient) {
        // handle diagrams
        var diagrams = getDiagrams();
        if (diagrams.size() == 1) {
            // if there is just one diagram, we expect it illustrates element and put it inline
            builder.append("{{page>").append(diagrams.get(0).getRelLink()).append("&noheader}}\n");
            diagrams.get(0).sync(wikiClient);
        }
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
    abstract void appendDocument(StringBuilder builder);

    private List<DiagramHandler> getDiagrams() {
        var diagrams = element.GetDiagrams();
        var result = new ArrayList<DiagramHandler>(diagrams.GetCount());
        for (var diagram : diagrams) {
            result.add(new DiagramHandler(diagram, wikiMap));
        }
        diagrams.destroy();
        return result;
    }

    private List<ElementHandler> getElements() {
        var subElements = element.GetElements();
        var result = new ArrayList<ElementHandler>(subElements.GetCount());
        for (var subElement : subElements) {
            elementHandlerFactory.getElementHandler(subElement, wikiMap).ifPresent(result::add);
        }
        subElements.destroy();
        return result;
    }

    private void appendElement(ElementHandler element, ProvysWikiClient wikiClient, StringBuilder startBuilder,
                               List<String> contentBuilder) {
        startBuilder.append("  * [[").append(element.getRelLink()).append("]]\n");
        contentBuilder.add(element.getRelLink());
        element.sync(wikiClient);
    }

    private void appendSubElements(StringBuilder builder, ProvysWikiClient wikiClient) {
        if (getNamespace().isEmpty()) {
            return;
        }
        boolean used = false;
        @SuppressWarnings("squid:S3655") // value checked in if above, function deterministic...
        var namespace = getNamespace().get();
        LOG.info("Synchronize subelements of {} to namespace {}", element::GetAlias,
                () -> namespace);
        List<String> contentBuilder = new ArrayList<>(10);
        // handle diagrams
        var diagrams = getDiagrams();
        if (diagrams.size() > 1) {
            used = true;
            // multiple diagrams are exported as links in diagram section
            builder.append("\n==== Diagrams ====\n");
            for (var diagram : diagrams) {
                appendElement(diagram, wikiClient, builder, contentBuilder);
            }
        } else if (diagrams.size() == 1) {
            // diagram inserted inline, but we still should not delete it...
            contentBuilder.add(diagrams.get(0).getRelLink());
        }
        // handle elements
        var subElements = getElements();
        if (!subElements.isEmpty()) {
            used = true;
            builder.append("\n==== Objects ====\n");
            if (!contentBuilder.isEmpty()) {
                contentBuilder.add("\\\\");
            }
            for (var subElement : subElements) {
                appendElement(subElement, wikiClient, builder, contentBuilder);
            }
        }
        // synchronize to wiki
        if (used) {
            wikiClient.syncSidebar(namespace);
            wikiClient.syncContent(namespace, contentBuilder);
        } else {
            wikiClient.deleteSidebarIfExists(namespace);
            wikiClient.deleteContentIfExists(namespace);
        }
        wikiClient.deleteUnusedNamespaces(namespace, contentBuilder);
        wikiClient.deleteUnusedPages(namespace, contentBuilder);
    }

    /**
     * Synchronize wiki page corresponding to given element
     */
    @Override
    public void sync(ProvysWikiClient wikiClient) {
        LOG.info("Synchronize document {}", this::getId);
        var builder = new StringBuilder();
        appendTitle(builder);
        appendDiagram(builder, wikiClient);
        appendDocument(builder);
        appendSubElements(builder, wikiClient);
        wikiClient.putPage(this.getId(), builder.toString());
    }
}
