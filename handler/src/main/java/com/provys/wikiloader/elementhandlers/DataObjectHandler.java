package com.provys.wikiloader.elementhandlers;

import com.provys.catalogue.api.Attr;
import com.provys.catalogue.api.AttrGrp;
import com.provys.catalogue.api.CatalogueRepository;
import com.provys.catalogue.api.Entity;
import com.provys.wikiloader.impl.ElementHandlerFactory;
import com.provys.wikiloader.wikimap.WikiElement;
import com.provys.wikiloader.wikimap.WikiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sparx.Element;

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
     * @param info is infromation about element mapping to wiki
     * @param catalogueRepository is Provys Catalogue repository used to retrieve information about entity and its
     *                           attributes
     */
    DataObjectHandler(Element element, WikiElement info, ElementHandlerFactory elementHandlerFactory,
                      WikiMap wikiMap, CatalogueRepository catalogueRepository) {
        super(element, info, elementHandlerFactory, wikiMap);
        this.entity = catalogueRepository.getEntityManager().getByNameNmIfExists(element.GetAlias()).orElse(null);
        if (this.entity == null) {
            LOG.warn("Entity for data object {} not found", element::GetAlias);
        }
    }

    @Override
    void appendTitle(StringBuilder builder) {
        builder.append("====== ")
                .append((entity == null) ? getElement().GetName() : entity.getName())
                .append(" (")
                .append((entity == null) ? getElement().GetAlias() : entity.getNameNm())
                .append(") ======\n");
    }

    @Override
    void appendAlias(StringBuilder builder) {
        // Alias is included in title, thus this method does nothing
    }

    private void appendDomain(StringBuilder builder, Attr attr) {
        var domainNm = attr.getDomain();
        builder.append(domainNm);

        // missing code...


    }

    private void appendAttr(StringBuilder builder, Attr attr) {
        builder.append("|**").append(attr.getName()).append("**  |''")
                .append(attr.getNameNm()).append("''  |  ");
        appendDomain(builder, attr);
        builder.append("  |\n|");
        attr.getNote().map(getWikiMap()::formatProvysNote).ifPresent(builder::append);
        builder.append("  |||");
    }

    private void appendAttrHeader(StringBuilder builder) {
        builder.append("^ Name  ^ Int. Name  ^  Domain  ^");
    }

    private void appendAttrs(StringBuilder builder, Collection<Attr> attrs) {
        for (var attr : attrs) {
            appendAttr(builder, attr);
        }
    }

    private void appendAttrGrp(StringBuilder builder, AttrGrp attrGrp) {
        builder.append("===== ").append(attrGrp.getName()).append(" (").append(attrGrp.getNameNm()).append(") =====\n");
        appendAttrHeader(builder);
        attrGrp.getAttrs().stream()
                .filter(attr -> attr.getAttrGrpId().isEmpty())
                .sorted()
                .forEach(attr -> appendAttr(builder, attr));
    }

    @Override
    void appendDocument(StringBuilder builder) {
        if (entity == null) {
            appendNotes(builder);
        } else {
            if (!entity.isObjectClass()) {
                builder.append("Astract type\n");
            }
            builder.append("Belongs to group: [[").append(getWikiMap().getPackageLink(getElement().GetPackageID()))
                    .append("]]\n");
            entity.getNote().ifPresent(note -> {
                builder.append("===== Notes =====\n");
                builder.append(getWikiMap().formatProvysNote(note));
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
            entity.getAttrGrps().stream().sorted().forEach(attrGrp -> appendAttrGrp(builder, attrGrp));
            // and append attributes not attached to any attribute group
            var attrs = entity.getAttrs().stream()
                    .filter(attr -> attr.getAttrGrpId().isEmpty())
                    .sorted()
                    .collect(Collectors.toList());
            if (!attrs.isEmpty()) {
                builder.append("\\\\\n");
                appendAttrHeader(builder);
                appendAttrs(builder, attrs);
            }
        }
    }
}
