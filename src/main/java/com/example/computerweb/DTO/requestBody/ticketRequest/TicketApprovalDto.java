package com.example.computerweb.DTO.requestBody.ticketRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor
public class TicketApprovalDto {
    @NotNull
    private Long ticketId; // YeuCauID của PhieuYeuCau
    // private Long approverUserId; // Sẽ lấy từ SecurityContext
    @NotBlank
    private String approvalStatus; // Ví dụ: "APPROVED", "REJECTED"
    private String approverNote; // Ghi chú của người duyệt
}
