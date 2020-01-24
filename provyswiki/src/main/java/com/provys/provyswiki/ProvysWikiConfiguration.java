package com.provys.provyswiki;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

@ConfigurationProperties(prefix = "provyswiki")
class ProvysWikiConfiguration {

    @Nonnull
    private final String url;
    @Nonnull
    private final String user;
    @Nonnull
    private final String pwd;

    @ConstructorBinding
    ProvysWikiConfiguration(String url, String user, String pwd) {
        if (Objects.requireNonNull(url, "Property provyswiki.url not specified").isBlank()) {
            throw new IllegalArgumentException("Property provyswiki.url cannot be blank");
        }
        this.url = url;
        if (Objects.requireNonNull(user, "Property provyswiki.user not specified").isBlank()) {
            throw new IllegalArgumentException("Property provyswiki.user cannot be blank");
        }
        this.user = user;
        if (Objects.requireNonNull(pwd, "Property provyswiki.pwd not specified").isBlank()) {
            throw new IllegalArgumentException("Property provyswiki.pwd cannot be blank");
        }
        this.pwd = pwd;
    }

    @Nonnull
    public String getUrl() {
        return url;
    }

    @Nonnull
    public String getUser() {
        return user;
    }

    @Nonnull
    public String getPwd() {
        return pwd;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (!(o instanceof ProvysWikiConfiguration)) return false;
        ProvysWikiConfiguration that = (ProvysWikiConfiguration) o;
        return getUrl().equals(that.getUrl()) &&
                getUser().equals(that.getUser()) &&
                getPwd().equals(that.getPwd());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(), getUser(), getPwd());
    }

    @Override
    public String toString() {
        return "ProvysWikiConfiguration{" +
                "url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}
