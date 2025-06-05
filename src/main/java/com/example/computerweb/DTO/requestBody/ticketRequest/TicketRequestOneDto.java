package com.example.computerweb.DTO.requestBody.ticketRequest;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TicketRequestOneDto {
    private String requestId;
    // private String typeRequestId; // Có thể giữ lại nếu FE cần ID
    private String typeRequestName; // Tên loại yêu cầu (ví dụ: "Thay đổi lịch", "Mượn phòng")
    private String dateRequest;
    private String userRequest; // Người tạo phiếu
    // private String status; // Mã trạng thái tổng thể của phiếu (ví dụ: "WAITING_DEAN_APPROVAL")
    private String statusName; // Nội dung trạng thái tổng thể của phiếu (ví dụ: "Chờ Trưởng Khoa duyệt")

    // Tùy chọn: Nếu muốn hiển thị trạng thái duyệt cụ thể của vai trò đang xem
    // private String deanApprovalStatusName;    // Trạng thái duyệt của Trưởng Khoa
    // private String registrarApprovalStatusName; // Trạng thái duyệt của Giáo Vụ
    // private String facilitiesApprovalStatusName; // Trạng thái duyệt của CSVC
}
