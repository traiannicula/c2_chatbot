package mil.llm.ws;

import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.smallrye.mutiny.Multi;
import mil.llm.agents.Chatbot;


@WebSocket(path = "/chatbot")
public class ChatEndpoint {

    private final Chatbot agent;

    public ChatEndpoint(Chatbot agent) {
        this.agent = agent;
    }
    
    @OnOpen
    public String onOpen() {
        return "Hello, I'm you C2 Analyst, how can I help you?";
    }

    @OnTextMessage
    public Multi<String> onMessage(String message) {
        return agent.chat(message);
    }
}
