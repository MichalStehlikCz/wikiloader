package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

class EaMeaningItemExporter extends EaUGTopicExporter<EaMeaningItemRef, EaMeaningItem> {
    EaMeaningItemExporter(EaMeaningItem eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }

    @Override
    public String toString() {
        return "EaMeaningItemExporter{} " + super.toString();
    }
}
