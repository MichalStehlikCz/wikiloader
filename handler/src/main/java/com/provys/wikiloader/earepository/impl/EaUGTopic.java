package com.provys.wikiloader.earepository.impl;

import com.provys.wikiloader.earepository.EaObject;

import javax.annotation.Nonnull;
import java.util.List;

interface EaUGTopic extends EaObject, EaUGTopicRef {

    /**
     * Return list of technical packages topic is included in
     *
     * @return list of technical packages topic is included in
     */
    @Nonnull
    List<EaTechnicalPackageRef> getIncludedIn();

    /**
     * Return list of reports this topic uses
     *
     * @return list of reports this topic uses
     */
    @Nonnull
    List<EaReportRef> getReports();

    /**
     * Return list of interfaces this topic uses
     *
     * @return list of interfaces this topic uses
     */
    @Nonnull
    List<EaInterfaceRef> getInterfaces();
}
