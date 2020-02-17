package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObjectRef;
import com.provys.wikiloader.earepository.EaRepository;
import com.provys.wikiloader.earepository.WikiSetBuilder;
import com.provys.wikiloader.earepository.WikiSetObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

class WikiSetBuilderImpl implements WikiSetBuilder {

    @Nonnull
    private final EaRepository eaRepository;
    private int threshold = 5;
    private final Set<EaObjectRef> objects = new HashSet<>();

    WikiSetBuilderImpl(EaRepository eaRepository) {
        this.eaRepository = Objects.requireNonNull(eaRepository);
    }

    @Nonnull
    @Override
    public WikiSetBuilder setThreshold(int threshold) {
        this.threshold = threshold;
        return this;
    }

    @Nonnull
    @Override
    public WikiSetBuilder addPackageById(int packageId) {
        objects.add(eaRepository.getPackageRefById(packageId));
        return this;
    }

    @Nonnull
    @Override
    public WikiSetBuilder addElementById(int elementId) {
        objects.add(eaRepository.getElementRefById(elementId));
        return this;
    }

    @Nonnull
    @Override
    public WikiSetBuilder addEaObjects(Collection<? extends EaObjectRef> newObjects) {
        objects.addAll(newObjects);
        return this;
    }

    @Nonnull
    @Override
    public List<WikiSetObject> build() {
        var setBuilder = new SetBuilder(threshold);
        for (var object : objects) {
            setBuilder.addObject(object, true, null);
        }
        return setBuilder.build().stream()
                .map(SetMember::getWikiSetObject)
                .flatMap(member -> member.getChildren().stream())
                .collect(Collectors.toList());
    }

    /**
     * Class builds WikiSetObject; it relies on SetBuilder to ensure that there is only single instance of SetMember for
     * given EaObject
     */
    private static class SetMember {
        @Nonnull
        private final EaObjectRef object;
        private boolean mandatory;
        @Nullable
        private SetMember overview = null;
        @Nonnull
        Set<SetMember> children = new HashSet<>();

        SetMember(EaObjectRef object, boolean mandatory) {
            this.object = object;
            this.mandatory = mandatory;
        }

        /**
         * Get object reference this member represents
         *
         * @return object reference this member represents
         */
        @Nonnull
        private EaObjectRef getObject() {
            return object;
        }

        @Nullable
        public SetMember getOverview() {
            return overview;
        }

        public void setOverview(@Nonnull SetMember overview) {
            this.overview = overview;
        }

        /**
         * Reduce children of this member if appropriate and indicates if this member should be also reduced.
         * If topic has overview and it is mandatory (e.g. topic itself is exported) or its overview has children,
         * overview is converted to regular topic
         *
         * @return true if item should be reduced (it has lower number of children than threshold and it is not
         * mandatory), false otherwise
         */
        boolean reduce(int threshold) {
            if ((overview != null) && (mandatory || !overview.children.isEmpty())) {
                children.add(overview);
                overview = null;
            }
            for (var child : new ArrayList<SetMember>(children)) { // we will manipulate with content and HashSet iterator doesn't like it
                if (child.reduce(threshold)) {
                    children.remove(child);
                    children.addAll(child.children);
                }
            }
            return (!mandatory) && (overview == null) && (children.size() < threshold);
        }

        WikiSetObjectBase getWikiSetObject() {
            var setChildren = children.stream().sorted(Comparator.comparing(SetMember::getObject))
                    .map(SetMember::getWikiSetObject)
                    .collect(Collectors.toList());
            if (mandatory) {
                return new WikiSetTopic(object, setChildren);
            } else if (overview != null) {
                return new WikiSetTitleWithOverview(object.getShortTitle(), overview.getObject(), setChildren);
            } else {
                return new WikiSetTitle(object.getShortTitle(), setChildren);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SetMember)) return false;
            SetMember setMember = (SetMember) o;
            return getObject().equals(setMember.getObject());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getObject());
        }

        @Override
        public String toString() {
            return "SetMember{" +
                    "object=" + object +
                    ", mandatory=" + mandatory +
                    ", overview=" + overview +
                    '}';
        }
    }

    private static class SetBuilder {

        private static final Logger LOG = LogManager.getLogger(SetBuilder.class);

        private final int threshold;
        private final Map<EaObjectRef, SetMember> memberByEaObject = new HashMap<>();

        SetBuilder(int threshold) {
            this.threshold = threshold;
        }

        /**
         * Add object to builder collection
         *
         * @param object is object to be added or modified
         * @param mandatory if set, marks object as mandatory
         * @param child is child to be added to given member; no child added when null
         */
        void addObject(EaObjectRef object, boolean mandatory, @Nullable SetMember child) {
            var member = memberByEaObject.computeIfAbsent(object, obj -> new SetMember(obj, mandatory));
            if (mandatory) {
                member.mandatory = true;
            }
            if (child != null) {
                if (child.getObject().getAlias().filter(alias -> alias.startsWith("overview_")).isPresent()
                        && child.children.isEmpty()) {
                    var oldOverview = member.getOverview();
                    if (oldOverview == null) {
                        member.setOverview(child);
                    } else {
                        if (!oldOverview.equals(child)) {
                            LOG.warn("Duplicate overview for topic {}: {} and {}; second overview ignored", member,
                                    oldOverview, child);
                            member.children.add(child);
                        }
                    }
                } else {
                    member.children.add(child);
                }
            }
            object.getParent().ifPresent(parent -> addObject(parent, false, member));
        }

        /**
         * Build goes through root members, reduces and exports them
         *
         * @return list of reduced root level members
         */
        List<SetMember> build() {
            var result = new ArrayList<SetMember>(1);
            for (var member : memberByEaObject.values()) {
                if (member.object.getParent().isEmpty()) {
                    // we return root item ... and silently expect, that there is only one
                    member.reduce(threshold);
                    result.add(member);
                }
            }
            return result;
        }
    }
}
