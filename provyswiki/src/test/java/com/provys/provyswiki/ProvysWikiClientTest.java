package com.provys.provyswiki;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProvysWikiClientTest {

    private static final ProvysWikiClient provysWikiClient = new ProvysWikiClient(
            "http://provys-wiki.dcit.cz/lib/exe/xmlrpc.php", "stehlik", "stehlik");

    @Test
    void syncSidebarTest() {
        if (!provysWikiClient.getPage("playground:java:sidebar:sidebar").isEmpty()) {
            provysWikiClient.deletePage("playground:java:sidebar:sidebar");
        }
        provysWikiClient.syncSidebar("playground:java:sidebar");
        assertThat(provysWikiClient.getPage("playground:java:sidebar:sidebar")).isEqualTo(
                "{{page>..:..:..:sidebar}}\n" +
                "\n" +
                "----\n" +
                "Content ([[content?do=edit|edit]])\n" +
                "  * [[..:..:]]\n" +
                "    * [[..:]]\n" +
                "      * [[.:]]{{page>content}}");
        provysWikiClient.syncSidebar("playground:java:sidebar:sub1");
        assertThat(provysWikiClient.getPage("playground:java:sidebar:sub1:sidebar")).isEqualTo(
                "{{page>..:..:..:..:sidebar}}\n" +
                        "\n" +
                        "----\n" +
                        "Content ([[content?do=edit|edit]])\n" +
                        "  * [[..:..:..:]]\n" +
                        "    * [[..:..:]]\n" +
                        "      * [[..:]]\n" +
                        "        * [[.:]]{{page>content}}");
    }

    @Test
    void syncContentTest() {
        provysWikiClient.syncContent("playground:java:content",
                List.of("topic1", ".dir1:", "", ".dir2:", "\\\\", "topic2"));
        assertThat(provysWikiClient.getPage("playground:java:content:content")).isEqualTo(
                "  * [[topic1]]\n" +
                "  * [[.dir1:]]\n" +
                "\n" +
                "  * [[.dir2:]]\n" +
                "\\\\\n" +
                "  * [[topic2]]\n");
    }

    @Test
    void syncEmptyContentTest() {
        if (!provysWikiClient.getPage("playground:java:emptycontent:content").isEmpty()) {
            provysWikiClient.deletePage("playground:java:emptycontent:content");
        }
        provysWikiClient.syncContent("playground:java:emptycontent", Collections.emptyList());
        assertThat(provysWikiClient.getPage("playground:java:emptycontent:content")).isEmpty();
    }

    @Test
    void deleteUnusedNamespacesTest() {
        provysWikiClient.putPage("playground:java:deleteunusedns:page1", "text1");
        provysWikiClient.putPage("playground:java:deleteunusedns:page2", "text2");
        provysWikiClient.putPage("playground:java:deleteunusedns:sub1:sidebar", "sidebar text1");
        provysWikiClient.putPage("playground:java:deleteunusedns:sub1:page", "text1");
        provysWikiClient.putPage("playground:java:deleteunusedns:sub2:sidebar", "sidebar text2");
        provysWikiClient.putPage("playground:java:deleteunusedns:sub3:page", "text1");
        provysWikiClient.deleteUnusedNamespaces("playground:java:deleteunusedns", List.of("page1", ".sub1:"));
        assertThat(provysWikiClient.getPage("playground:java:deleteunusedns:page1")).isEqualTo("text1");
        assertThat(provysWikiClient.getPage("playground:java:deleteunusedns:page2")).isEqualTo("text2");
        assertThat(provysWikiClient.getPage("playground:java:deleteunusedns:sub1:sidebar")).isEqualTo("sidebar text1");
        assertThat(provysWikiClient.getPage("playground:java:deleteunusedns:sub1:page")).isEqualTo("text1");
        assertThat(provysWikiClient.getPage("playground:java:deleteunusedns:sub2:sidebar")).isEmpty();
        assertThat(provysWikiClient.getPage("playground:java:deleteunusedns:sub3:page")).isEmpty();
    }

    @Test
    void deleteUnusedPagesTest() {
        provysWikiClient.putPage("playground:java:deleteunused:page1", "text1");
        provysWikiClient.putPage("playground:java:deleteunused:page2", "text2");
        provysWikiClient.putPage("playground:java:deleteunused:sub:page", "text");
        provysWikiClient.putPage("playground:java:deleteunused:sidebar", "sidebar text");
        provysWikiClient.putPage("playground:java:deleteunused:start", "start text");
        provysWikiClient.putPage("playground:java:deleteunused:content", "content text");
        provysWikiClient.deleteUnusedPages("playground:java:deleteunused", List.of("page1", "testpage"));
        assertThat(provysWikiClient.getPage("playground:java:deleteunused:page1")).isEqualTo("text1");
        assertThat(provysWikiClient.getPage("playground:java:deleteunused:page2")).isEmpty();
        assertThat(provysWikiClient.getPage("playground:java:deleteunused:sidebar")).isEqualTo("sidebar text");
        assertThat(provysWikiClient.getPage("playground:java:deleteunused:start")).isEqualTo("start text");
        assertThat(provysWikiClient.getPage("playground:java:deleteunused:content")).isEqualTo("content text");
        assertThat(provysWikiClient.getPage("playground:java:deleteunused:sub:page")).isEqualTo("text");
    }
}