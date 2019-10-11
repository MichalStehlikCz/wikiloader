package com.provys.wikiloader.impl;

import com.provys.wikiloader.wikimap.WikiMap;
import org.sparx.Element;
import org.sparx.Package;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public interface HandlerFactory {
    @Nonnull
    Optional<Handler> getElementHandler(Element element, WikiMap wikiMap);

    @Nonnull
    Collection<Handler> getElementHandlers(org.sparx.Collection<Element> elements, WikiMap wikiMap);

    @Nonnull
    Optional<Handler> getPackageHandler(Package pkg, WikiMap wikiMap);

    @Nonnull
    Collection<Handler> getPackageHandlers(org.sparx.Collection<Package> pkgs, WikiMap wikiMap);
}
