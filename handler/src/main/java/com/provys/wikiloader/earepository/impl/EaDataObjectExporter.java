package com.provys.wikiloader.earepository.impl;

import com.provys.catalogue.api.Attr;
import com.provys.catalogue.api.AttrGrp;
import com.provys.catalogue.api.Entity;
import com.provys.provyswiki.ProvysWikiClient;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public class EaDataObjectExporter extends EaObjectRegularExporter<EaDataObject> {

    EaDataObjectExporter(EaDataObject eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    @Override
    void appendTitle() {
        startBuilder.append("====== ")
                .append(getEaObject().getEntity().map(Entity::getName).orElseGet(
                    () -> getEaObject().getName()))
                .append(" (")
                .append(getEaObject().getEntity().map(Entity::getNameNm)
                        .orElseGet(() -> getEaObject().getAlias().orElseThrow()
                            .toUpperCase(Locale.ENGLISH)))
                .append(") ======\n");
    }

    @Override
    void appendAlias() {
        // Alias is included in title, thus this method does nothing
    }

    private void appendSubDomain(String domainNm, String subdomainNm) {
        startBuilder.append("(").append(subdomainNm).append(")");
    }

    private void appendDomain(Attr attr) {
        final var domainNm = attr.getDomain().getNameNm();
        startBuilder.append(domainNm);
        attr.getSubdomainNm().ifPresent(subdomainNm -> appendSubDomain(domainNm, subdomainNm));
    }

    private void appendAttr(Attr attr) {
        startBuilder.append("|**").append(attr.getName()).append("**  |''")
                .append(attr.getNameNm()).append("''  |  ");
        appendDomain(attr);
        startBuilder.append("  |\n|");
        attr.getNote().ifPresent(startBuilder::append);
        startBuilder.append("  |||\n");
    }

    private void appendAttrHeader() {
        startBuilder.append("^ Name  ^ Int. Name  ^  Domain  ^\n");
    }

    private void appendAttrs(Collection<Attr> attrs) {
        for (var attr : attrs) {
            appendAttr(attr);
        }
    }

    private void appendAttrGrp(AttrGrp attrGrp) {
        startBuilder.append("==== ").append(attrGrp.getName()).append(" (").append(attrGrp.getNameNm()).append(") ====\n");
        appendAttrHeader();
        attrGrp.getAttrs().stream()
                .filter(attr -> !attr.isIsCustom())
                .sorted()
                .forEach(this::appendAttr);
    }

    @Override
    void appendDocument() {
        if (getEaObject().getEntity().isEmpty()) {
            appendNotes();
        } else {
            var entity = getEaObject().getEntity().orElseThrow(); // should not throw... tested just above
            if (!entity.isObjectClass()) {
                startBuilder.append("Abstract type\n");
            }
            startBuilder.append("Belongs to group: [[");
            getEaObject().getParent().ifPresent(parent -> parent.appendLink(startBuilder));
            startBuilder.append("]]\n");
            entity.getNote().ifPresent(note -> {
                startBuilder.append("===== Notes =====\n");
                startBuilder.append(note);
            });
            startBuilder.append("===== Code =====\n");
            entity.getTableNm().ifPresent(table -> startBuilder.append("Base table: ''").append(table).append("''\\\\\n"));
            entity.getViewNm().ifPresent(view -> startBuilder.append("Base view: ''").append(view).append("''\\\\\n"));
            entity.getPgPackageNm().ifPresent(pgPackage -> startBuilder.append("PG Package: ''").append(pgPackage)
                    .append("''\\\\\n"));
            entity.getCpPackageNm().ifPresent(cpPackage -> startBuilder.append("CP Package: ''").append(cpPackage)
                    .append("''\\\\\n"));
            entity.getEpPackageNm().ifPresent(epPackage -> startBuilder.append("EP Package: ''").append(epPackage)
                    .append("''\\\\\n"));
            entity.getFpPackageNm().ifPresent(fpPackage -> startBuilder.append("FP Package: ''").append(fpPackage)
                    .append("''\\\\\n"));
            startBuilder.append("===== Attributes =====\n");
            entity.getAttrGrps().stream().sorted().forEach(this::appendAttrGrp);
            startBuilder.append("==== Others ====\n");
            // and append attributes not attached to any attribute group
            var attrs = entity.getAttrs().stream()
                    .filter(attr -> !attr.isIsCustom())
                    .filter(attr -> attr.getAttrGrpId().isEmpty())
                    .sorted()
                    .collect(Collectors.toList());
            if (!attrs.isEmpty()) {
                appendAttrHeader();
                appendAttrs(attrs);
            }
        }
    }
}
