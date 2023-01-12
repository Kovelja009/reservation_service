package com.komponente.reservation_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDto {
    private String recipient;
    private String subject;
    private String text;
    private String firstName;
    private String lastName;
    private String company;
    private String username;
    private String model;
    private String link;

    public NotificationDto(){

    }

    public NotificationDto(String recipient, String subject, String text, String firstName, String lastName, String company, String username, String model, String link) {
        this.recipient = recipient;
        this.subject = subject;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.username = username;
        this.model = model;
        this.link = link;
        this.text = text;
    }



}
