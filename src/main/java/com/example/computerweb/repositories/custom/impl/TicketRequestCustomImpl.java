package com.example.computerweb.repositories.custom.impl;

import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRequestOneDto;
import com.example.computerweb.models.entity.*;
import com.example.computerweb.models.enums.StatusEnum;
import com.example.computerweb.repositories.*;
import com.example.computerweb.repositories.custom.TicketRequestCustom;
import com.example.computerweb.utils.DateUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

@Repository
@RequiredArgsConstructor
public class TicketRequestCustomImpl implements TicketRequestCustom {
    private final IUserRepository iUserRepository;
    private final IStatusRepository iStatusRepository;

    @PersistenceContext
    private EntityManager entityManager;




//    @Override
//    public List<TicketRequestOneDto> findListTicketForRole(String role) {
//        StringBuilder sql = new StringBuilder();
//        if ( role.equals("GVU")){
//            sql.append("SELECT PhieuYeuCau.YeuCauID , PhieuYeuCau.LoaiYcID_FK , PhieuYeuCau.NgayGui , PhieuYeuCau.UserIdNguoiGui_FK , PhieuYeuCau.DuyetGVU\n" +
//                    "                FROM PhieuYeuCau\n" +
//                    "                WHERE PhieuYeuCau.DuyetCSVC =  2 OR PhieuYeuCau.DuyetTK = 2 OR PhieuYeuCau.DuyetGVU =1  OR  PhieuYeuCau.DuyetGVU =2 ");
//        }else if ( role.equals("CSVC")){
//            sql.append("SELECT PhieuYeuCau.YeuCauID , PhieuYeuCau.LoaiYcID_FK , PhieuYeuCau.NgayGui , PhieuYeuCau.UserIdNguoiGui_FK , PhieuYeuCau.DuyetCSVC\n" +
//                    "                FROM PhieuYeuCau\n" +
//                    "                WHERE PhieuYeuCau.LoaiYcID_FK != 2 ");
//        }else if ( role.equals("TK")){
//            sql.append("SELECT PhieuYeuCau.YeuCauID , PhieuYeuCau.LoaiYcID_FK , PhieuYeuCau.NgayGui , PhieuYeuCau.UserIdNguoiGui_FK , PhieuYeuCau.DuyetTK\n" +
//                    "                FROM PhieuYeuCau\n" +
//                    "                WHERE PhieuYeuCau.LoaiYcID_FK = 1 ");
//
//        }
//
//        Query query = entityManager.createNativeQuery(sql.toString());
//        List<Object[]> resultList = query.getResultList();
//        List<TicketRequestOneDto> ticketRequestOneDtoList = new ArrayList<>();
//        for ( Object[] result : resultList){
//            TicketRequestOneDto ticketRequestOneDto = new TicketRequestOneDto();
//            ticketRequestOneDto.setRequestId(result[0].toString());
//            ticketRequestOneDto.setTypeRequestId(result[1].toString());
//            ticketRequestOneDto.setDateRequest(DateUtils.dateTimeConvertToString((Timestamp)result[2]));
//            UserEntity user = this.iUserRepository.findUserEntityById(Long.valueOf(result[3].toString()));
//            ticketRequestOneDto.setUserRequest(user.getFirstName() + " " + user.getLastName());
//            StatusEntity statusCSVC = this.iStatusRepository.findStatusEntityById(Long.valueOf(result[4].toString()));
//            ticketRequestOneDto.setStatus(statusCSVC.getNameStatus());
//
//            ticketRequestOneDtoList.add(ticketRequestOneDto);
//        }
//        return ticketRequestOneDtoList;
//
//    }

