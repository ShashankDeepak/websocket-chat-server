package com.javaLearn.websocketapp.chat;

import com.javaLearn.websocketapp.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Controller
@Slf4j
public class ChatController {
    Map<String,String> chatRooms = new TreeMap<>();
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @MessageMapping("/chatRooms")
    @SendTo("/list/allRooms")
    public Map<String,String> getListOfChatRooms() {
        return chatRooms;
    }
    @MessageMapping("/sendMessage/{roomname}")
    public void sendMessage(
            @Payload ChatMessage chatMessage,
            @DestinationVariable String roomname
    ) throws Exception {
        log.info("SendMessage was called with roomname = " + roomname + "with destination = " + "/topic/chat/" + roomname);
        if(chatRooms.containsKey(roomname)) {
            log.info(chatMessage.toString());
            log.info("Message sent");
            this.simpMessagingTemplate.convertAndSend("/topic/" + roomname,chatMessage);
        }
        else {
            throw new Exception("Chat Room " + roomname + " does not exist. Create a new chat room first");
        }
    }

    @MessageMapping("/createRoom/{roomname}")
    @SendTo("/list/createdRooms")
    public Map<String,String> createChatRoom(@DestinationVariable String roomname) throws Exception {
        log.info("/createRoom called");
        if(!chatRooms.containsKey(roomname)){
            chatRooms.put(roomname,"/topic/" + roomname);
            log.info("Chat room " + roomname + " created");

            Map<String,String> tempMap = new HashMap<>();
            tempMap.put(roomname,"/topic/" + roomname.replace(" ", ""));
            return tempMap;
        }
        else {
            throw new Exception("Room " + roomname + " already present");
        }
    }
    @MessageMapping("/chat/{roomname}")
    public ChatMessage chatInRoom(@Payload ChatMessage message, @DestinationVariable String roomname) throws Exception {
        log.info("/chat/ was called");
        if(message != null){
            log.info("/topic/" + roomname);
            simpMessagingTemplate.convertAndSend("/topic/" + roomname, message);
            return message;
        }
        throw new Exception("Empty message");
    }

    @MessageMapping("/addUser/{roomname}")
    @SendTo("/topic/{roomname}") //Will send the chatMessage to the queue, /topic/public
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            @DestinationVariable String roomname,
            SimpMessageHeaderAccessor headerAccessor
    ) throws Exception {

        //adds username is websocket session
        if(chatRooms.containsKey(roomname)) {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());

            return chatMessage;
        }
        else {
            throw new Exception("Room does not exist to add user. Create a room first");
        }
    }
}
