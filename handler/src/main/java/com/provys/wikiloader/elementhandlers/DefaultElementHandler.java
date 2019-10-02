package com.provys.wikiloader.elementhandlers;

import com.provys.wikiloader.impl.ElementHandlerFactory;
import com.provys.wikiloader.wikimap.WikiElement;
import com.provys.wikiloader.wikimap.WikiMap;
import org.sparx.Element;

class DefaultElementHandler extends ElementHandlerBase {

    DefaultElementHandler(Element element, WikiElement info, ElementHandlerFactory elementHandlerFactory,
                          WikiMap wikiMap) {
        super(element, info, elementHandlerFactory, wikiMap);
    }

    @Override
    void appendDocument(StringBuilder builder) {
        appendTitle(builder);
        appendAlias(builder);
        appendNotes(builder);
    }
}
