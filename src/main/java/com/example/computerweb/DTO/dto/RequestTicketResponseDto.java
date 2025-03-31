package com.example.computerweb.DTO.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RequestTicketResponseDto {
    private String requestTicketId;
    private String typeRequest;
    private String dateSent;
    private String userSent;
    private String statusCSVC;
    private String statusGVU;


}
