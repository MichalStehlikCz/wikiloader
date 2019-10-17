package com.provys.wikiloader.wikimap;

import com.provys.provyswiki.ProvysWikiClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Diagram;
import org.sparx.Element;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Helper class, represents element mapping to wiki
 */
public class WikiElement {

    private static final Logger LOG = LogManager.getLogger(WikiElement.class);

    private static final WikiElement EMPTY_ELEMENT_INFO = new WikiElement();

    /**
     * Create information about element mapping to wiki based on supplied element
     *
     * @param element is EA repository element
     * @param wikiMap is link resolver that should be used for master element and package mapping
     * @return element information for given element
     */
    static WikiElement of(Element element, WikiMap wikiMap) {
        if (element.GetType().equals("UMLDiagram")) {
            // UMLDiagram element is in fact pointer to diagram, is not exported to wiki, but links to associated
            // diagram
            Diagram diagram = (Diagram) element.GetCompositeDiagram();
            if (diagram == null) {
                LOG.info("Linked diagram missing in UMLDiagram element {}", element::GetName);
                return EMPTY_ELEMENT_INFO;
            }
            return wikiMap.getDiagramLink(diagram)
                    .map(link -> new WikiElement(null, link, null, null))
                    .orElse(EMPTY_ELEMENT_INFO);
        }
        if (element.GetAlias().isEmpty() && !element.GetType().equals("UMLDiagram")) {
            // element without alias is not exported -> no link
            return EMPTY_ELEMENT_INFO;
        }
        Optional<String> parentNamespace;
        if (element.GetParentID() > 0) {
            parentNamespace = wikiMap.getElementNamespace(element.GetParentID());
        } else {
            parentNamespace = wikiMap.getPackageNamespace(element.GetPackageID());
        }
        if (parentNamespace.isEmpty()) {
            return EMPTY_ELEMENT_INFO;
        }
        String link = parentNamespace.get() + ":" + element.GetAlias().toLowerCase();
        String relLink = element.GetAlias().toLowerCase();
        String namespace = null;
        String topic;
        if (element.GetType().equals("Boundary")) {
            // Boundary is not exported, but if it has alias, it links to given package...
            link = link + ":";
            relLink = null;
            topic = null;
        } else {
            // Normal element... if sub-elements are present, translates to namespace
            var subElements = element.GetElements();
            var diagrams = element.GetDiagrams();
            try {
                if ((subElements.GetCount() > 0) || (diagrams.GetCount() > 0) ||
                        element.GetStereotype().equals("ArchiMate_Product")) {
                    // if there are sub-elements and diagrams, we need namespace to contain them in
                    // Product element generates bunch of topics and thus also needs namespace
                    namespace = link;
                    link = link + ":";
                    relLink = "." + relLink + ":";
                    topic = ProvysWikiClient.START;
                } else {
                    topic =  element.GetAlias().toLowerCase();
                }
            } finally {
                diagrams.destroy();
                subElements.destroy();
            }
        }
        return new WikiElement(namespace, link, relLink, topic);
    }

    static WikiElement of(int elementId, WikiMap wikiMap) {
        var element = wikiMap.getRepository().GetElementByID(elementId);
        try {
            return of(element, wikiMap);
        } finally {
            element.destroy();
        }
    }

    /**
     * Namespace corresponding to given element. Only filled in when element has sub-elements and thus translates
     * to namespace
     */
    @Nullable
    private final String namespace;

    /**
     * Link used to refer to topic. If element translates to namespace, it is namespace link (ends with :). It
     * does not contain initial :
     */
    @Nullable
    private final String link;

    /**
     * Link relative to parent namespace. Either topic name of .topic:
     */
    @Nullable
    private final String relLink;

    /**
     * Topic (file) name, without namespace. Empty when no topic is created for element (e.g. element is not
     * exported or element is boundary and maps to package)
     */
    @Nullable
    private final String topic;

    /**
     * Create empty element link; should be accessed via static singleton, used to represent elements without alias
     */
    private WikiElement(@Nullable String namespace, @Nullable String link, @Nullable String relLink,
                        @Nullable String topic) {
        this.namespace = namespace;
        this.link = link;
        this.relLink = relLink;
        this.topic = topic;
    }

    /**
     * Create empty element link
     */
    private WikiElement() {
        this.namespace = null;
        this.link = null;
        this.relLink = null;
        this.topic = null;
    }

    /**
     * @return namespace this element defines; empty if element is not mapped to wiki or it is simple topic
     */
    public Optional<String> getNamespace() {
        return Optional.ofNullable(namespace);
    }

    /**
     * @return link to given element; link to corresponding namespace (defined by package) for boundary elements.
     * Empty if element is not mapped to wiki
     */
    Optional<String> getLink() {
        return (link == null) ? Optional.empty() : Optional.of(":" + link);
    }

    /**
     * @return link to given element, relative from enclosing namespace
     */
    public Optional<String> getRelLink() {
        return Optional.ofNullable(relLink);
    }

    /**
     * @return topic name
     */
    public Optional<String> getTopic() {
        return Optional.ofNullable(topic);
    }

    /**
     * @return Id of document, corresponding to element. Is start document in namespace for element with
     * sub-elements
     */
    public Optional<String> getTopicId() {
        if (topic == null) {
            return Optional.empty();
        }
        return (namespace == null) ? Optional.ofNullable(link) : Optional.of(namespace + ":" + topic);
    }
}
