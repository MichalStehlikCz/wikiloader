package com.provys.wikiloader.handlers;

import com.provys.common.exception.InternalException;
import com.provys.wikiloader.earepository.EaElement;
import com.provys.wikiloader.impl.HandlerFactory;
import com.provys.wikiloader.wikimap.WikiElement;
import com.provys.wikiloader.wikimap.WikiMap;
import com.provys.wikiloader.wikimap.WikiSetObject;
import org.sparx.Element;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

class ProductPackageHandler extends ElementHandlerBase<ProductPackageHandler> {

    ProductPackageHandler(Element element, WikiElement info, HandlerFactory handlerFactory, WikiMap wikiMap) {
        super(ProductPackageExporter::new, element, info, handlerFactory, wikiMap);
        if (element.GetAlias().isEmpty()) {
            throw new InternalException(LOG, "Cannot process element with empty alias");
        }
    }

    private List<EaElement> getFunctions() {
        var result = new ArrayList<EaElement>(10);
        var connectors = getElement().GetConnectors();
        for (var connector : connectors) {
            if (connector.GetStereotype().equals("ArchiMate_Association")) {
                var element = getWikiMap().getEaRepository().getElementById(connector.GetSupplierID());
                if (element.getStereotype().filter(s -> s.equals("ArchiMate_BusinessService")).isPresent()) {
                    result.add(element);
                }
            }
            connector.destroy();
        }
        connectors.destroy();
        return result;
    }

    List<WikiSetObject> getFunctionContent() {
        return getWikiMap().getSetBuilder().setThreshold(3).addEaObjects(getFunctions()).build();
    }

    @Nonnull
    @Override
    ProductPackageHandler self() {
        return this;
    }
}
