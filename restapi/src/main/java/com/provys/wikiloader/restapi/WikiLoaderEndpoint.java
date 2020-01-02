package com.provys.wikiloader.restapi;

import com.provys.wikiloader.WikiLoader;
import io.swagger.v3.oas.annotations.Operation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/wikiloader")
@ApplicationScoped
public class WikiLoaderEndpoint {

    @Inject
    WikiLoader wikiLoader;

    @GET
    @Path("/sync")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
            summary = "Synchronise Wiki",
            description = "Read Enterprise Architect model and apply it on provys wiki")
    public String sync(@QueryParam("path") @DefaultValue("") String path,
                            @QueryParam("recursive") @DefaultValue("true") boolean recursive) {
        wikiLoader.run(path, recursive);
        return "Synchronisation successfull";
    }
}
