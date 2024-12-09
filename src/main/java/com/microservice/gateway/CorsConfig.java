//package com.microservice.gateway;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsWebFilter;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.config.annotation.EnableWebSocket;
//import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
//import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//import org.springframework.web.socket.WebSocketHandler;
//
//@Configuration
//@EnableWebSocket // Enable WebSocket support
//public class CorsConfig implements WebMvcConfigurer, WebSocketConfigurer {
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:3000", "ws://trackflow:8001","http://trackflow:8001") // specify allowed origins
//                .allowedMethods("GET", "POST", "PUT", "DELETE")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(webSocketHandler(), "/ws") // register WebSocket endpoint
//                .setAllowedOrigins("http://localhost:3000"); // specify allowed WebSocket origins
//    }
//
//    @Bean
//    public WebSocketHandler webSocketHandler() {
//        return new TextWebSocketHandler() {  // A simple WebSocket handler
//            @Override
//            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//                session.sendMessage(new TextMessage("Received: " + message.getPayload()));
//            }
//        };
//    }
//}
