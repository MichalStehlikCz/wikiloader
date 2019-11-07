package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.earepository.EaElementRef;

import javax.annotation.Nonnull;
import java.util.ArrayList;

class EaItemGroupExporter<E extends EaElementRef, R extends EaItemGroupRef<E, R, G>,
        G extends EaItemGroup<E, R, G>> extends EaParentExporter<G> {

    EaItemGroupExporter(G eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    /**
     * Append header for elements section
     */
    @Override
    void appendElementsHeader() {
        startBuilder.append("\n===== Packages =====\n");
    }

    private static class SubPackageExporter<E extends EaElementRef, R extends EaItemGroupRef<E, R, G>,
            G extends EaItemGroup<E, R, G>> {

        @Nonnull
        private final StringBuilder builder = new StringBuilder();
        private int lines = 4; // empty box has height roughly corresponding to 4 lines of text

        SubPackageExporter(R pkg) {
            appendSubPackage(pkg);
        }

        @SuppressWarnings("squid:S3457")
        private void appendElementToContent(E element, int level) {
            builder.append(String.format("%" + (level * 2 + 10) + "s", "* [["));
            element.appendLink(builder);
            builder.append("]]\n");
            lines++;
        }

        @SuppressWarnings("squid:S3457")
        private void appendPackageToContent(R pkgRef, int level) {
            builder.append(String.format("%" + (level * 2 + 10) + "s", "* [["));
            pkgRef.appendLink(builder);
            builder.append("]]\n");
            lines++;
            var pkg = pkgRef.getObject();
            for (var subElement : pkg.getElements()) {
                appendElementToContent(subElement, level + 1);
            }
            for (var subPackage : pkg.getPackages()) {
                appendPackageToContent(subPackage, level + 1);
            }
        }

        private void appendSubPackage(R pkg) {
            builder.append("<panel type=\"default\" title=\"")
                    .append(pkg.getName().replace("&", "And")).append("\">\n");
            appendPackageToContent(pkg, 0);
            builder.append("</panel>");
        }

        @Nonnull
        String export() {
            return builder.toString();
        }

        int getLines() {
            return lines;
        }
    }

    @Override
    void appendPackages() {
        // we need to find height to split panels to two columns...
        final var panels = new ArrayList<SubPackageExporter>(3);
        for (var subPackage : getEaObject().getPackages()) {
            if (subPackage.isTopic()) {
                panels.add(new SubPackageExporter<>(subPackage));
                contentBuilder.add(subPackage.getParentLink().orElseThrow());
                subPackage.appendPages(pages);
                subObjects.add(subPackage);
            }
        }
        if (panels.size() <= 1) {
            // one or zero panels -> no need for columns
            for (var panel : panels) {
                startBuilder.append(panel.export());
            }
        } else {
            // build two columns, target height is half of summary height
            int height = panels.stream().mapToInt(SubPackageExporter::getLines).sum() / 2;
            startBuilder.append("<grid>\n")
                    .append("<col sm=\"6\">\n");
            int currentHeight = 0;
            int pos = 0;
            // it looks better when first column is higher than second... but second cannot be empty
            while ((pos + 1 < panels.size()) && (currentHeight < height)) {
                startBuilder.append(panels.get(pos).export());
                currentHeight += panels.get(pos).getLines();
                pos++;
            }
            startBuilder.append("</col>\n").append("<col sm=\"6\">\n");
            for (; pos < panels.size(); pos++) {
                startBuilder.append(panels.get(pos).export());
            }
            startBuilder.append("</col>\n").append("</grid>\n");
        }
    }

    @Override
    void appendBody() {
        // handle diagrams
        appendDiagrams();
        // insert own content
        appendDocument();
        // handle elements - they go before sub-packages
        appendElements();
        // handle sub-packages
        appendPackages();
    }
}
