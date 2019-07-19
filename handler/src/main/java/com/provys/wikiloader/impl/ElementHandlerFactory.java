package com.provys.wikiloader.impl;

import org.sparx.Element;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface ElementHandlerFactory {
    @Nonnull
    Optional<ElementHandler> getElementHandler(Element element, LinkResolver linkResolver);
}
