package com.yondu.knowledgebase.DTO.email;

import java.time.LocalDateTime;

public class EmailResponseDTO {
    private LocalDateTime timeStamp;
    private String sender;
    private String receiver;

    public EmailResponseDTO() {
    }

    public EmailResponseDTO(LocalDateTime timeStamp, String sender, String receiver) {
        this.timeStamp = timeStamp;
        this.sender = sender;
        this.receiver = receiver;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
