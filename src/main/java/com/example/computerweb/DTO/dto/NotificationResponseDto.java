package com.example.computerweb.DTO.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationResponseDto {

    private String id;

    private String nameNotification;

    private String contentNotification;

    private String dateNotification;

    private  String status;
}
