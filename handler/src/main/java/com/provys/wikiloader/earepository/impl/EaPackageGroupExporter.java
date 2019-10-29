package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.handlers.PackageGroupExporter;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class EaPackageGroupExporter extends EaParentExporter<EaPackageGroup> {

    EaPackageGroupExporter(EaPackageGroup eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    /**
     * Append header for elements section
     */
    void appendElementsHeader() {
        startBuilder.append("\n===== Packages =====\n");
    }

    private static class SubPackageExporter {

        @Nonnull
        private final StringBuilder builder = new StringBuilder();
        private int lines = 4; // empty box has height roughly corresponding to 4 lines of text

        SubPackageExporter(EaPackageGroupRef pkg) {
            appendSubPackage(pkg);
        }

        @SuppressWarnings("squid:S3457")
        private void appendElementToContent(EaProductPackageRef element, int level) {
            builder.append(String.format("%" + (level * 2 + 10) + "s", "* [["));
            element.appendLink(builder);
            builder.append("]]\n");
            lines++;
        }

        @SuppressWarnings("squid:S3457")
        private void appendPackageToContent(EaPackageGroupRef pkg, int level) {
            builder.append(String.format("%" + (level * 2 + 10) + "s", "* [["));
            pkg.appendLink(builder);
            builder.append("]]\n");
            lines++;
            for (var subElement : pkg.getElements()) {
                if (!(subElement instanceof HandlerInt)) {
                    throw new InternalException(LOG, "Sub-element is surprisingly not HandlerInt");
                }
                appendElementToContent((HandlerInt) subElement, level + 1);
            }
            for (var subPackage : pkg.getSubPackages()) {
                if (!(subPackage instanceof HandlerInt)) {
                    throw new InternalException(LOG, "Sub-package is surprisingly not HandlerInt");
                }
                appendPackageToContent((HandlerInt) subPackage, level + 1);
            }
        }

        private void appendSubPackage(EaPackageGroupRef pkg) {
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
                if (subPackage instanceof EaPackageGroupRef) {
                    panels.add(new SubPackageExporter((EaPackageGroupRef) subPackage));
                    contentBuilder.add(subPackage.getParentLink().orElseThrow());
                    subPackage.appendPages(pages);
                } else {
                    appendElement(subPackage);
                }
            }
        }
        if (panels.size() <= 1) {
            // one or zero panels -> no need for columns
            for (var panel : panels) {
                startBuilder.append(panel.export());
            }
        } else {
            // build two columns, target height is half of summary height
            int height = panels.stream().mapToInt(PackageGroupExporter.SubPackageExporter::getLines).sum() / 2;
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
