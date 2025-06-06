package com.example.computerweb.DTO.dto.notificationResponse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NotificationResponseDto {

    private String id;

    private String nameNotification;

    private String dateNotification;

    private  String status;
}
