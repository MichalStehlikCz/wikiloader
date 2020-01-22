package com.provys.wikiloader.cli;

import com.provys.common.exception.RegularException;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.WikiLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ApplicationScoped
public class RunWikiLoader implements Runnable {

    @Nonnull
    private static final Logger LOG = LogManager.getLogger(RunWikiLoader.class.getName());
    @Nonnull
    private final WikiLoader wikiLoader;
    @Nullable
    private String wikiUrl;
    @Nullable
    private String wikiUser;
    @Nullable
    private String wikiPwd;
    @Nullable
    private String provysAddress;
    @Nullable
    private String provysUser;
    @Nullable
    private String provysPwd;
    @Nullable
    private String eaAddress;
    @Nullable
    private String model;
    @Nullable
    private String path;
    private boolean recursive = true;

    @SuppressWarnings("CdiUnproxyableBeanTypesInspection")
    @Inject
    RunWikiLoader(WikiLoader wikiLoader) {
        this.wikiLoader = Objects.requireNonNull(wikiLoader);
    }

    @Nonnull
    RunWikiLoader setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
        return this;
    }

    @Nonnull
    RunWikiLoader setWikiUser(String wikiUser) {
        this.wikiUser = wikiUser;
        return this;
    }

    @Nonnull
    RunWikiLoader setWikiPwd(String wikiPwd) {
        this.wikiPwd = wikiPwd;
        return this;
    }

    @Nonnull
    RunWikiLoader setProvysAddress(String provysAddress) {
        this.provysAddress = provysAddress;
        return this;
    }

    @Nonnull
    RunWikiLoader setProvysUser(String provysUser) {
        this.provysUser = provysUser;
        return this;
    }

    @Nonnull
    RunWikiLoader setProvysPwd(String provysPwd) {
        this.provysPwd = provysPwd;
        return this;
    }

    @Nonnull
    RunWikiLoader setEaAddress(String eaAddress) {
        this.eaAddress = eaAddress;
        return this;
    }

    @Nonnull
    RunWikiLoader setModel(@Nullable String model) {
        this.model = model;
        return this;
    }

    @Nonnull
    RunWikiLoader setPath(@Nullable String path) {
        this.path = path;
        return this;
    }

    @Nonnull
    RunWikiLoader setRecursive(boolean recursive) {
        this.recursive = recursive;
        return this;
    }

    @Override
    public void run() {
        if (provysAddress == null) {
            throw new IllegalStateException("Provys database address is not specified");
        }
        if (provysUser == null) {
            throw new IllegalStateException("Provys database user is not specified");
        }
        if (provysPwd == null) {
            throw new IllegalStateException("Provys database password is not specified");
        }
        if (eaAddress == null) {
            throw new IllegalStateException("Enterprise Architect repository address is not specified");
        }
        if (wikiUrl == null) {
            throw new RegularException(LOG, "JAVA_WIKILOADER_WIKIURLMISSING",
                    "Cannot connect to wiki - URL not specified");
        }
        if (wikiUser == null) {
            throw new RegularException(LOG, "JAVA_WIKILOADER_WIKIUSERMISSING",
                    "Cannot connect to wiki - user not specified");
        }
        if (wikiPwd == null) {
            throw new RegularException(LOG, "JAVA_WIKILOADER_WIKIPWDMISSING",
                    "Cannot connect to wiki - password not specified");
        }
        var config = ConfigProviderResolver.instance().getConfig();
        if (config != null) {
            ConfigProviderResolver.instance().releaseConfig(config);
        }
        config = ConfigProviderResolver.instance()
                .getBuilder()
                .addDefaultSources()
                .withSources(new CommandLineParamsSource(provysAddress, provysUser, provysPwd, eaAddress, wikiUrl,
                        wikiUser, wikiPwd))
                .addDiscoveredConverters()
                .build();
        ConfigProviderResolver.instance().registerConfig(config, getClass().getClassLoader());
        wikiLoader.run(model, path, recursive, false);
        LOG.info("Synchronisation of Enterprise Architect models to wiki finished");
    }

    public static class CommandLineParamsSource implements ConfigSource {

        private final Map<String, String> properties = new HashMap<>(3);

        CommandLineParamsSource(String provysUrl, String provysUser, String provysPwd, String eaAddress, String wikiUrl,
                                String wikiUser, String wikiPwd) {
            properties.put("PROVYSDB_URL", provysUrl);
            properties.put("PROVYSDB_USER", provysUser);
            properties.put("PROVYSDB_PWD", provysPwd);
            properties.put("EA_ADDRESS", eaAddress);
            properties.put("PROVYSWIKI_URL", wikiUrl);
            properties.put("PROVYSWIKI_USER", wikiUser);
            properties.put("PROVYSWIKI_PWD", wikiPwd);
        }

        @Override
        public int getOrdinal() {
            return 900;
        }

        @Override
        public Map<String, String> getProperties() {
            return properties;
        }

        @Override
        public String getValue(String key) {
            return properties.get(key);
        }

        @Override
        public String getName() {
            return "CommandLineParams";
        }
    }
}
