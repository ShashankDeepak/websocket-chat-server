package com.javaLearn.websocketapp.config;

import com.javaLearn.websocketapp.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
//disconnect event listener
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messageTemplate; //Use to tell the user when the user is disconnected
    @EventListener
    public void handleWebSocketEventListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if(username != null) {
            log.info("User Disconnected");
            var chatMessage = ChatMessage
                    .builder()
                    .messageType("LEAVE")
                    .sender(username)
                    .build();

            messageTemplate.convertAndSend("/topic/public",chatMessage);
        }
    }
}
