package com.javaLearn.websocketapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatMessage {
    private String sender;
    private String content;
    private String messageType;
}
