package intellijP.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration WebSocket pour la communication temps reel.
 * j'Utilise STOMP comme protocole de messagerie.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Active le broker de messages en memoire
        // Les clients s'abonnent aux topics /topic/* pour recevoir les messages
        config.enableSimpleBroker("/topic");
        // Prefixe pour les messages envoyes par les clients
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Point d'entree WebSocket - les clients se connectent ici
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*", "http://172.31.253.157:*")
                .withSockJS(); // Fallback pour navigateurs sans support WebSocket
    }
}
