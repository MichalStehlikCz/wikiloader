package com.provys.wikiloader.earepository;

import com.provys.common.exception.RegularException;

import javax.annotation.Nonnull;

/**
 * Defines Enterprise Architect models and their mapping to wiki
 */
public enum EaModel {

    PRODUCT_MODEL("Product Model", "eamodel"),
    COMPANY_MODEL("Company Model", "companymodel"),
    MYPROVYS_MODEL("myProvys Model", "myprovysmodel");

    /**
     * Retrieve model with given wiki namespace
     *
     * @param wikiNamespace is wiki namespace we are looking up
     * @return model with given wiki namespace
     */
    public static EaModel getByWikiNamespace(String wikiNamespace) {
        for (var model : values()) {
            if (model.wikiNamespace.equals(wikiNamespace)) {
                return model;
            }
        }
        throw new RegularException("WIKILOADER_MODEL_NOT_FOUND", "Model was not found by wiki name " + wikiNamespace);
    }

    @Nonnull
    private final String name;
    @Nonnull
    private final String wikiNamespace;

    EaModel(String name, String wikiNamespace) {
        this.name = name;
        this.wikiNamespace = wikiNamespace;
    }

    /**
     * @return model name as used in Enterprise Architect root element
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * @return namespace model is mapped to in wiki
     */
    @Nonnull
    public String getWikiNamespace() {
        return wikiNamespace;
    }
}
