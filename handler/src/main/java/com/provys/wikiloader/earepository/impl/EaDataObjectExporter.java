package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

public class EaDataObjectExporter extends EaObjectRegularExporter<EaDataObject> {

    EaDataObjectExporter(EaDataObject eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }
}
