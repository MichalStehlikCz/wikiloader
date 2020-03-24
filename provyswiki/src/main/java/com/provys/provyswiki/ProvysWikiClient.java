package com.provys.provyswiki;

import com.provys.dokuwiki.DokuWikiClient;
import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Extension of generic DokuWiki client, that implements functionality depending on structure of provys wikipedia -
 * stuff like having unified sidebar documents in each namespace, synchronizing content of given namespace (directory)
 * and creation of corresponding content file etc.
 */
@SuppressWarnings("WeakerAccess")
@Component
public class ProvysWikiClient extends DokuWikiClient {

    private static final Logger LOG = LogManager.getLogger(ProvysWikiClient.class);

    /** Name of topic that defines sidebar for given namespace */
    public static final String SIDEBAR = "sidebar";
    /** Name of topic that defines content for given namespace */
    public static final String CONTENT = "content";
    /** Name of topic that acts as landing page for given namespace */
    public static final String START = "start";

    /**
     * Create new provys-wiki client instance. Read all parameters from application configuration
     */
    @Autowired
    public ProvysWikiClient(ProvysWikiConfiguration configuration) {
        super(configuration.getUrl(), configuration.getUser(), configuration.getPwd());
    }

    /**
     * Create new provys-wiki client instance.
     *
     * @param url      is url used to access xml-rpc endpoint of DokuWiki
     * @param userName used to login to wiki
     * @param password used to login to wiki
     */
    public ProvysWikiClient(String url, String userName, String password) {
        super(url, userName, password);
    }

    /**
     * Create or replace sidebar in given namespace.
     *
     * @param namespace is namespace for which sidebar should be created / repaired
     */
    public void syncSidebar(String namespace) {
        int depth = getPageIdParser().getDepth(namespace);
        StringBuilder builder = new StringBuilder().append("{{page>")
                .append("..:".repeat(depth - 1))
                .append(SIDEBAR).append("}}\n\n----\nContent ([[").append(CONTENT).append("?do=edit|edit]])\n");
        for (int i = depth - 2; i > 0; i--) {
            builder.append("  ".repeat(depth - i - 2))
                    .append("  * [[")
                    .append("..:".repeat(i))
                    .append("]]\n");
        }
        builder.append("  ".repeat(depth - 2))
                .append("  * [[.:]]{{page>").append(CONTENT).append("}}");
        putPage(namespace + ":" + SIDEBAR, builder.toString());
    }

    /**
     * Delete sidebar in given namespace. Used when namespace is being converted to simple topic. Action is safe even if
     * sidebar does not exist
     *
     * @param namespace is namespace in which sidebar should be deleted
     */
    public void deleteSidebarIfExists(String namespace) {
        deletePageIfExists(namespace + ":" + SIDEBAR);
    }

    /**
     * Create or replace content in given namespace. Caller suppliers list of topics that should be included in content;
     * does no validation of topics, just constructs content based on them... If supplied string is empty, empty line is
     * added to content. If supplied string is ---, adds separator to content
     */
    public void syncContent(String namespace, Collection<String> topics) {
        String contentId = namespace + ':' + CONTENT;
        if (topics.isEmpty()) {
            // if content is empty, it has to be deleted, put might fail if no previous content exists...
            if (!getPage(contentId).isEmpty()) {
                deletePage(contentId);
            }
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (var topic : topics) {
            if ((topic == null) || topic.isEmpty()) {
                builder.append('\n');
            } else if (topic.equals("\\\\")) {
                builder.append("\\\\\n");
            } else {
                builder.append("  * [[").append(topic).append("]]\n");
            }
        }
        putPage(contentId, builder.toString());
    }

    /**
     * Delete content in given namespace. Used when namespace is being converted to simple topic. Action is safe even if
     * content does not exist
     *
     * @param namespace is namespace in which content should be deleted
     */
    public void deleteContentIfExists(String namespace) {
        deletePageIfExists(namespace + ':' + CONTENT);
    }

    /**
     * Insert specified page to wiki; prefix page with generated tag. Action skipped if current page contains manual tag
     *
     * @param id is wiki topic id
     * @param text is text to be placed to this topic. It will be prefixed with generated tag
     */
    public void putGeneratedPage(String id, String text) {
        if (!getPage(id).contains("{{tag>manual}}")) {
            putPage(id, "{{tag>generated}}\n" + text);
        }
    }

    /**
     * Check if page doesn't exist or it contains empty tag.
     *
     * @param id is wiki topic id
     * @return true if page doesn't exist or is marked as empty, false otherwise
     */
    public boolean isPageEmpty(String id) {
        var origText = getPage(id);
        return origText.isEmpty() || origText.startsWith("{{tag>empty}}");
    }

    /**
     * Insert specified page to wiki if page doesn't exist yet; prefix page with empty tag. If page already exists, does
     * nothing
     *
     * @param id is wiki topic id
     * @param text is text to be placed to this topic. It will be prefixed with empty tag
     */
    public void putPageIfEmpty(String id, String text) {
        if (isPageEmpty(id)) {
            putPage(id, "{{tag>empty}}\n" + text);
        }
    }

    /**
     * Delete sub-namespaces that are not referenced from content. It expects that content contains references to
     * namespaces in form {@code ".name:"}.
     */
    public void deleteUnusedNamespaces(String namespace, List<String> used) {
        var usedSet = used.stream()
                .filter(name -> (name.charAt(0) == '.') && (name.charAt(name.length()-1) == ':'))
                .map(name -> name.substring(1, name.length() - 1))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        getNamespaceNames(namespace)
                .stream()
                .filter(name -> !usedSet.contains(name))
                .map(name -> namespace + ":" + name)
                .forEach(delNameSpace -> {
                    LOG.info("Delete unused namespace {}", delNameSpace);
                    deleteNamespace(delNameSpace);
                });
    }

    /**
     * Delete topics that are not referenced from content and are not standard topics (sidebar, content, start).
     */
    public void deleteUnusedPages(String namespace, List<String> used) {
        Set<String> usedSet = used.stream()
                .map(pageId -> (pageId.charAt(0) == '.') ? pageId.substring(1) : pageId)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        usedSet.add(SIDEBAR);
        usedSet.add(START);
        usedSet.add(CONTENT);
        getPageNames(namespace)
                .stream()
                .filter(page -> !usedSet.contains(page))
                .map(page -> namespace + ":" + page)
                .forEach(pageId -> {
                    LOG.info("Delete unused page {}", pageId);
                    deletePage(pageId);
                });
    }
}
