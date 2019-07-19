package com.provys.wikiloader.elementhandlers;

import com.provys.catalogue.api.Attr;
import com.provys.catalogue.api.AttrGrp;
import com.provys.catalogue.api.CatalogueRepository;
import com.provys.catalogue.api.Entity;
import com.provys.wikiloader.impl.LinkResolver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Collectors;

class DataObjectHandler extends DefaultElementHandler {

    private static final Logger LOG = LogManager.getLogger(DataObjectHandler.class);

    @Nullable
    private final Entity entity;

    /**
     * Create and return handler for data object. Log warning if entity is not found based on supplied alias.
     *
     * @param element is element we create handler for
     * @param id is id of wiki page where element will be described
     * @param catalogueRepository is Provys Catalogue repository used to retrieve information about entity and its
     *                           attributes
     */
    DataObjectHandler(Element element, String id, CatalogueRepository catalogueRepository) {
        super(element, id);
        this.entity = catalogueRepository.getEntityManager().getByNameNmIfExists(element.GetAlias()).orElse(null);
        if (this.entity == null) {
            LOG.warn("Entity for data object {} not found", id);
        }
    }

    private void appendDomain(StringBuilder builder, Attr attr, LinkResolver linkResolver) {
        var domainNm = attr.getDomain();
        builder.append(domainNm);
        if (domainNm.equals("UID")) {

        }
    }

    private void appendAttr(StringBuilder builder, Attr attr, LinkResolver linkResolver) {
        builder.append("|**").append(attr.getName()).append("**  |''")
                .append(attr.getNameNm()).append("''  |  ");
        appendDomain(builder, attr, linkResolver);
        builder.append("  |\n|");
        attr.getNote().map(linkResolver::formatProvysNote).ifPresent(builder::append);
        builder.append("  |||");
    }

    private void appendAttrHeader(StringBuilder builder) {
        builder.append("^ Name  ^ Int. Name  ^  Domain  ^");
    }

    private void appendAttrs(StringBuilder builder, Collection<Attr> attrs, LinkResolver linkResolver) {
        for (var attr : attrs) {
            appendAttr(builder, attr, linkResolver);
        }
    }

    private void appendAttrGrp(StringBuilder builder, AttrGrp attrGrp, LinkResolver linkResolver) {
        builder.append("===== ").append(attrGrp.getName()).append(" (").append(attrGrp.getNameNm()).append(") =====\n");
        appendAttrHeader(builder);
        attrGrp.getAttrs().stream()
                .filter(attr -> attr.getAttrGrpId().isEmpty())
                .sorted()
                .forEach(attr -> appendAttr(builder, attr, linkResolver));
    }

    @Override
    @Nonnull
    String getDocument(LinkResolver linkResolver) {
        var builder = new StringBuilder();
        if (entity == null) {
            appendTitle(builder);
            appendAlias(builder);
            appendNotes(builder);
        } else {
            builder.append("====== ").append(entity.getName()).append(" (").append(entity.getNameNm())
                    .append(") ======\n");
            if (!entity.isObjectClass()) {
                builder.append("Astract type\n");
            }
            builder.append("Belongs to group: [[").append(linkResolver.getPackageLink(getElement().GetPackageID()))
                    .append("]]\n");
            entity.getNote().ifPresent(note -> {
                builder.append("===== Notes =====\n");
                builder.append(linkResolver.formatProvysNote(note));
            });
            builder.append("===== Code =====\n");
            entity.getTable().ifPresent(table -> builder.append("Base table: ''").append(table).append("''\n"));
            entity.getView().ifPresent(view -> builder.append("Base view: ''").append(view).append("''\n"));
            entity.getPgPackage().ifPresent(pgPackage -> builder.append("PG Package: ''").append(pgPackage)
                    .append("''\n"));
            entity.getCpPackage().ifPresent(cpPackage -> builder.append("CP Package: ''").append(cpPackage)
                    .append("''\n"));
            entity.getEpPackage().ifPresent(epPackage -> builder.append("EP Package: ''").append(epPackage)
                    .append("''\n"));
            entity.getFpPackage().ifPresent(fpPackage -> builder.append("FP Package: ''").append(fpPackage)
                    .append("''\n"));
            builder.append("===== Attributes =====\n");
            entity.getAttrGrps().stream().sorted().forEach(attrGrp -> appendAttrGrp(builder, attrGrp, linkResolver));
            // and append attributes not attached to any attribute group
            var attrs = entity.getAttrs().stream()
                    .filter(attr -> attr.getAttrGrpId().isEmpty())
                    .sorted()
                    .collect(Collectors.toList());
            if (!attrs.isEmpty()) {
                builder.append("\\\\\n");
                appendAttrHeader(builder);
                appendAttrs(builder, attrs, linkResolver);
            }
        }
        return builder.toString();
    }
}
