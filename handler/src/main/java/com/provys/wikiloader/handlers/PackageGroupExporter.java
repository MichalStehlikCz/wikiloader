package com.provys.wikiloader.handlers;

import com.provys.common.exception.InternalException;
import com.provys.provyswiki.ProvysWikiClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;

class PackageGroupExporter extends DefaultExporter<DefaultPackageHandler> {

    private static final Logger LOG = LogManager.getLogger(PackageGroupExporter.class);

    PackageGroupExporter(DefaultPackageHandler handler, ProvysWikiClient wikiClient) {
        super(handler, wikiClient);
    }

    private static class SubPackageExporter {

        @Nonnull
        private final StringBuilder builder = new StringBuilder();
        private int lines = 4; // empty box has height roughly corresponding to 4 lines of text

        SubPackageExporter(HandlerInt pkg) {
            appendSubPackage(pkg);
        }

        @SuppressWarnings("squid:S3457")
        private void appendElementToContent(HandlerInt element, int level) {
            builder.append(String.format("%" + (level * 2 + 10) + "s", "* [[")).append(element.getId()).append("]]\n");
            lines++;
        }

        @SuppressWarnings("squid:S3457")
        private void appendPackageToContent(HandlerInt pkg, int level) {
            builder.append(String.format("%" + (level * 2 + 10) + "s", "* [[")).append(pkg.getId()).append("]]\n");
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

        private void appendSubPackage(HandlerInt pkg) {
            builder.append("<panel type=\"default\" title=\"")
                    .append(pkg.getEaName().replace("&", "And")).append("\">\n");
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

    private void appendSubPackages() {
        // we need to find height to split panels to two columns...
        final var panels = new ArrayList<SubPackageExporter>(3);
        for (var subPackage : getSubPackages()) {
            if (subPackage instanceof HandlerInt) {
                panels.add(new SubPackageExporter((HandlerInt) subPackage));
                contentBuilder.add(subPackage.getRelLink());
                subPackage.appendPages(pages);
            } else {
                appendElement(subPackage);
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
        // handle elements
        var elements = getElements();
        if (!elements.isEmpty()) {
            startBuilder.append("\n==== Packages ====\n");
            if (!contentBuilder.isEmpty()) {
                contentBuilder.add("\\\\");
            }
            for (var element : elements) {
                appendElement(element);
            }
        }
        // handle sub-packages
        appendSubPackages();
    }
}
