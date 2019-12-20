package com.provys.wikiloader.restapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api")
@OpenAPIDefinition(
        info = @Info(
                title = "Provys Catalogue repository Api",
                version = "1.0",
                description = "Provides methods for accessing entities and other objects from provys metadata catalogue"
        ),
        servers = {@Server(url = "/wikiloader/api")}) // needed because swagger does not read path from appplication...
public class WikiLoaderApplication extends Application {
}
