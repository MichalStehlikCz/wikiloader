package com.provys.wikiloader.restapi;

import com.provys.wikiloader.WikiLoader;
import io.swagger.annotations.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;

@RestController
@RequestMapping(value = "/wikiloader", produces = MediaType.TEXT_PLAIN_VALUE)
public class WikiLoaderController {

    private static final Logger LOG = LogManager.getLogger(WikiLoaderController.class);

    @Nonnull
    private final WikiLoader wikiLoader;

    @Autowired
    public WikiLoaderController(WikiLoader wikiLoader) {
        this.wikiLoader = wikiLoader;
    }

    @ApiOperation(value = "Full Synchronization", notes = "Read whole Enterprise Architect model and apply it on provys wiki")
    @GetMapping("/syncall")
    public String syncAll() {
        LOG.info("WikiLoader SyncAll");
        wikiLoader.run(null, null, true, true);
        LOG.info("WikiLoader SyncAll finished");
        return "Synchronisation successful";
    }

    @ApiOperation(value = "Partial Synchronization", notes = "Synchronise defined part of tree on wiki")
    @GetMapping("/sync")
    public String sync(@RequestParam(name = "model", defaultValue = "eamodel")
                           @ApiParam(value = "Model (eamodel, company_model)",
                                   allowableValues = "eamodel, company_model") String model,
                       @RequestParam(name = "path", required = false)
                       @ApiParam(value = "Path to given topic on wiki, excluding model, excluding :start in case of" +
                               " namespace") String path,
                       @RequestParam(name = "recursive", defaultValue = "true")
                           @ApiParam("If set to false, only single topic is synchronised") boolean recursive) {
        LOG.info("WikiLoader Sync {}, {}, {}", model, path, recursive);
        wikiLoader.run(model, path, recursive, true);
        LOG.info("WikiLoader Sync finished");
        return "Synchronisation successful";
    }
}
