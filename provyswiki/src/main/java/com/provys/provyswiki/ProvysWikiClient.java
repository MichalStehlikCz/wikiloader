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
        StringBuilder builder = new StringBuilder().append("{{page>");
        for (int i = 1; i < depth; i++) {
            builder.append("..:");
        }
        builder.append("sidebar}}\n\n----\nContent ([[content?do=edit|edit]])\n");
        for (int i = depth - 2; i > 0; i--) {
            for (int j = depth - 2; j > i; j--) {
                builder.append("  ");
            }
            builder.append("  * [[");
            for (int j = 0; j < i; j++) {
                builder.append("..:");
            }
            builder.append("]]\n");
        }
        for (int j = depth - 2; j > 0; j--) {
            builder.append("  ");
        }
        builder.append("  * [[.:]]{{page>content}}");
        putPage(namespace + ":sidebar", builder.toString());
    }

    /**
     * Create or replace content in given namespace. Caller suppliers list of topics that should be included in content;
     * does no validation of topics, just constructs content based on them... If supplied string is empty, empty line is
     * added to content. If supplied string is ---, adds separator to content
     */
    public void syncContent(String namespace, List<String> topics) {
        String contentId = namespace + ":content";
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
        usedSet.add("sidebar");
        usedSet.add("start");
        usedSet.add("content");
        getPageNames(namespace)
                .stream()
                .filter(page -> !usedSet.contains(page))
                .map(page -> namespace + ":" + page)
                .forEach(this::deletePage);
    }
}
