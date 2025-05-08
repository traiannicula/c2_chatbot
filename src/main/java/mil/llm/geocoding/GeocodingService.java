package mil.llm.geocoding;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestQuery;

import dev.langchain4j.agent.tool.Tool;
import io.quarkus.cache.CacheResult;
import io.quarkus.rest.client.reactive.ClientQueryParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@RegisterRestClient(configKey = "geocoding")
@RegisterProvider(GeocodingRequestFilter.class)
public interface GeocodingService {

    @GET
    @Path("/searchJSON")
    @CacheResult(cacheName = "geonames")
    @ClientQueryParam(name = "fuzzy", value = "0.6")
    @ClientQueryParam(name = "maxRows", value = "10")
    @ClientQueryParam(name = "username", value = "traian_nicula")
    @ClientQueryParam(name = "east", value = "0.0")
    @ClientQueryParam(name = "west", value = "0.0")
    @ClientQueryParam(name = "north", value = "0.0")
    @ClientQueryParam(name = "south", value = "0.0")
    @Tool("Finds the latitude, longitude and the name of a given city. Query name can be misspelled")
    GeoNames search(
        @RestQuery("q") String query
    );
}
