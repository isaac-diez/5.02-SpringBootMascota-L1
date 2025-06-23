package cat.itacademy.s05.t02.n01.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the HTTP endpoint that clients will connect to for the WebSocket handshake.
        // SockJS is a fallback for browsers that don't support WebSockets.
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173") // Your frontend URL
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // The message broker will route messages with these prefixes to clients.
        // "/topic" is for public broadcasts, "/user" is for private messages to a specific user.
        registry.enableSimpleBroker("/topic", "/user");
        // This defines the prefix for messages sent from clients to the server (if you add that functionality later).
        registry.setApplicationDestinationPrefixes("/app");
        // This enables user-specific destinations, like /user/{username}/queue/pet-updates
        registry.setUserDestinationPrefix("/user");
    }
}