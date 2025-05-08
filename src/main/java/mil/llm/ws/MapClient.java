package mil.llm.ws;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.websockets.next.BasicWebSocketConnector;
import io.quarkus.websockets.next.BasicWebSocketConnector.ExecutionModel;
import io.quarkus.websockets.next.WebSocketClientConnection;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import mil.llm.geocoding.Util;

@Singleton
public class MapClient {
    URI uri = URI.create("ws://localhost:8080/map");

    @Inject
    BasicWebSocketConnector connector;

    private WebSocketClientConnection connection;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void connect(@Observes StartupEvent ev) {
        scheduler.schedule(this::establishConnection, 5, TimeUnit.SECONDS);
    }

    private void establishConnection() {
        connection = connector
                .baseUri(uri)
                .executionModel(ExecutionModel.NON_BLOCKING)
                .onTextMessage((c, m) -> {
                    System.out.println("Received message: " + m);
                    updateUtilValues(m);
                })
                .connectAndAwait();

    }

    private void updateUtilValues(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            if (jsonNode.get("type").asText().equals("extent")) {
                Util.west = jsonNode.get("west").asDouble();
                Util.south = jsonNode.get("south").asDouble();
                Util.east = jsonNode.get("east").asDouble();
                Util.north = jsonNode.get("north").asDouble();
                System.out.println("Updated Util values: west=" + Util.west + ", south=" + Util.south + ", east="
                        + Util.east + ", north=" + Util.north);
            }
        } catch (Exception e) {
            System.err.println("Failed to update Util values: " + e.getMessage());
        }
    }

    public void send(String message) {
        if (connection != null) {
            connection.sendTextAndAwait(message);
        } else {
            System.err.println("Connection is not established. Call connect() first.");
        }
    }

    public void onShutdown(@Observes ShutdownEvent ev) {
        scheduler.shutdown();
        if (connection != null) {
            connection.close();
        }
    }

}
