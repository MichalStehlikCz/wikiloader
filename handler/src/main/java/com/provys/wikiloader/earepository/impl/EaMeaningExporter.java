package com.provys.wikiloader.earepository.impl;

import com.provys.provyswiki.ProvysWikiClient;

class EaMeaningExporter extends EaUGTopicExporter<EaMeaningRef, EaMeaning> {
    EaMeaningExporter(EaMeaning eaObject, ProvysWikiClient wikiClient) {
        super(eaObject, wikiClient);
    }
}
