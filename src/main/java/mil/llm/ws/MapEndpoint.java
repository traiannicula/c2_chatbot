package mil.llm.ws;

import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;

@WebSocket(path = "/map")
public class MapEndpoint {

    @OnTextMessage(broadcast = true)
    public String onMessage(String message) {
        return message;
    }
}
