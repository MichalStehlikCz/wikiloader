package com.provys.wikiloader.earepository.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@ConfigurationProperties(prefix = "ea")
class EaLoaderConfiguration {
    @Nonnull
    private final String address;

    @ConstructorBinding
    EaLoaderConfiguration(String address) {
        if (Objects.requireNonNull(address, "Property ea.address not specified").isBlank()) {
            throw new IllegalArgumentException("Property ea.address cannot be blank");
        }
        this.address = address;
    }

    @Nonnull
    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof EaLoaderConfiguration)) return false;
        EaLoaderConfiguration that = (EaLoaderConfiguration) o;
        return getAddress().equals(that.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddress());
    }

    @Override
    public String toString() {
        return "EaLoaderConfiguration{" +
                "address='" + address + '\'' +
                '}';
    }
}
