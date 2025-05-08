package mil.llm.geocoding;

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GeocodingRequestFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        UriBuilder uriBuilder = UriBuilder.fromUri(requestContext.getUri());
        uriBuilder.replaceQueryParam("east", Util.east)
                  .replaceQueryParam("west", Util.west)
                  .replaceQueryParam("north", Util.north)
                  .replaceQueryParam("south", Util.south);
        requestContext.setUri(uriBuilder.build());
    }
    
}
