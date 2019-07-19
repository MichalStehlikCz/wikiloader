package com.provys.wikiloader;

import com.provys.common.exception.RegularException;
import com.provys.provyswiki.ProvysWikiClient;
import com.provys.wikiloader.impl.WikiLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.sparx.Repository;

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
    private ProvysWikiClient getWikiClient() {
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
        return new ProvysWikiClient(wikiUrl, wikiUser, wikiPwd);
    }

    @Override
    public void run() {
        ConfigProviderResolver.instance().registerConfig(
                ConfigProviderResolver.instance().getBuilder().forClassLoader(getClass().getClassLoader()).
                        withSources(new CommandLineParamsSource(provysAddress, provysUser, provysPwd)).build(),
                getClass().getClassLoader());
        Repository eaRepository = null;
        try {
            // Create a repository object - This will create a new instance of EA
            eaRepository = new Repository();
            // Attempt to open the provided file
            if (eaRepository.OpenFile(eaAddress)) {
                wikiLoader.run(eaRepository, getWikiClient());
            } else {
                // If the file couldn't be opened then notify the user
                throw new RegularException(LOG, "EALOADER_CANNOTOPENREPOSITORY",
                        "EA was unable to open the file '" + eaAddress + '\'');
            }
        } finally {
            if (eaRepository != null) {
                // Clean up
                eaRepository.CloseFile();
                eaRepository.Exit();
                eaRepository.destroy();
            }
        }
        LOG.info("Synchronisation of Enterprise Architect models to wiki finished");
    }

    public static class CommandLineParamsSource implements ConfigSource {

        private final Map<String, String> properties = new HashMap<>(3);

        CommandLineParamsSource(String url, String user, String pwd) {
            properties.put("PROVYSDB_URL", url);
            properties.put("PROVYSDB_USER", user);
            properties.put("PROVYSDB_PWD", pwd);
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
