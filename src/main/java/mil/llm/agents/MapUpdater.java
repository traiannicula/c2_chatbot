package mil.llm.agents;

import dev.langchain4j.agent.tool.Tool;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import mil.llm.ws.MapClient;

@Singleton
public class MapUpdater {
    @Inject
    MapClient mapClient;

    @Tool("Updates the map with the geocoding information in JSON format")
    public void updatePoint(String json) {
        mapClient.send(json);
    }
}
