package com.yondu.knowledgebase.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yondu.knowledgebase.DTO.notification.NotificationDTO;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.services.UserService;
import io.jsonwebtoken.Jwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private TokenUtil tokenUtil;
    @Autowired
    private UserService userService;
    private ObjectMapper objectMapper = new ObjectMapper();
    private record WebSocketUser(long userId, WebSocketSession session){}
    private List<WebSocketUser> sessions = new ArrayList<>();
    private Logger log = LoggerFactory.getLogger(WebSocketHandler.class);

    public WebSocketHandler() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        SimpleModule simpleModule = new SimpleModule();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));

        objectMapper.registerModule(javaTimeModule);
        objectMapper.registerModule(simpleModule);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        log.info("New websocket established: " + session.getId());

        String token = extractTokenFromUrl(session.getUri());

        Jwt jwt = tokenUtil.readJwt(token);
        String email = (String) jwt.getHeader().get("email");

        User user = userService.loadUserByUsername(email);
        sessions.add(new WebSocketUser(user.getId(), session));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);

        log.info("Received message from " + session.getId() + ": " + message.getPayload());
    }

    private String extractTokenFromUrl(URI uri) {
        log.info("WebSocketHandler.extractTokenFromUrl()");
        log.info("uri : " + uri);

        UriComponents uriComponents = UriComponentsBuilder.fromUri(uri).build();
        String token = uriComponents.getQueryParams().getFirst("token");

        return token != null ? token : "";
    }

    public void sendMessageToClient(long userId, NotificationDTO.BaseResponse notification) throws IOException {
        log.info("WebSocketHandler.sendMessageToClient()");
        log.info("userId : " + userId);
        log.info("notification : " + notification);

        for(WebSocketUser user : sessions) {
            if(user.userId() == userId) {
                if(user.session().isOpen()){
                    String data = objectMapper.writeValueAsString(notification);
                    user.session().sendMessage(new TextMessage(data));
                }
            }
        }
    }
}
