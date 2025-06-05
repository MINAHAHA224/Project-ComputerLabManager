package com.example.computerweb.models.enums;

import lombok.Getter;

import java.util.Map;
import java.util.TreeMap;

@Getter
public enum StatusEnum {
    PENDING_APPROVAL(1, "PENDING_APPROVAL", "Chờ duyệt"),
    APPROVED(2, "APPROVED", "Đã duyệt"),
    REJECTED(3, "REJECTED", "Từ chối"),
    NOTSEEN(4, "NOTSEEN", "Chưa xem"),
    SEEN(5, "SEEN", "Đã xem"),
    ACTIVE(6, "ACTIVE", "Hoạt động"),
    OFF(9, "OFF", "Trạng thái nghỉ"),
    COMPLETED(10, "COMPLETED", "Đã hoàn tất"),
    NOT_REQUIRED(11, "NOT_REQUIRED", "Không yêu cầu duyệt"),
    WAITING_DEAN_APPROVAL(12, "WAITING_DEAN_APPROVAL", "Chờ Trưởng Khoa duyệt"),
    WAITING_REGISTRAR_PROCESSING(13, "WAITING_REGISTRAR_PROCESSING", "Chờ Giáo Vụ xử lý"),
    WAITING_FACILITIES_APPROVAL(14, "WAITING_FACILITIES_APPROVAL", "Chờ CSVC duyệt"),
    CANCELLED_BY_USER(15, "CANCELLED_BY_USER", "Đã hủy bởi người tạo"),
    PROCESSED_SUCCESSFULLY(16, "PROCESSED_SUCCESSFULLY", "Đã xử lý thành công (GVU/CSVC)"),
    PROCESSING_FAILED(17, "PROCESSING_FAILED", "Xử lý thất bại (GVU/CSVC)"),
    AGREE(0, "AGREE", "Đồng ý (GVU/CSVC/TK)"),
    NOT_AGREE(0, "NOT_AGREE", "Không đồng ý (GVU/CSVC/TK)");

    private final int id;
    private final String code;
    private final String description;

    StatusEnum(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }

    public static Map<String, String> getTrangThaiMap() {
        Map<String, String> map = new TreeMap<>();
        for (StatusEnum status : StatusEnum.values()) {
            map.put(String.valueOf(status.getId()), status.getDescription());
        }
        return map;
    }
}
