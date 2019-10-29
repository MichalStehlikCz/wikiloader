package com.provys.wikiloader.earepository.impl;

import com.provys.common.exception.InternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Objects;

class EaPackageGroupRef extends EaDefaultPackageRef {

    private static final Logger LOG = LogManager.getLogger(EaPackageGroupRef.class);

    private final boolean sales;
    private final boolean technical;

    EaPackageGroupRef(@Nullable EaObjectRefBase parent, String name, @Nullable String alias,
                      @Nullable String stereotype, int treePos, int packageId, boolean sales, boolean technical) {
        super(parent, name, alias, stereotype, treePos, packageId);
        this.sales = sales;
        this.technical = technical;
    }

    boolean isSales() {
        return sales;
    }

    boolean isTechnical() {
        return technical;
    }

    void appendSalesLink(StringBuilder builder) {
        if (!hasLink()) {
            throw new InternalException(LOG, "Cannot append sales link - element not exported to wiki");
        }
        if (!sales) {
            throw new InternalException(LOG, "Cannot append sales link - not sales package");
        }
        builder.append(":");
        getParent().orElseThrow().appendNamespace(builder);
        getAlias().ifPresent(builder::append);
        builder.append(":sales");
    }

    void appendTechnicalLink(StringBuilder builder) {
        if (!hasLink()) {
            throw new InternalException(LOG, "Cannot append technical link - element not exported to wiki");
        }
        if (!technical) {
            throw new InternalException(LOG, "Cannot append technical link - not technical package");
        }
        builder.append(":");
        getParent().orElseThrow().appendNamespace(builder);
        getAlias().ifPresent(builder::append);
        builder.append(":technical");
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EaPackageGroupRef that = (EaPackageGroupRef) o;
        return sales == that.sales &&
                technical == that.technical;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sales, technical);
    }

    @Override
    public String toString() {
        return "EaPackageGroupRef{" +
                "sales=" + sales +
                ", technical=" + technical +
                "} " + super.toString();
    }
}