    @Override
    public List<TicketRequestOneDto> findListTicketForRole(String roleMaQuyen) { // Giả sử role là MaQuyen
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT pyc.YeuCauID, lyc.MaLoaiYC, lyc.NDloaiYC, pyc.NgayGui, pyc.UserIdNguoiGui_FK, st_phieu.MaTrangThai, st_phieu.NDTrangThai ");
        // Thêm các cột trạng thái duyệt cụ thể nếu DTO cần
        // sql.append(", st_tk.NDTrangThai AS TrangThaiDuyetTK, st_gvu.NDTrangThai AS TrangThaiDuyetGVU, st_csvc.NDTrangThai AS TrangThaiDuyetCSVC ");
        sql.append("FROM PhieuYeuCau pyc ");
        sql.append("JOIN LoaiYC lyc ON pyc.LoaiYcID_FK = lyc.LoaiYcID ");
        sql.append("JOIN TrangThai st_phieu ON pyc.TrangThaiID_FK = st_phieu.TrangThaiID ");
        // JOIN thêm với TrangThai cho các cột Duyet... nếu cần hiển thị chi tiết trạng thái từng bước
        // sql.append("LEFT JOIN TrangThai st_tk ON pyc.DuyetTK = st_tk.TrangThaiID ");
        // sql.append("LEFT JOIN TrangThai st_gvu ON pyc.DuyetGVU = st_gvu.TrangThaiID ");
        // sql.append("LEFT JOIN TrangThai st_csvc ON pyc.DuyetCSVC = st_csvc.TrangThaiID ");

        // Lấy ID các trạng thái từ MaTrangThai
        // Bạn nên có cache hoặc lấy các StatusEntity này một lần ở Service thay vì query nhiều lần
        int statusWaitingDeanId = StatusEnum.WAITING_DEAN_APPROVAL.getId();
        int statusWaitingRegistrarId = StatusEnum.WAITING_REGISTRAR_PROCESSING.getId();
        int statusWaitingFacilitiesId = StatusEnum.WAITING_FACILITIES_APPROVAL.getId();
        int statusCompletedId = StatusEnum.COMPLETED.getId();
        // Thêm các trạng thái khác nếu muốn hiển thị (ví dụ: phiếu đã duyệt bởi vai trò này)


        if (roleMaQuyen.equals("GVU")) { // Giả sử "GVU" là MaQuyen
            sql.append("WHERE pyc.TrangThaiID_FK = :statusWaitingRegistrar ");
            // GVU có thể muốn xem cả phiếu đã xử lý hoặc từ chối bởi GVU
            // sql.append("OR (pyc.MODIFIED_GVU = :currentUserId AND pyc.TrangThaiID_FK IN (:statusCompleted, :statusOverallRejected)) ");
            // Hoặc đơn giản là các phiếu đang chờ họ và các phiếu họ đã xử lý gần đây
//            sql.append("OR (pyc.DuyetGVU IS NOT NULL )"); // Phiếu GVU đã chạm vào
        } else if (roleMaQuyen.equals("CSVC")) { // Giả sử "CSVC" là MaQuyen
            sql.append("WHERE pyc.TrangThaiID_FK = :statusWaitingFacilities ");
//            sql.append("OR (pyc.DuyetCSVC =  1 ) ");
        } else if (roleMaQuyen.equals("TK")) { // Giả sử "TK" là MaQuyen
            sql.append("WHERE pyc.TrangThaiID_FK = :statusWaitingDean ");
//            sql.append("OR (pyc.DuyetTK IS NOT NULL   ) ");
        } else {
            // Vai trò không xác định hoặc không có quyền xem phiếu quản lý
            return new ArrayList<>();
        }
        sql.append("ORDER BY pyc.NgayGui DESC");

        Query query = entityManager.createNativeQuery(sql.toString());

        // Lấy UserID hiện tại để filter các phiếu "đã xử lý bởi tôi" (nếu cần)
        // String emailCurrentUser = SecurityUtils.getPrincipal(); // Lấy ở Service và truyền vào đây nếu cần
        // UserEntity currentUser = iUserRepository.findByAccountEmail(emailCurrentUser).orElse(null);
        // Long currentUserId = (currentUser != null) ? currentUser.getUserID() : -1L; // Giá trị không bao giờ khớp nếu không có user


        if (roleMaQuyen.equals("GVU")) {
            query.setParameter("statusWaitingRegistrar", statusWaitingRegistrarId);
            // query.setParameter("currentUserIdParam", currentUserId); // Nếu dùng điều kiện MODIFIED_GVU
        } else if (roleMaQuyen.equals("CSVC")) {
            query.setParameter("statusWaitingFacilities", statusWaitingFacilitiesId);
            // query.setParameter("currentUserIdParam", currentUserId);
        } else if (roleMaQuyen.equals("TK")) {
            query.setParameter("statusWaitingDean", statusWaitingDeanId);
            // query.setParameter("currentUserIdParam", currentUserId);
        }
        // Xóa :currentUserIdParam nếu không dùng đến điều kiện đó
        // Hiện tại, để đơn giản, query chỉ lấy các phiếu đang chờ vai trò đó.
        // Bạn cần thêm logic lấy UserID hiện tại và truyền vào query nếu muốn mở rộng điều kiện WHERE

        List<Object[]> resultList = query.getResultList();
        List<TicketRequestOneDto> ticketRequestOneDtoList = new ArrayList<>();

        for (Object[] result : resultList) {
            TicketRequestOneDto dto = new TicketRequestOneDto();
            dto.setRequestId(result[0].toString());
            // dto.setTypeRequestId(result[1].toString()); // MaLoaiYC
            dto.setTypeRequestName(result[2].toString()); // NDloaiYC - Thêm trường này vào DTO
            dto.setDateRequest(DateUtils.dateTimeConvertToString((Timestamp) result[3]));

            UserEntity userTaoPhieu = iUserRepository.findById(Long.valueOf(result[4].toString())).orElse(null);
            dto.setUserRequest(userTaoPhieu != null ? userTaoPhieu.getFirstName() + " " + userTaoPhieu.getLastName() : "N/A");

            // dto.setStatus(result[5].toString()); // MaTrangThai của phiếu
            dto.setStatusName(result[6].toString()); // NDTrangThai của phiếu - Thêm trường này vào DTO

            // Để hiển thị trạng thái duyệt cụ thể của vai trò hiện tại (nếu cần)
            // Ví dụ: nếu là GVU, bạn có thể lấy trạng thái từ cột DuyetGVU của phiếu đó
            // TicketRequestEntity phieu = iTicketRequestRepository.findById(Long.valueOf(result[0].toString())).orElse(null);
            // if (phieu != null && phieu.getDuyetGVU() != null) {
            //    dto.setCurrentRoleApprovalStatus(phieu.getDuyetGVU().getNDTrangThai()); // Thêm trường này vào DTO
            // }


            ticketRequestOneDtoList.add(dto);
        }
        return ticketRequestOneDtoList;
    }
}
