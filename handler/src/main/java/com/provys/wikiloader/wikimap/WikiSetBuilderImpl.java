package com.provys.wikiloader.wikimap;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaElement;
import com.provys.wikiloader.earepository.EaObject;
import com.provys.wikiloader.earepository.EaPackage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

class WikiSetBuilderImpl implements WikiSetBuilder {

    private static final Logger LOG = LogManager.getLogger(WikiSetBuilderImpl.class);

    @Nonnull
    private final WikiMap wikiMap;
    private int threshold = 5;
    private final Set<EaObject> objects = new HashSet<>();

    WikiSetBuilderImpl(WikiMap wikiMap) {
        this.wikiMap = Objects.requireNonNull(wikiMap);
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
        objects.add(wikiMap.getEaRepository().getPackageById(packageId));
        return this;
    }

    @Nonnull
    @Override
    public WikiSetBuilder addElementById(int elementId) {
        objects.add(wikiMap.getEaRepository().getElementById(elementId));
        return this;
    }

    @Nonnull
    @Override
    public WikiSetBuilder addEaObjects(Collection<? extends EaObject> newObjects) {
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
                .map(member -> member.getWikiSetObject(wikiMap))
                .flatMap(member -> member.getChildren().stream())
                .collect(Collectors.toList());
    }

    /**
     * Class builds WikiSetObject; it relies on SetBuilder to ensure that there is only single instance of SetMember for
     * given EaObject - it is because of that that we can use Set of SetMembers even though class doesn't override
     * object's equals method
     */
    private static class SetMember {
        @Nonnull
        EaObject object;
        boolean mandatory;
        @Nonnull
        Set<SetMember> children = new HashSet<>();

        SetMember(EaObject object, boolean mandatory) {
            this.object = object;
            this.mandatory = mandatory;
        }

        @Nonnull
        private EaObject getObject() {
            return object;
        }

        /**
         * Reduce children of this member if appropriate and indicates if this member should be also reduced
         *
         * @return true if item should be reduced (it has lower number of children than threshold and it is not
         * mandatory), false otherwise
         */
        boolean reduce(int threshold) {
            for (var child : new ArrayList<SetMember>(children)) { // we will manipulate with content and HashSet iterator doesn't like it
                if (child.reduce(threshold)) {
                    children.remove(child);
                    children.addAll(child.children);
                }
            }
            return (!mandatory) && (children.size() < threshold);
        }

        WikiSetObject getWikiSetObject(WikiMap wikiMap) {
            if (object instanceof EaElement) {
                var eaElement = (EaElement) object;
                return new WikiSetObjectImpl(eaElement.getName(),
                        wikiMap.getElementLink(eaElement.getElementId()).orElse(null),
                        children.stream().sorted(Comparator.comparing(SetMember::getObject))
                                .map(child -> child.getWikiSetObject(wikiMap))
                                .collect(Collectors.toList()));
            } else if (object instanceof EaPackage) {
                var eaPackage = (EaPackage) object;
                return new WikiSetObjectImpl(eaPackage.getName(),
                        wikiMap.getPackageLink(eaPackage.getPackageId()).orElse(null),
                        children.stream().sorted(Comparator.comparing(SetMember::getObject))
                                .map(child -> child.getWikiSetObject(wikiMap))
                                .collect(Collectors.toList()));
            }
            throw new InternalException(LOG, "Unsupported EaObject type " + object.getClass());
        }
    }

    private static class SetBuilder {

        private final int threshold;
        private final Map<EaObject, SetMember> memberByEaObject = new HashMap<>();

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
        void addObject(EaObject object, boolean mandatory, @Nullable SetMember child) {
            var member = memberByEaObject.computeIfAbsent(object, obj -> new SetMember(obj, mandatory));
            if (mandatory) {
                member.mandatory = true;
            }
            if (child != null) {
                member.children.add(child);
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
