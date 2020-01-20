package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

class EaMeaningExporter extends EaUGTopicExporter<EaMeaningItemRef, EaMeaningItem> {
    EaMeaningExporter(EaMeaningItem eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }
}
