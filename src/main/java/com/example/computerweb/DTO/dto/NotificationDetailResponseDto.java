package com.example.computerweb.DTO.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationDetailResponseDto {

    private String requestTicketId;

    private String dateNotification;
    private String nameNotification;
    private String contentNotification;
    private String userSent;
    private String department;


    private  String status;
}
