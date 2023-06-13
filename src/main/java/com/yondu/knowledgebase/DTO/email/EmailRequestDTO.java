package com.yondu.knowledgebase.DTO.email;

public class EmailRequestDTO {
    private String to;
    private String from;
    private String fromLink;
    private String notificationType;
    private String contentType;
    private String contentLink;

    public EmailRequestDTO() {
    }

    public EmailRequestDTO(String to, String from, String fromLink, String notificationType, String contentType, String contentLink) {
        this.to = to;
        this.from = from;
        this.fromLink = fromLink;
        this.notificationType = notificationType;
        this.contentType = contentType;
        this.contentLink = contentLink;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromLink() {
        return fromLink;
    }

    public void setFromLink(String fromLink) {
        this.fromLink = fromLink;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentLink() {
        return contentLink;
    }

    public void setContentLink(String contentLink) {
        this.contentLink = contentLink;
    }
}
