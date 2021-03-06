package com.provys.wikiloader.earepository;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Common interface for packages and elements as these two objects all together form tree of ea repository objects
 */
public interface EaObjectRef extends Comparable<EaObjectRef> {

    /**
     * @return repository this object reference has been retrieved from
     */
    @Nonnull
    EaRepository getRepository();

    /**
     * @return parent object
     */
    @Nonnull
    Optional<EaObjectRef> getParent();

    /**
     * @return model this object is part of
     */
    @Nonnull
    EaModel getModel();

    /**
     * @return name of given object; note that it is Enterprise Architect name, not necessarily title of given topic
     */
    @Nonnull
    String getName();

    /**
     * @return name of given object, but with sections enclosed in [] removed. This simple name is used to construct
     * title
     */
    @Nonnull
    String getPlainName();

    /**
     * @return title of main page of given object on wiki
     */
    @Nonnull
    String getTitle();

    /**
     * @return short title of topic; used on  wiki in context of given guide (e.g. title often includes information
     * about object type, short title is usually just object name)
     */
    @Nonnull
    String getShortTitle();

    /**
     * @return link in wiki format if given topic is exported to wiki, with short title used as text of link and short
     * title in case topic is not exported
     */
    @Nonnull
    String getWikiLink();

    /**
     * Append wiki link to given builder
     */
    void appendWikiLink(StringBuilder builder);

    /**
     * @return alias of given object
     */
    @Nonnull
    Optional<String> getAlias();

    /**
     * @return type of given object; Package, Diagram or value of type property for elements
     */
    @Nonnull
    String getType();

    /**
     * @return stereotype of given object; for packages, StereotypeEx is used instead
     */
    @Nonnull
    Optional<String> getStereotype();

    /**
     * @return description of enterprise architect object, used in log messages
     */
    @Nonnull
    String getEaDesc();

    /**
     * @return full object this reference represents, read from repository
     */
    @Nonnull
    EaObject getObject();

    /**
     * @return true if given object is element of type, that is never exported (boundaries,  diagram links, ...)
     */
    boolean isIgnoredType();

    /**
     * @return true if given object is exported as topic to wiki, false otherwise. Similar to hasLink, but hasLink
     * returns true even for some elements (boundary, UMLDiagram) that are not exported on their own
     */
    boolean isTopic();

    /**
     * Get wiki Id of topic, corresponding to given Enterprise Architect object. Empty optional if no topic is generated
     * from given object. Topic does not have leading : and is full topic Id (including start topic name if appropriate)
     *
     * @return topic this object exports to
     */
    @Nonnull
    Optional<String> getTopicId();

    /**
     * Get namespace, corresponding to given topic in wiki. Only if Enterprise Architect object translates to namespace,
     * empty if it translates to single topic. Namespace does not have leading nor trailing :
     *
     * @return namespace this object exports to
     */
    @Nonnull
    Optional<String> getNamespace();

    /**
     * Get link to topic, with path relative from parent object's namespace. Starts with . if it is namespace or
     * if it points to element in sub-namespace, might be full path if linked topic is not under parent
     *
     * @return link to topic from parent's namespace
     */
    @Nonnull
    Optional<String> getParentLink();

    /**
     * @return true if there is topic on wiki that represents this object, false otherwise. If it is false, calls to
     * appendLink and appendParentLink throw exception
     */
    boolean hasLink();

    /**
     * Append namespace, corresponding to this object. If object does not map to namespace, throws exception. Namespace
     * does not have leading semicolon
     *
     * @param builder is {@code StringBuilder} namespace should be appended to
     * @param trailingColon defines if trailing should or should not be appended
     */
    void appendNamespace(StringBuilder builder, boolean trailingColon);

    /**
     * Append link to topic, corresponding to this object. Link starts with :. Namespace link ends with : without final
     * start topic name
     *
     * @param builder is {@code StringBuilder} link should be appended to
     */
    void appendLink(StringBuilder builder);

    /**
     * Append link to topic, with path relative from parent object's namespace. Starts with . if it is namespace or
     * if it points to element in sub-namespace, might be full path if linked topic is not under parent
     *
     * @param builder is {@code StringBuilder} link should be appended to
     */
    void appendParentLink(StringBuilder builder);

    /**
     * Append all topics and namespaces generated by this element to parent's namespace
     *
     * @param pages is collection of pages, where this handler's generated topics and namespaces should be added to
     */
    void appendPages(Collection<? super String> pages);

    /**
     * Retrieve position of given items, defined as list of tree positions, starting from root package (model). Used to
     * implement comparable
     *
     * @return list with tree positions, starting from root package to given object
     */
    @Nonnull
    List<Integer> getPos();
}
