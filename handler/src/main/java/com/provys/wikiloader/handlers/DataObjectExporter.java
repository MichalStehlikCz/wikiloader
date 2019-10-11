package com.provys.wikiloader.handlers;

import com.provys.catalogue.api.Attr;
import com.provys.catalogue.api.AttrGrp;
import com.provys.catalogue.api.CatalogueRepository;
import com.provys.catalogue.api.Entity;
import com.provys.provyswiki.ProvysWikiClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Collectors;

class DataObjectExporter extends DefaultExporter<DefaultElementHandler> {

    private static final Logger LOG = LogManager.getLogger(DataObjectExporter.class);

    @Nullable
    private final Entity entity;

    /**
     * Create and return handler for data object. Log warning if entity is not found based on supplied alias.
     *
     * @param catalogueRepository is Provys Catalogue repository used to retrieve information about entity and its
     *                           attributes
     */
    DataObjectExporter(DefaultElementHandler handler, ProvysWikiClient wikiClient,
                       CatalogueRepository catalogueRepository) {
        super(handler, wikiClient);
        this.entity = catalogueRepository.getEntityManager().getByNameNmIfExists(handler.getEaAlias())
                .orElse(null);
        if (this.entity == null) {
            LOG.warn("Entity for data object {} not found", handler::getEaAlias);
        }
    }

    @Override
    void appendTitle() {
        startBuilder.append("====== ")
                .append((entity == null) ? getHandler().getEaName() : entity.getName())
                .append(" (")
                .append((entity == null) ? getHandler().getEaAlias().toUpperCase() : entity.getNameNm())
                .append(") ======\n");
    }

    @Override
    void appendAlias() {
        // Alias is included in title, thus this method does nothing
    }

    private void appendDomain(Attr attr) {
        var domainNm = attr.getDomain();
        startBuilder.append(domainNm);

        // missing code...


    }

    private void appendAttr(Attr attr) {
        startBuilder.append("|**").append(attr.getName()).append("**  |''")
                .append(attr.getNameNm()).append("''  |  ");
        appendDomain(attr);
        startBuilder.append("  |\n|");
        attr.getNote().map(getHandler().getWikiMap()::formatProvysNote).ifPresent(startBuilder::append);
        startBuilder.append("  |||");
    }

    private void appendAttrHeader() {
        startBuilder.append("^ Name  ^ Int. Name  ^  Domain  ^");
    }

    private void appendAttrs(Collection<Attr> attrs) {
        for (var attr : attrs) {
            appendAttr(attr);
        }
    }

    private void appendAttrGrp(AttrGrp attrGrp) {
        startBuilder.append("===== ").append(attrGrp.getName()).append(" (").append(attrGrp.getNameNm()).append(") =====\n");
        appendAttrHeader();
        attrGrp.getAttrs().stream()
                .filter(attr -> attr.getAttrGrpId().isEmpty())
                .sorted()
                .forEach(this::appendAttr);
    }

    @Override
    void appendDocument() {
        if (entity == null) {
            appendNotes();
        } else {
            var wikiMap = getHandler().getWikiMap();
            if (!entity.isObjectClass()) {
                startBuilder.append("Abstract type\n");
            }
            startBuilder.append("Belongs to group: [[").append(wikiMap.getPackageLink(
                    getHandler().getElement().GetPackageID()))
                    .append("]]\n");
            entity.getNote().ifPresent(note -> {
                startBuilder.append("===== Notes =====\n");
                startBuilder.append(wikiMap.formatProvysNote(note));
            });
            startBuilder.append("===== Code =====\n");
            entity.getTable().ifPresent(table -> startBuilder.append("Base table: ''").append(table).append("''\n"));
            entity.getView().ifPresent(view -> startBuilder.append("Base view: ''").append(view).append("''\n"));
            entity.getPgPackage().ifPresent(pgPackage -> startBuilder.append("PG Package: ''").append(pgPackage)
                    .append("''\n"));
            entity.getCpPackage().ifPresent(cpPackage -> startBuilder.append("CP Package: ''").append(cpPackage)
                    .append("''\n"));
            entity.getEpPackage().ifPresent(epPackage -> startBuilder.append("EP Package: ''").append(epPackage)
                    .append("''\n"));
            entity.getFpPackage().ifPresent(fpPackage -> startBuilder.append("FP Package: ''").append(fpPackage)
                    .append("''\n"));
            startBuilder.append("===== Attributes =====\n");
            entity.getAttrGrps().stream().sorted().forEach(this::appendAttrGrp);
            // and append attributes not attached to any attribute group
            var attrs = entity.getAttrs().stream()
                    .filter(attr -> attr.getAttrGrpId().isEmpty())
                    .sorted()
                    .collect(Collectors.toList());
            if (!attrs.isEmpty()) {
                startBuilder.append("\\\\\n");
                appendAttrHeader();
                appendAttrs(attrs);
            }
        }
    }
}
