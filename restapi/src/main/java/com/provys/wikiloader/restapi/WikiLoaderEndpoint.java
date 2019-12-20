package com.provys.wikiloader.restapi;

import com.provys.wikiloader.WikiLoader;
import io.swagger.v3.oas.annotations.Operation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/wikiloader")
@ApplicationScoped
public class WikiLoaderEndpoint {

    @Inject
    WikiLoader wikiLoader;

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
            summary = "Synchronise Whole Wiki",
            description = "Read whole Enterprise Architect model and apply it on provys wiki")
    public Response syncAll() {
        wikiLoader.run(null, true);
        return Response.ok("Synchronisation successfull").build();
    }

    @GET
    @Path("/tree/{path}")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
            summary = "Synchronise Tree",
            description = "Read Enterprise Architect section starting at given namespace and synchronise sub-tree")
    public Response syncTree(@PathParam("path") String path) {
        wikiLoader.run(path, true);
        return Response.ok("Synchronisation successfull").build();
    }

    @GET
    @Path("/topic/{path}")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
            summary = "Synchronise Single Topic",
            description = "Read Enterprise Architect package / element / diagram and synchronise it to wiki")
    public Response syncTopic(@PathParam("path") String path) {
        wikiLoader.run(path, false);
        return Response.ok("Synchronisation successfull").build();
    }
}
