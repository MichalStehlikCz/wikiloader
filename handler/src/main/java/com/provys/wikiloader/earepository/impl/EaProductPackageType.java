package com.provys.wikiloader.earepository.impl;

enum EaProductPackageType {
    UNIVERSAL(true, true),
    TECHNICAL(true, false),
    SALES(false, true);

    private final boolean technical;
    private final boolean sales;

    EaProductPackageType(boolean technical, boolean sales) {
        this.technical = technical;
        this.sales = sales;
    }

    public boolean isTechnical() {
        return technical;
    }

    public boolean isSales() {
        return sales;
    }
}
