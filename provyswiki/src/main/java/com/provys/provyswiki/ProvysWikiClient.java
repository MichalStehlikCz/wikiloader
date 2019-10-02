package com.provys.provyswiki;

import com.provys.dokuwiki.DokuWikiClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Extension of generic DokuWiki client, that implements functionality depending on structure of provys wikipedia -
 * stuff like having unified sidebar documents in each namespace, synchronizing content of given namespace (directory)
 * and creation of corresponding content file etc.
 */
@SuppressWarnings("WeakerAccess")
public class ProvysWikiClient extends DokuWikiClient {

    /** Name of topic that defines sidebar for given namespace */
    public static final String SIDEBAR = "sidebar";
    /** Name of topic that defines content for given namespace */
    public static final String CONTENT = "content";
    /** Name of topic that acts as landing page for given namespace */
    public static final String START = "start";

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
    public void syncContent(String namespace, List<String> topics) {
        String contentId = namespace + ":" + CONTENT;
        if (topics.isEmpty()) {
            // if content is empty, it has to be deleted, put might fail if no previous content exists...
            if (!getPage(contentId).isEmpty()) {
                deletePage(contentId);
            }
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (var topic : topics) {
            if ((topic == null) || (topic.isEmpty())) {
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
        deletePageIfExists(namespace + ":" + CONTENT);
    }

    /**
     * Delete sub-namespaces that are not referenced from content. It expects that content contains references to
     * namespaces in form {@code ".name:"}.
     */
    public void deleteUnusedNamespaces(String namespace, List<String> used) {
        Set<String> usedSet = new HashSet<>(used.size());
        for (var name : used) {
            if ((name.charAt(0) == '.') && (name.charAt(name.length()-1) == ':')) {
                usedSet.add(name.substring(1, name.length() - 1));
            }
        }
        getNamespaceNames(namespace)
                .stream()
                .filter(name -> !usedSet.contains(name))
                .map(name -> namespace + ":" + name)
                .forEach(this::deleteNamespace);
    }

    /**
     * Delete topics that are not referenced from content and are not standard topics (sidebar, content, start).
     */
    public void deleteUnusedPages(String namespace, List<String> used) {
        Set<String> usedSet = new HashSet<>(used);
        usedSet.add(SIDEBAR);
        usedSet.add(START);
        usedSet.add(CONTENT);
        getPageNames(namespace)
                .stream()
                .filter(page -> !usedSet.contains(page))
                .map(page -> namespace + ":" + page)
                .forEach(this::deletePage);
    }
}
