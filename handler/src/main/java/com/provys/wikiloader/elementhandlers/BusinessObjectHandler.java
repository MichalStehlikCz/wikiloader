package com.provys.wikiloader.elementhandlers;

import com.provys.wikiloader.impl.LinkResolver;
import org.sparx.Element;

import javax.annotation.Nonnull;

class BusinessObjectHandler extends ElementHandlerBase {

    BusinessObjectHandler(Element element, String id) {
        super(element, id);
    }

    @Nonnull
    @Override
    String getDocument(LinkResolver linkResolver) {
        var builder = new StringBuilder();
        appendTitle(builder);
        appendAlias(builder);
        appendNotes(builder);
        return builder.toString();
    }
}
