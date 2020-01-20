package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObject;

import javax.annotation.Nonnull;
import java.util.List;

public interface EaUGTopic extends EaObject, EaUGTopicRef {

    @Nonnull
    List<EaTechnicalPackageRef> getIncludedIn();
}
