package com.provys.wikiloader;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import picocli.CommandLine;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import java.io.File;

/**
 * Application class, used to gather inputs from command line, build CDI container and pass parameters to runner inside
 * container
 */
@CommandLine.Command(description = "Load Enterprise Architect models to Provys wiki", name="wikiloader",
        mixinStandardHelpOptions = true, version = "1.0")
public class WikiLoaderCliApplication implements Runnable {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new WikiLoaderCliApplication())
                .registerConverter(Level.class, Level::valueOf)
                .execute(args);
        System.exit(exitCode);
    }

    @CommandLine.Option(names = {"--wikiurl"},
            description = "Provys wikipedia Xml-Rpc endpoint url (http://provys-wiki.dcit.cz/lib/exe/xmlrpc.php)",
            defaultValue = "http://provys-wiki.dcit.cz/lib/exe/xmlrpc.php")
    private String wikiUrl;

    @CommandLine.Option(names = {"--wikiuser"}, description = "Provys wikipedia user", defaultValue = "stehlik")
    private String wikiUser;

    @CommandLine.Option(names = {"--wikipwd"}, description = "Provys wikipedia user password", defaultValue = "stehlik")
    private String wikiPwd;

    @CommandLine.Option(names = {"-p", "--provysdb"},
            description = "Provys database connect string (localhost:60002:PVYS)", defaultValue = "localhost:60002:PVYS")
    private String provysAddress;

    @CommandLine.Option(names = {"--provysuser"}, description = "Provys DB user", defaultValue = "ealoader")
    private String provysUser;

    @CommandLine.Option(names = {"--provyspwd"}, description = "Provys DB user password", defaultValue = "heslo")
    private String provysPwd;

    @CommandLine.Option(names = {"-e", "--eaproject"}, description = "Enterprise architect project",
            defaultValue = "provys_ea --- DBType=3;Connect=Provider=OraOLEDB.Oracle.1;Password=ker;" +
                    "Persist Security Info=True;User ID=ker;Data Source=enterprise_architect;LazyLoad=1;")
    private String eaAddress;

    @CommandLine.Option(names = {"-l", "--logfile"}, description = "Log file")
    private File logFile;

    @CommandLine.Option(names = {"--loglevel"}, description = "Log level", defaultValue = "ERROR")
    private Level logLevel;

    /**
     * Configure logger based on command line arguments (logfile and loglevel)
     */
    private void configureLogger() {
        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setConfigurationName("RootLogger");
        AppenderComponentBuilder appenderBuilder;
        if (logFile != null) {
            appenderBuilder = builder.newAppender("Log", "File").
                    addAttribute("fileName", logFile.getPath());
        } else {
            appenderBuilder = builder.newAppender("Log", "Console")
                    .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
        }
        appenderBuilder.add(builder.newLayout("PatternLayout").
                addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level %-10t %-50c{-2} %msg%n%throwable"));
        builder.add(appenderBuilder);
        builder.add(builder.newRootLogger(logLevel).
                add(builder.newAppenderRef("Log")).
                addAttribute("additivity", true));
        builder.add(builder.newLogger("org.jboss.weld", Level.INFO).
                addAttribute("additivity", true));
        Configurator.initialize(builder.build());
        final Logger logger = LogManager.getLogger(WikiLoaderCliApplication.class);
        logger.info("LoggerInit: Logger initialized: " +
                ((logFile != null) ? "file " + logFile.getPath() : "console") + ", level " + logLevel);
    }

    /**
     * Open connection to PROVYS database, open Enterprise Architect instance and pass them both to loader
     */
    @Override
    public void run() {
        configureLogger();
        SeContainer container = SeContainerInitializer.newInstance()
                .addProperty("org.jboss.weld.se.archive.isolation", false).initialize();
        RunWikiLoader runner = container.select(RunWikiLoader.class).get();
        runner.setWikiUrl(wikiUrl)
                .setWikiUser(wikiUser)
                .setWikiPwd(wikiPwd)
                .setProvysAddress(provysAddress)
                .setProvysUser(provysUser)
                .setProvysPwd(provysPwd)
                .setEaAddress(eaAddress)
                .run();
    }
}
