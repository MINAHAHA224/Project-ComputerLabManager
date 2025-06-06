package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseDto;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseFields;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseOneDto;
import com.example.computerweb.DTO.dto.notificationResponse.NotificationDetailResponseDto;
import com.example.computerweb.DTO.dto.notificationResponse.NotificationResponseDto;
import com.example.computerweb.DTO.dto.requestTicketResponse.RequestTicketResponseDto;
import com.example.computerweb.DTO.dto.ticketResponse.TicketResponseMgmDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseFailure;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.roomRequest.TicketCancelCalendarRequestDto;
import com.example.computerweb.DTO.requestBody.roomRequest.TicketChangeRoomRequestDto;
import com.example.computerweb.DTO.requestBody.roomRequest.TicketRentRoomRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketApprovalDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketChangeRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketChangeDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketManagementRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRentDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRequestOneDto;
import com.example.computerweb.exceptions.CalendarException;
import com.example.computerweb.models.entity.*;
import com.example.computerweb.models.enums.StatusEnum;
import com.example.computerweb.repositories.*;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.services.ITicketRequestService;
import com.example.computerweb.utils.DateUtils;
import com.example.computerweb.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TicketRequestServiceImpl implements ITicketRequestService {
    private final ICalendarService iCalendarService;
    private final ITicketRequestRepository iTicketRequestRepository;
    private final IStatusRepository iStatusRepository;
    private final INotificationRepository iNotificationRepository;
    private final IWeekSemesterRepository iWeekSemesterRepository;
    private final ICalendarRepository iCalendarRepository;
    private final IPracticeCaseRepository iPracticeCaseRepository;
    private final IRoomRepository iRoomRepository;
    private final ITypeRequestRepository iTypeRequestRepository;
    private final IClassroomRepository iClassroomRepository;
    private final ISubjectRepository iSubjectRepository;
    private final IAccountRepository iAccountRepository;
    private final IUserRepository iUserRepository;
    private final ICreditClassRepository iCreditClassRepository;

    private boolean doIntervalsOverlap(long startIdA, long endIdA, long startIdB, long endIdB) {
        if (startIdA > endIdA || startIdB > endIdB) return false;
        return Math.max(startIdA, startIdB) <= Math.min(endIdA, endIdB);
    }

    // Hàm tiện ích kiểm tra phòng trống toàn diện (đã có ở các ví dụ trước)
    private boolean isRoomFreeForEntireDuration(
            Long calendarIdToExclude, // ID của lịch gốc nếu đang update/thay đổi
            RoomEntity roomCandidate,
            WeekSemesterEntity weekSemester,
            Long day,
            PracticeCaseEntity practiceCaseBegin, // Tiết bắt đầu của khoảng mới
            Long allCase, // Số tiết của khoảng mới
            StatusEntity statusActive,
            ICalendarRepository calendarRepository,
            IPracticeCaseRepository practiceCaseRepository,
            Map<String, List<long[]>> tempOccupiedSlots // Có thể là null nếu không check temp
    ) {
        List<PracticeCaseEntity> practiceCasesForBooking;
        List<PracticeCaseEntity> potentialCases = practiceCaseRepository.findPracticeCasesStartingFromId(
                practiceCaseBegin.getId(), PageRequest.of(0, allCase.intValue()));

        if (potentialCases.size() < allCase) {
            // Không đủ tiết liên tục từ DB, coi như không trống
            System.err.println(String.format("isRoomFreeForEntireDuration: Không tìm thấy đủ %d tiết TH liên tục từ tiết '%s'", allCase, practiceCaseBegin.getNamePracticeCase()));
            return false;
        }
        // Kiểm tra tính liên tục ID (quan trọng)
        for (int k = 0; k < potentialCases.size(); k++) {
            PracticeCaseEntity currentFetchedCase = potentialCases.get(k);
            Long expectedId = practiceCaseBegin.getId() + k;
            if (!currentFetchedCase.getId().equals(expectedId)) {
                System.err.println(String.format("isRoomFreeForEntireDuration: Tính liên tục ID tiết TH bị gián đoạn cho tiết bắt đầu '%s'", practiceCaseBegin.getNamePracticeCase()));
                return false; // Coi như không trống nếu ID không liên tục
            }
        }
        practiceCasesForBooking = potentialCases;

        long currentBookingStartPcId = practiceCasesForBooking.get(0).getId();
        long currentBookingEndPcId = practiceCasesForBooking.get(practiceCasesForBooking.size() - 1).getId();

        // 1. Kiểm tra với DB
        List<CalendarEntity> existingBookingsInDb = calendarRepository.findAllByWeekSemesterAndDayAndRoomAndStatus(
                weekSemester, day, roomCandidate, statusActive);

        if (!existingBookingsInDb.isEmpty()) {
            for (CalendarEntity existingBooking : existingBookingsInDb) {
                if (calendarIdToExclude != null && existingBooking.getId().equals(calendarIdToExclude)) {
                    continue; // Bỏ qua chính lịch gốc nếu đang kiểm tra cho việc thay đổi nó
                }
                PracticeCaseEntity existingStartPc = existingBooking.getPracticeCase();
                Long existingAllCase = existingBooking.getAllCase();
                if (existingStartPc == null || existingAllCase == null || existingAllCase <= 0) continue;

                long existingDbStartId = existingStartPc.getId();
                long existingDbEndId = existingStartPc.getId() + existingAllCase - 1;
                if (doIntervalsOverlap(currentBookingStartPcId, currentBookingEndPcId, existingDbStartId, existingDbEndId)) {
                    return false; // Chồng chéo với DB
                }
            }
        }

        // 2. Kiểm tra với slot "đặt tạm" (nếu có sử dụng)
        if (tempOccupiedSlots != null) {
            String roomTimeSlotKey = roomCandidate.getId() + "_" + weekSemester.getId() + "_" + day;
            List<long[]> tempIntervals = tempOccupiedSlots.get(roomTimeSlotKey);
            if (tempIntervals != null && !tempIntervals.isEmpty()) {
                for (long[] interval : tempIntervals) {
                    if (doIntervalsOverlap(currentBookingStartPcId, currentBookingEndPcId, interval[0], interval[1])) {
                        return false; // Chồng chéo với slot tạm
                    }
                }
            }
        }
        return true; // Phòng trống
    }


    ///////////////////////////////Test


    @Override
    public List<TicketRequestOneDto> handleGetAllDataForRqManagementPage() {
        StatusEntity statusPending = this.iStatusRepository.findStatusEntityById(1L);
        StatusEntity statusApproval = this.iStatusRepository.findStatusEntityById(2L);
        List<TicketRequestOneDto> data = new ArrayList<>();

        String email = SecurityUtils.getPrincipal();
        AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(email).get();
        RoleEntity roleUser = account.getUser().getRole();
        String nameRole = roleUser.getNameRole();
        data = this.iTicketRequestRepository.findListTicketForRole(nameRole);
        return data;
    }

    @Override
    public TicketResponseMgmDto handleGetDetailRequest(Long ticketId) {
        TicketRequestEntity ticketRequest = this.iTicketRequestRepository.getTicketRequestEntityById(ticketId);
        TicketResponseMgmDto ticket = new TicketResponseMgmDto();
        ticket.setRequestId(ticketRequest.getId().toString());
        ticket.setTypeRequest(ticketRequest.getTypeRequest().getNameTypeRequest());
        ticket.setDateRequest(DateUtils.dateTimeConvertToString(ticketRequest.getDateRequest()));
        ticket.setUserRequest(ticketRequest.getUser().getFirstName() + " " + ticketRequest.getUser().getLastName());

        StatusEntity doneCSVC = ticketRequest.getStatusCSVC();
        Date dateCreateCSVC = ticketRequest.getDateCreateCSVC();
        UserEntity userCSVC = ticketRequest.getUserCSVC();

        StatusEntity doneGVU = ticketRequest.getStatusGVU();
        Date dateCreateGVU = ticketRequest.getDateCreateGVU();
        UserEntity userGVU = ticketRequest.getUserGVU();

        ticket.setDoneCSVC(doneCSVC != null ? doneCSVC.getNameStatus() : null);
        ticket.setCreated_CSVC(dateCreateCSVC != null ? DateUtils.convertToString(dateCreateCSVC) : null);
        ticket.setModified_CSVC(userCSVC != null ? userCSVC.getFirstName() + " " + userCSVC.getLastName() : null);

        ticket.setDoneGVU(doneGVU != null ? doneGVU.getNameStatus() : null);
        ticket.setCreated_GVU(dateCreateGVU != null ? DateUtils.convertToString(dateCreateGVU) : null);
        ticket.setModified_GVU(userGVU != null ? userGVU.getFirstName() + " " + userGVU.getLastName() : null);


        String nameTypeRequest = ticketRequest.getTypeRequest().getNameTypeRequest();


        if (nameTypeRequest.equals("TDL")) {
            CalendarEntity calendarOld = ticketRequest.getCalendar();
            WeekSemesterEntity weekSemesterEntityOld = calendarOld.getWeekSemester();
            ticket.setWeekSemesterOld("Tuần: " + weekSemesterEntityOld.getWeekStudy() + "[Từ " + DateUtils.convertToString(weekSemesterEntityOld.getDateBegin()) + " Đến " + DateUtils.convertToString(weekSemesterEntityOld.getDateEnd()) + "]");
            ticket.setDayOld(calendarOld.getDay().toString());
            ticket.setPracticeCaseBeginOld(calendarOld.getPracticeCase().getNamePracticeCase());
            ticket.setAllCaseOld(calendarOld.getAllCase().toString());
            ticket.setRoomOld(calendarOld.getRoom().getNameRoom());
            ticket.setNoteOld(calendarOld.getNoteCalendar());

            // new use data on ticket
            WeekSemesterEntity weekSemesterEntityNew = ticketRequest.getWeekSemester();
            ticket.setWeekSemesterNew("Tuần : " + weekSemesterEntityNew.getWeekStudy() + "[Từ " + DateUtils.convertToString(weekSemesterEntityNew.getDateBegin()) + " Đến " + DateUtils.convertToString(weekSemesterEntityNew.getDateEnd()) + "]");
            ticket.setDayNew(ticketRequest.getDay().toString());
            ticket.setPracticeCaseBeginNew(ticketRequest.getPracticeCase().getNamePracticeCase());
            ticket.setAllCaseNew(ticketRequest.getAllCase().toString());
            ticket.setRoomNew(ticketRequest.getRoom().getNameRoom());
            ticket.setNoteNew(ticketRequest.getNoteTicket());


        } else if (nameTypeRequest.equals("MP")) {
            // rent room
            WeekSemesterEntity weekSemesterEntityNew = ticketRequest.getWeekSemester();
            ticket.setWeekSemesterNew("Tuần : " + weekSemesterEntityNew.getWeekStudy() + "[Từ " + DateUtils.convertToString(weekSemesterEntityNew.getDateBegin()) + " Đến " + DateUtils.convertToString(weekSemesterEntityNew.getDateEnd()) + "]");
            ticket.setDayNew(ticketRequest.getDay().toString());
            ticket.setPracticeCaseBeginNew(ticketRequest.getPracticeCase().getNamePracticeCase());
            ticket.setAllCaseNew(ticketRequest.getAllCase().toString());
            ticket.setRoomNew(ticketRequest.getRoom().getNameRoom());
            ticket.setNoteNew(ticketRequest.getNoteTicket());
        } else if (nameTypeRequest.equals("HUY")) {
            CalendarEntity calendarOld = ticketRequest.getCalendar();
            WeekSemesterEntity weekSemesterEntityOld = calendarOld.getWeekSemester();
            ticket.setWeekSemesterOld("Tuần : " + weekSemesterEntityOld.getWeekStudy() + "[Từ " + DateUtils.convertToString(weekSemesterEntityOld.getDateBegin()) + " Đến " + DateUtils.convertToString(weekSemesterEntityOld.getDateEnd()) + "]");
            ticket.setDayOld(calendarOld.getDay().toString());
            ticket.setPracticeCaseBeginOld(calendarOld.getPracticeCase().getNamePracticeCase());
            ticket.setAllCaseOld(calendarOld.getAllCase().toString());
            ticket.setRoomOld(calendarOld.getRoom().getNameRoom());
            ticket.setNoteOld(calendarOld.getNoteCalendar());
        }


        return ticket;
    }

    @Override
    @Transactional
    public ResponseEntity<String> handleTicketRequest(TicketManagementRequestDto ticketManagementRequestDto) {
        // information account current on this software
        String emailCurrent = SecurityUtils.getPrincipal();
        AccountEntity accountCurrent = this.iAccountRepository.findAccountEntityByEmail(emailCurrent).get();
        UserEntity userCurrent = accountCurrent.getUser();
        String roleUserCurrent = userCurrent.getRole().getNameRole();
        StatusEntity statusActive = this.iStatusRepository.findStatusEntityByNameStatus("ACTIVE");
        // data ticketRequest
        TicketRequestEntity ticketRequestEntity = this.iTicketRequestRepository.getTicketRequestEntityById(ticketManagementRequestDto.getTicketId());
        StatusEntity statusReject = this.iStatusRepository.findStatusEntityByNameStatus("REJECT");
        StatusEntity statusApproval = this.iStatusRepository.findStatusEntityByNameStatus("APPROVAL");
        StatusEntity statusNotSeen = this.iStatusRepository.findStatusEntityByNameStatus("NOTSEEN");
        // calendar

        // data calendarNew
        WeekSemesterEntity weekSemesterNew = ticketRequestEntity.getWeekSemester();
        Long dayNew = ticketRequestEntity.getDay();
        PracticeCaseEntity practiceCaseBeginNew = ticketRequestEntity.getPracticeCase();
        Long allCaseNew = ticketRequestEntity.getAllCase();
        RoomEntity roomNew = ticketRequestEntity.getRoom();
        UserEntity teacher = ticketRequestEntity.getUser();
        if (ticketManagementRequestDto.getStatus().equals("REJECT")) {
            try {
                // save ticket
                if (roleUserCurrent.equals("GVU")) {
                    ticketRequestEntity.setStatusGVU(statusReject);
                    ticketRequestEntity.setDateCreateGVU(new Date());
                    ticketRequestEntity.setUserGVU(userCurrent);
                    ticketRequestEntity.setStatusTicket(statusReject);
                } else if (roleUserCurrent.equals("CSVC")) {
                    ticketRequestEntity.setStatusCSVC(statusReject);
                    ticketRequestEntity.setDateCreateCSVC(new Date());
                    ticketRequestEntity.setUserCSVC(userCurrent);
                    ticketRequestEntity.setStatusTicket(statusReject);
                } else if (roleUserCurrent.equals("TK")) {
                    ticketRequestEntity.setStatusTK(statusReject);
                    ticketRequestEntity.setDateCreateTK(new Date());
                    ticketRequestEntity.setUserTK(userCurrent);
                    ticketRequestEntity.setStatusTicket(statusReject);
                }
                this.iTicketRequestRepository.save(ticketRequestEntity);

                NotificationEntity notificationEntity = new NotificationEntity();
                notificationEntity.setUser(teacher);
                notificationEntity.setNameNotification("Thông tin về việc thay đổi lịch");
                notificationEntity.setContentNotification(ticketManagementRequestDto.getNoteNotification());
                notificationEntity.setDateNotification(new Date()); // xử lí date and time
                notificationEntity.setTicketRequest(ticketRequestEntity);
                notificationEntity.setUserGui(userCurrent);
                notificationEntity.setStatus(statusNotSeen);
                // save ticket
                this.iNotificationRepository.save(notificationEntity);
            } catch (Exception e) {
                System.out.println("--ER : error save ticketRequest" + e.getMessage());
                e.printStackTrace();
                throw e;
            }
            return ResponseEntity.ok().body("Phiếu từ chối đã được gửi");
        } else if (ticketManagementRequestDto.getStatus().equals("APPROVAL")) {

            TypeRequestEntity typeRequestMp = this.iTypeRequestRepository.findTypeRequestEntityByNameTypeRequest("MP");
            TypeRequestEntity typeRequestTdl = this.iTypeRequestRepository.findTypeRequestEntityByNameTypeRequest("TDL");
            TypeRequestEntity typeRequestHUY = this.iTypeRequestRepository.findTypeRequestEntityByNameTypeRequest("HUY");
            // Approval with GVU
            if (roleUserCurrent.equals("GVU")) {


                if (ticketRequestEntity.getTypeRequest() == typeRequestMp) {
                    try {
                        CalendarEntity calendarEntity = new CalendarEntity();
                        calendarEntity.setCreditClass(null);
                        calendarEntity.setUser(teacher);
//                            calendarEntity.setGroup(null);
//                            calendarEntity.setOrganization(null);
                        calendarEntity.setWeekSemester(weekSemesterNew);
                        calendarEntity.setDay(dayNew);
                        calendarEntity.setPracticeCase(practiceCaseBeginNew);
                        calendarEntity.setAllCase(allCaseNew);
                        calendarEntity.setRoom(roomNew);
                        calendarEntity.setStatus(statusActive);
                        calendarEntity.setNoteCalendar("Lich muon phong");
                        this.iCalendarRepository.save(calendarEntity);
                    } catch (Exception e) {
                        System.out.println("--ER : error save calendar practiceCase" + e.getMessage());
                        e.printStackTrace();
                        throw e;
                    }
                    // save ticket
                    ticketRequestEntity.setStatusGVU(statusApproval);
                    ticketRequestEntity.setDateCreateGVU(new Date());
                    ticketRequestEntity.setUserGVU(userCurrent);
                    ticketRequestEntity.setStatusTicket(statusApproval);
                    this.iTicketRequestRepository.save(ticketRequestEntity);

                    // save notification
                    NotificationEntity notificationEntity = new NotificationEntity();
                    notificationEntity.setUser(teacher);
                    notificationEntity.setNameNotification("Thông tin về việc mượn phòng ");
                    notificationEntity.setContentNotification(ticketManagementRequestDto.getNoteNotification());
                    notificationEntity.setDateNotification(new Date()); // xử lí date and time
                    notificationEntity.setTicketRequest(ticketRequestEntity);
                    notificationEntity.setUserGui(userCurrent);
                    notificationEntity.setStatus(statusNotSeen);
                    this.iNotificationRepository.save(notificationEntity);

                    return ResponseEntity.ok().body("Lịch mượn phòng đã tạo thành công");
                } else if (ticketRequestEntity.getTypeRequest() == typeRequestHUY) {
                    try {
                        CalendarEntity calendarEntity = ticketRequestEntity.getCalendar();
                        this.iCalendarRepository.deleteById(calendarEntity.getId());
                    } catch (Exception e) {
                        System.out.println("--ER : error Huy calendar" + e.getMessage());
                        e.printStackTrace();
                        throw e;
                    }
                    // save ticket
                    ticketRequestEntity.setCalendar(null);
                    ticketRequestEntity.setStatusGVU(statusApproval);
                    ticketRequestEntity.setDateCreateGVU(new Date());
                    ticketRequestEntity.setUserGVU(userCurrent);
                    ticketRequestEntity.setStatusTicket(statusApproval);
                    this.iTicketRequestRepository.save(ticketRequestEntity);


                    // save notification
                    NotificationEntity notificationEntity = new NotificationEntity();
                    notificationEntity.setUser(teacher);
                    notificationEntity.setNameNotification("Thông tin về việc hủy phòng");
                    notificationEntity.setContentNotification(ticketManagementRequestDto.getNoteNotification());
                    notificationEntity.setDateNotification(new Date()); // xử lí date and time
                    notificationEntity.setTicketRequest(ticketRequestEntity);
                    notificationEntity.setUserGui(userCurrent);
                    notificationEntity.setStatus(statusNotSeen);
                    this.iNotificationRepository.save(notificationEntity);


                    return ResponseEntity.ok().body("Lịch mượn phòng xóa thành công");
                }
                // type : change calendar ( room , calendar )
                else if (ticketRequestEntity.getTypeRequest() == typeRequestTdl) {
                    StatusEntity offStatus = this.iStatusRepository.findStatusEntityByNameStatus("OFF");
                    //  boolean checkRequestTdl = this.iCalendarRepository.existsByCreditClassAndWeekSemesterAndDayAndPracticeCaseAndRoom(creditClass,weekSemesterNew, dayNew, practiceCaseBeginNew,roomNew);
                    try {
                        CalendarEntity calendarEntityCurrent = ticketRequestEntity.getCalendar();
                        calendarEntityCurrent.setStatus(offStatus);
                        CalendarEntity calendarNew = new CalendarEntity();
                        calendarNew.setCreditClass(calendarEntityCurrent.getCreditClass());
//                        calendarNew.setGroup(calendarEntityCurrent.getGroup());
//                        calendarNew.setOrganization(calendarEntityCurrent.getOrganization());
                        calendarNew.setWeekSemester(weekSemesterNew);
                        calendarNew.setDay(dayNew);
                        calendarNew.setPracticeCase(practiceCaseBeginNew);
                        calendarNew.setAllCase(calendarEntityCurrent.getAllCase());
                        calendarNew.setRoom(roomNew);
                        calendarNew.setStatus(statusActive);
                        calendarNew.setNoteCalendar("Lich day bu");
                        this.iCalendarRepository.save(calendarEntityCurrent);
                        this.iCalendarRepository.save(calendarNew);

                    } catch (Exception e) {
                        System.out.println("--ER : error save change calendar practiceCase" + e.getMessage());
                        e.printStackTrace();
                        throw e;
                    }
                    // save ticket
                    ticketRequestEntity.setStatusGVU(statusApproval);
                    ticketRequestEntity.setDateCreateGVU(new Date());
                    ticketRequestEntity.setUserGVU(userCurrent);
                    ticketRequestEntity.setStatusTicket(statusApproval);
                    this.iTicketRequestRepository.save(ticketRequestEntity);
                    // save notification

                    // save notification
                    NotificationEntity notificationEntity = new NotificationEntity();
                    notificationEntity.setUser(teacher);
                    notificationEntity.setNameNotification("Thay đổi lịch hoặc Thay đổi phòng");
                    notificationEntity.setContentNotification(ticketManagementRequestDto.getNoteNotification());
                    notificationEntity.setDateNotification(new Date()); // xử lí date and time
                    notificationEntity.setTicketRequest(ticketRequestEntity);
                    notificationEntity.setUserGui(userCurrent);
                    notificationEntity.setStatus(statusNotSeen);
                    this.iNotificationRepository.save(notificationEntity);

                    return ResponseEntity.ok().body("Thay đổi lịch đã thành công");
                }
            } else if (roleUserCurrent.equals("CSVC")) {
                try {
                    StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("ACTIVE");

                    boolean checkRequestMp = this.iCalendarRepository.existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus(weekSemesterNew, dayNew, practiceCaseBeginNew, roomNew, status);
                    if (!checkRequestMp) {
                        ticketRequestEntity.setStatusCSVC(statusApproval);
                        ticketRequestEntity.setDateCreateCSVC(new Date());
                        ticketRequestEntity.setUserCSVC(userCurrent);
                        this.iTicketRequestRepository.save(ticketRequestEntity);
                    } else {
                        throw new CalendarException("Đã tồn tại lịch vào Thứ: " +
                                dayNew + ", Tuần: " + weekSemesterNew.getWeekStudy() + " Thời gian: [" +
                                DateUtils.convertToString(weekSemesterNew.getDateBegin()) +
                                "-" +
                                DateUtils.convertToString(weekSemesterNew.getDateBegin()) + // Chỗ này hình như bị lặp, cần kiểm tra lại
                                "]" +
                                ", Bắt đầu tại ca thực hành: " +
                                practiceCaseBeginNew.getNamePracticeCase()
                        );
                    }
                } catch (CalendarException e) {
                    System.out.println("---ER error save doneCSVC" + e.getMessage());
                    e.printStackTrace();
                    throw new CalendarException(e.getMessage());
                }
                return ResponseEntity.ok().body("Phiếu chấp thuận thành công (CSVC)");

            }
        }
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<String> handleCreateTicketDeleteRoom(Long calendarId, String message) {
        String email = SecurityUtils.getPrincipal();
        AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(email).get();
        UserEntity user = account.getUser();


        CalendarEntity calendarDelete = this.iCalendarRepository.findCalendarEntityById(calendarId);
        TypeRequestEntity typeRequest = this.iTypeRequestRepository.findTypeRequestEntityByNameTypeRequest("HUY");
        WeekSemesterEntity weekSemester = calendarDelete.getWeekSemester();
        Long day = calendarDelete.getDay();
        Long allCase = calendarDelete.getAllCase();
        RoomEntity room = calendarDelete.getRoom();
        PracticeCaseEntity practiceCase = calendarDelete.getPracticeCase();
        StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("APPROVAL");
        StatusEntity statusPending = this.iStatusRepository.findStatusEntityByNameStatus("PENDING");
        try {
            TicketRequestEntity ticketRequest = new TicketRequestEntity();
            ticketRequest.setTypeRequest(typeRequest);
            ticketRequest.setCalendar(calendarDelete);
            ticketRequest.setDateRequest(new Date());
            ticketRequest.setUser(user);
            ticketRequest.setWeekSemester(weekSemester);
            ticketRequest.setDay(day);
            ticketRequest.setPracticeCase(practiceCase);
            ticketRequest.setAllCase(allCase);
            ticketRequest.setRoom(room);
            ticketRequest.setNoteTicket(message);
            ticketRequest.setStatusTicket(statusPending);
            ticketRequest.setStatusCSVC(status);
            ticketRequest.setDateCreateCSVC(new Date());

            this.iTicketRequestRepository.save(ticketRequest);

            return ResponseEntity.ok().body("Tạo phiếu xóa phòng thành công");
        } catch (Exception e) {
            System.out.println("-----ER error save delete ticket room" + e.getMessage());
            e.printStackTrace();

        }

        return ResponseEntity.badRequest().body("Tạo phiếu xóa phòng thất bại");
    }

    @Override
    public CalendarResponseDto handleGetCreateTicketChangeCalendar(Long calendarId) {

        CalendarEntity calendarEntity = this.iCalendarRepository.findCalendarEntityById(calendarId);

        CalendarResponseDto data = new CalendarResponseDto();
        CalendarResponseOneDto calendarCurrent = new CalendarResponseOneDto();
        calendarCurrent.setCalendarId(calendarEntity.getId().toString());
        calendarCurrent.setCreditClassId(calendarEntity.getCreditClass() != null ? calendarEntity.getCreditClass().getId().toString() : null);
        calendarCurrent.setUserIdMp_Fk(calendarEntity.getUser() != null ? calendarEntity.getUser().getFirstName() + " " + calendarEntity.getUser().getLastName() : null);
//        calendarCurrent.setGroupId(calendarEntity.getGroup()!=null ? calendarEntity.getGroup() : null);
        calendarCurrent.setWeekSemesterId(calendarEntity.getWeekSemester().getId().toString());
        calendarCurrent.setDayId(calendarEntity.getDay().toString());
        calendarCurrent.setPracticeCaseBeginId(calendarEntity.getPracticeCase().getId().toString());
        calendarCurrent.setAllCase(calendarEntity.getAllCase().toString());
        calendarCurrent.setRoomId(calendarEntity.getRoom().getId().toString());
        calendarCurrent.setPurposeUse(calendarEntity.getNoteCalendar());

        // get database
        CalendarResponseFields calendarResponseFields = this.iCalendarService.handleGetDataForCreatePage();
        data.setUserCurrent(calendarCurrent);
        data.setDataBase(calendarResponseFields);
        // set data , then response
        return data;
    }

    @Override
    @Transactional
    public ResponseEntity<String> handlePostCreateTicketChangeCalendar(TicketChangeDto ticketChangeDto) {
        try {
            TypeRequestEntity typeRequest = this.iTypeRequestRepository.findTypeRequestEntityById(ticketChangeDto.getTypeRequestId());
            CalendarEntity calendar = this.iCalendarRepository.findCalendarEntityById(ticketChangeDto.getCalendarId());
            Date dateSent = new Date();
            Long day = ticketChangeDto.getDay();
            WeekSemesterEntity weekSemester = this.iWeekSemesterRepository.findWeekSemesterEntityById(ticketChangeDto.getWeekSemesterId());
            Long allCase = calendar.getAllCase();

            PracticeCaseEntity caseBegin = this.iPracticeCaseRepository.findPracticeCaseEntityById(ticketChangeDto.getCaseBeginId());
            // Kiểm tra số tiết liên tục có hợp lệ không
            List<PracticeCaseEntity> practiceCasesForBooking;
            List<PracticeCaseEntity> potentialCases = iPracticeCaseRepository.findPracticeCasesStartingFromId(
                    caseBegin.getId(), PageRequest.of(0, allCase.intValue()));
            if (potentialCases.size() < allCase) {
                throw new CalendarException(String.format("Không tìm thấy đủ %d tiết TH liên tục trên hệ thống bắt đầu từ tiết '%s' (ID: %d).",
                        allCase, calendar.getPracticeCase().getNamePracticeCase(), calendar.getPracticeCase().getId()));
            }

            String note = ticketChangeDto.getPurposeUse();
            StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("PENDING");

            //userCurrent
            String email = SecurityUtils.getPrincipal();
            AccountEntity accountCurrent = this.iAccountRepository.findAccountEntityByEmail(email).get();
            UserEntity userSent = accountCurrent.getUser();

            TicketRequestEntity newTicket = new TicketRequestEntity();
            newTicket.setTypeRequest(typeRequest);
            newTicket.setCalendar(calendar);
            newTicket.setDateRequest(dateSent);
            newTicket.setUser(userSent);
            newTicket.setWeekSemester(weekSemester);
            newTicket.setDay(day);
            newTicket.setPracticeCase(caseBegin);

            newTicket.setNoteTicket(note);
            newTicket.setStatusTicket(status);
            newTicket.setStatusTK(status);

            this.iTicketRequestRepository.save(newTicket);
            return ResponseEntity.ok().body("Yêu cầu thay đổi lịch thành công");
        } catch (Exception e) {
            System.out.println("---ER error save ticketRequest on request GV" + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<String> handlePostCreateTicketRentRoom(TicketRentDto ticketRentDto) {

        try {

            TypeRequestEntity typeRequest = this.iTypeRequestRepository.findTypeRequestEntityById(ticketRentDto.getTypeRequestId());

            Date dateSent = new Date();
            Long day = ticketRentDto.getDay();
            WeekSemesterEntity weekSemester = this.iWeekSemesterRepository.findWeekSemesterEntityById(ticketRentDto.getWeekSemesterId());
            PracticeCaseEntity caseBegin = this.iPracticeCaseRepository.findPracticeCaseEntityById(ticketRentDto.getCaseBeginId());
            Long allCase = ticketRentDto.getAllCase();
            RoomEntity room = this.iRoomRepository.findRoomEntityById(ticketRentDto.getRoomId());
            String note = ticketRentDto.getPurposeUse();
            StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("PENDING");

            //userCurrent
            String email = SecurityUtils.getPrincipal();
            AccountEntity accountCurrent = this.iAccountRepository.findAccountEntityByEmail(email).get();
            UserEntity userSent = accountCurrent.getUser();

            TicketRequestEntity newTicket = new TicketRequestEntity();
            newTicket.setTypeRequest(typeRequest);
            newTicket.setCalendar(null);
            newTicket.setDateRequest(dateSent);
            newTicket.setUser(userSent);
            newTicket.setWeekSemester(weekSemester);
            newTicket.setDay(day);
            newTicket.setPracticeCase(caseBegin);
            newTicket.setAllCase(allCase);
            newTicket.setRoom(room);
            newTicket.setNoteTicket(note);
            newTicket.setStatusTicket(status);
            newTicket.setStatusCSVC(status);
            newTicket.setStatusGVU(status);

            this.iTicketRequestRepository.save(newTicket);
            return ResponseEntity.ok().body("Yêu cầu thay đổi lịch thành công");
        } catch (Exception e) {
            System.out.println("---ER error save ticketRequest on request GV" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseEntity<String> handlePostDeleteTicketRentRoom(Long calendarId) {
        return null;
    }

    @Override
    public List<NotificationResponseDto> handleGetAllNotificationOfUser() {
        String emailUser = SecurityUtils.getPrincipal();
        AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(emailUser).get();
        UserEntity user = account.getUser();
        List<NotificationEntity> allNotification = this.iNotificationRepository.findAllByUser(user);
        List<NotificationResponseDto> noteResponses = new ArrayList<>();
        for (NotificationEntity note : allNotification) {
            NotificationResponseDto noteResponse = new NotificationResponseDto();
            noteResponse.setId(note.getId().toString());
            noteResponse.setNameNotification(note.getNameNotification());
            noteResponse.setDateNotification(note.getDateNotification().toString());
            noteResponse.setStatus(note.getStatus().getNameStatus());
            noteResponses.add(noteResponse);
        }
        return noteResponses;
    }

    @Override
    @Transactional
    public NotificationDetailResponseDto handleChangeStatusNote(Long notificationId) {

        NotificationEntity notification = this.iNotificationRepository.findNotificationEntityById(notificationId);
        NotificationDetailResponseDto noteResponseDto = new NotificationDetailResponseDto();

        try {
            StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("SEEN");
            notification.setStatus(status);
            this.iNotificationRepository.save(notification);
            UserEntity userGui = notification.getUserGui();
            String department = userGui.getRole().getNameRole();

            noteResponseDto.setRequestTicketId(notification.getTicketRequest().getId().toString());
            noteResponseDto.setUserSent(userGui.getFirstName() + " " + userGui.getLastName());
            noteResponseDto.setDepartment(department);
            noteResponseDto.setNameNotification(notification.getNameNotification());
            noteResponseDto.setContentNotification(notification.getContentNotification());
            noteResponseDto.setDateNotification(DateUtils.dateTimeConvertToString(notification.getDateNotification()));
            noteResponseDto.setStatus(status.getNameStatus());
        } catch (Exception e) {
            System.out.println("--ER error save status notification" + e.getMessage());
            e.printStackTrace();
        }

        return noteResponseDto;
    }

    @Override
    @Transactional
    public ResponseEntity<String> handleDeleteOneOrMoreNote(String noteId) {
        List<String> listId = new ArrayList<>();
        if (noteId.contains(",")) {
            listId = Arrays.asList(noteId.split(","));
        } else {
            listId.add(noteId);
        }
        try {
            for (String id : listId) {
                this.iNotificationRepository.deleteById(Long.valueOf(id));
            }
        } catch (Exception e) {
            System.out.println("--ER error delete notification" + e.getMessage());
            e.printStackTrace();

        }

        return ResponseEntity.ok().body("Xóa thông báo thành công");
    }


    @Override
    @Transactional
    public ResponseEntity<String> handleDeleteOneOrMoreTicketRequest(String requestTicketIds) {
        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity account = iAccountRepository.findAccountEntityByEmail(emailCurrentUser)
                .orElseThrow(() -> new RuntimeException("Lỗi xác thực: Không tìm thấy người dùng."));

        UserEntity currentUser = account.getUser();
        List<String> idsToDelete = new ArrayList<>();
        if (requestTicketIds.contains(",")) {
            idsToDelete.addAll(Arrays.asList(requestTicketIds.split(",")));
        } else {
            idsToDelete.add(requestTicketIds);
        }

        int deletedCount = 0;
        List<String> errors = new ArrayList<>();

        for (String idStr : idsToDelete) {
            try {
                Long ticketId = Long.valueOf(idStr.trim());
                Optional<TicketRequestEntity> ticketOpt = iTicketRequestRepository.findById(ticketId);
                if (ticketOpt.isPresent()) {
                    TicketRequestEntity ticket = ticketOpt.get();
                    // Kiểm tra quyền: Chỉ GV tạo phiếu mới được xóa, và chỉ khi phiếu ở trạng thái nhất định (ví dụ: PENDING, REJECTED)
                    if (!ticket.getUser().getId().equals(currentUser.getId())) {
                        errors.add("Phiếu ID " + ticketId + ": Không có quyền xóa.");
                        continue;
                    }
                    // Ví dụ: Chỉ cho xóa phiếu đang chờ hoặc đã bị từ chối
                    String maTrangThaiPhieu = ticket.getStatusTicket() != null ? ticket.getStatusTicket().getNameStatus() : "";
                    if (maTrangThaiPhieu.equals("PENDING") /*Thêm các mã trạng thái cho phép xóa*/) {

                        // Trước khi xóa phiếu, có thể cần xóa thông báo liên quan (nếu ON DELETE CASCADE không được thiết lập)
                        // NotificationEntity notification = iNotificationRepository.findByTicketRequest(ticket);
                        // if (notification != null) { iNotificationRepository.delete(notification); }

                        iTicketRequestRepository.delete(ticket);
                        deletedCount++;
                    } else {
                        errors.add("Phiếu ID " + ticketId + ": Không thể xóa phiếu ở trạng thái '" + ticket.getStatusTicket().getNameStatus() + "'.");
                    }
                } else {
                    errors.add("Không tìm thấy phiếu với ID: " + ticketId);
                }
            } catch (NumberFormatException e) {
                errors.add("ID phiếu không hợp lệ: " + idStr);
            } catch (Exception e) {
                errors.add("Lỗi khi xóa phiếu ID " + idStr + ": " + e.getMessage());
                // Log lỗi chi tiết
                System.err.println("Lỗi khi xóa phiếu ID " + idStr + ": " + e);
            }
        }

        if (errors.isEmpty() && deletedCount > 0) {
            return ResponseEntity.ok("Đã xóa thành công " + deletedCount + " phiếu yêu cầu.");
        } else if (deletedCount > 0) {
            return ResponseEntity.status(HttpStatus.MULTI_STATUS).body("Đã xóa " + deletedCount + " phiếu. Các lỗi: " + String.join("; ", errors));
        } else {
            if (errors.isEmpty())
                return ResponseEntity.badRequest().body("Không có phiếu nào được chỉ định để xóa hoặc không tìm thấy.");
            return ResponseEntity.badRequest().body("Không có phiếu nào được xóa. Lỗi: " + String.join("; ", errors));
        }
    }

    @Override
    public List<RequestTicketResponseDto> handleGetAllRequestTicketGV() {
        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity account = iAccountRepository.findAccountEntityByEmail(emailCurrentUser)
                .orElseThrow(() -> new RuntimeException("Lỗi xác thực: Không tìm thấy người dùng với email: " + emailCurrentUser));
        UserEntity currentUser = account.getUser();
        // Lấy tất cả các phiếu yêu cầu do người dùng hiện tại tạo
        List<TicketRequestEntity> ticketRequests = iTicketRequestRepository.findAllByUserOrderByDateRequestDesc(currentUser); // Sắp xếp theo ngày gửi giảm dần

        List<RequestTicketResponseDto> responseDtos = new ArrayList<>();
        for (TicketRequestEntity ticket : ticketRequests) {
            RequestTicketResponseDto dto = new RequestTicketResponseDto();
            dto.setRequestTicketId(ticket.getId().toString());
            dto.setTypeRequest(ticket.getTypeRequest() != null ? ticket.getTypeRequest().getNameTypeRequest() : "N/A");
            dto.setDateSent(ticket.getDateRequest() != null ? DateUtils.dateTimeConvertToString(ticket.getDateRequest()) : "N/A"); // Sử dụng dateTimeConvertToString
            dto.setUserSent(currentUser.getFirstName() + " " + currentUser.getLastName()); // Luôn là người dùng hiện tại

            // Lấy trạng thái duyệt (ví dụ, bạn có thể muốn hiển thị trạng thái tổng thể của phiếu)
            // Hoặc trạng thái của bước duyệt cuối cùng/quan trọng nhất đối với GV
            if (ticket.getStatusTicket() != null) {
                dto.setStatusOverall(ticket.getStatusTicket().getNameStatus()); // NDTrangThai thay vì NameStatus cho dễ đọc
            } else {
                dto.setStatusOverall("Chưa xác định");
            }

            // Bạn có thể muốn thêm các trạng thái duyệt chi tiết hơn nếu cần, ví dụ:
            // dto.setStatusTK(ticket.getStatusTK() != null ? ticket.getStatusTK().getNDTrangThai() : "-");
            // dto.setStatusGVU(ticket.getStatusGVU() != null ? ticket.getStatusGVU().getNDTrangThai() : "-");
            // dto.setStatusCSVC(ticket.getStatusCSVC() != null ? ticket.getStatusCSVC().getNDTrangThai() : "-");
            // Tuy nhiên, RequestTicketResponseDto của bạn chỉ có statusCSVC và statusGVU, cần điều chỉnh DTO hoặc logic ở đây.
            // Tạm thời giữ theo DTO của bạn:
            dto.setStatusCSVC(ticket.getStatusCSVC() != null ? ticket.getStatusCSVC().getNameStatus() : "-");
            dto.setStatusGVU(ticket.getStatusGVU() != null ? ticket.getStatusGVU().getNameStatus() : "-");
            dto.setStatusTK(ticket.getStatusTK() != null ? ticket.getStatusTK().getNameStatus() : "-");

            responseDtos.add(dto);
        }
        return responseDtos;
    }

    @Override
    public TicketResponseMgmDto handleGetRequestTicketGV(Long ticketId) {
        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity account = iAccountRepository.findAccountEntityByEmail(emailCurrentUser)
                .orElseThrow(() -> new RuntimeException("Lỗi xác thực: Không tìm thấy người dùng với email: " + emailCurrentUser));
        UserEntity currentUser = account.getUser();
        TicketRequestEntity ticketRequest = iTicketRequestRepository.findById(ticketId)
                .orElseThrow(() -> new CalendarException("Không tìm thấy phiếu yêu cầu với ID: " + ticketId));

        // Kiểm tra xem GV này có phải là người tạo phiếu không
        if (!ticketRequest.getUser().getId().equals(currentUser.getId())) {
            throw new CalendarException("Bạn không có quyền xem chi tiết phiếu yêu cầu này.");
            // Hoặc return new ResponseFailure(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền xem chi tiết phiếu yêu cầu này.");
            // tùy theo cách bạn muốn xử lý lỗi ở controller.
        }

        // Sử dụng lại logic map từ handleGetDetailRequest (dành cho quản lý) nhưng có thể tùy chỉnh
        // nếu DTO hiển thị cho GV cần khác biệt. Hiện tại TicketResponseMgmDto có vẻ dùng chung.
        TicketResponseMgmDto ticketDto = new TicketResponseMgmDto();
        ticketDto.setRequestId(ticketRequest.getId().toString());
        ticketDto.setTypeRequest(ticketRequest.getTypeRequest() != null ? ticketRequest.getTypeRequest().getNameTypeRequest() : "N/A");
        ticketDto.setDateRequest(ticketRequest.getDateRequest() != null ? DateUtils.dateTimeConvertToString(ticketRequest.getDateRequest()) : "N/A");
        ticketDto.setUserRequest(ticketRequest.getUser().getFirstName() + " " + ticketRequest.getUser().getLastName()); // Người tạo phiếu

        // Thông tin duyệt (lấy từ các trường duyệt cụ thể trong TicketRequestEntity)
        // Ví dụ sử dụng cấu trúc duyệt bạn đã có:
        StatusEntity statusTKEntity = ticketRequest.getStatusTK();
        if (statusTKEntity != null) {
            ticketDto.setDoneTK(statusTKEntity.getNameStatus()); // Giả sử TicketResponseMgmDto có trường này
            if (ticketRequest.getUserTK() != null) {
                ticketDto.setModified_TK(ticketRequest.getUserTK().getFirstName() + " " + ticketRequest.getUserTK().getLastName());
            }
            if (ticketRequest.getDateCreateTK() != null) {
                ticketDto.setCreated_TK(DateUtils.dateTimeConvertToString(ticketRequest.getDateCreateTK()));
            }
            // ticketDto.setNoteTK(ticketRequest.getGhiChuTK()); // Nếu có trường ghi chú của TK
        }


        StatusEntity statusCSVCentity = ticketRequest.getStatusCSVC();
        if (statusCSVCentity != null) {
            ticketDto.setDoneCSVC(statusCSVCentity.getNameStatus());
            if (ticketRequest.getUserCSVC() != null) {
                ticketDto.setModified_CSVC(ticketRequest.getUserCSVC().getFirstName() + " " + ticketRequest.getUserCSVC().getLastName());
            }
            if (ticketRequest.getDateCreateCSVC() != null) {
                ticketDto.setCreated_CSVC(DateUtils.dateTimeConvertToString(ticketRequest.getDateCreateCSVC()));
            }
            // ticketDto.setNoteCSVC(ticketRequest.getGhiChuCSVC()); // Nếu có
        }

        StatusEntity statusGVUentity = ticketRequest.getStatusGVU();
        if (statusGVUentity != null) {
            ticketDto.setDoneGVU(statusGVUentity.getNameStatus());
            if (ticketRequest.getUserGVU() != null) {
                ticketDto.setModified_GVU(ticketRequest.getUserGVU().getFirstName() + " " + ticketRequest.getUserGVU().getLastName());
            }
            if (ticketRequest.getDateCreateGVU() != null) {
                ticketDto.setCreated_GVU(DateUtils.dateTimeConvertToString(ticketRequest.getDateCreateGVU()));
            }
            // ticketDto.setNoteGVU(ticketRequest.getGhiChuGVU()); // Nếu có
        }

        // Thông tin lịch gốc và lịch đề xuất (nếu có)
        CalendarEntity calendarOld = ticketRequest.getCalendar(); // Lịch gốc
        if (calendarOld != null) {
            WeekSemesterEntity wsOld = calendarOld.getWeekSemester();
            ticketDto.setWeekSemesterOld(wsOld != null ? "Tuần: " + wsOld.getWeekStudy() + " [Từ " + DateUtils.convertToString(wsOld.getDateBegin()) + " Đến " + DateUtils.convertToString(wsOld.getDateEnd()) + "]" : "N/A");
            ticketDto.setDayOld(calendarOld.getDay() != null ? calendarOld.getDay().toString() : "N/A");
            ticketDto.setPracticeCaseBeginOld(calendarOld.getPracticeCase() != null ? calendarOld.getPracticeCase().getNamePracticeCase() : "N/A");
            ticketDto.setAllCaseOld(calendarOld.getAllCase() != null ? calendarOld.getAllCase().toString() : "N/A");
            ticketDto.setRoomOld(calendarOld.getRoom() != null ? calendarOld.getRoom().getNameRoom() : "N/A");
            ticketDto.setNoteOld(calendarOld.getNoteCalendar());
        }

        // Thông tin lịch mới/đề xuất từ PhieuYeuCau
        // (Các trường DeXuat_... hoặc các trường tương ứng bạn dùng trong TicketRequestEntity)
        WeekSemesterEntity wsNew = ticketRequest.getWeekSemester(); // DeXuat_TuanHoc_KiHoc_Id_FK
        if (wsNew != null) {
            ticketDto.setWeekSemesterNew("Tuần: " + wsNew.getWeekStudy() + " [Từ " + DateUtils.convertToString(wsNew.getDateBegin()) + " Đến " + DateUtils.convertToString(wsNew.getDateEnd()) + "]");
        }
        ticketDto.setDayNew(ticketRequest.getDay() != null ? ticketRequest.getDay().toString() : null); // DeXuat_Thu
        ticketDto.setPracticeCaseBeginNew(ticketRequest.getPracticeCase() != null ? ticketRequest.getPracticeCase().getNamePracticeCase() : null); // DeXuat_SoTietBD_FK
        ticketDto.setAllCaseNew(ticketRequest.getAllCase() != null ? ticketRequest.getAllCase().toString() : null); // DeXuat_SoTiet
        ticketDto.setRoomNew(ticketRequest.getRoom() != null ? ticketRequest.getRoom().getNameRoom() : null); // DeXuat_PhongID_FK (có thể null ban đầu cho MP)
        ticketDto.setNoteNew(ticketRequest.getNoteTicket()); // DeXuat_GhiChu hoặc GhiChu của phiếu

        // Hiển thị trạng thái chung của phiếu
        if (ticketRequest.getStatusTicket() != null) {
            ticketDto.setStatusOverall(ticketRequest.getStatusTicket().getNameStatus()); // Thêm trường này vào TicketResponseMgmDto nếu cần
        }


        return ticketDto;
    }


    ////////////////////////////////////////////////////test changeCalendar

    @Override
    @Transactional
    public ResponseData<?> createChangeCalendarTicket(TicketChangeRequestDto changeRequestDto) {
        // 1. Lấy thông tin người dùng hiện tại
        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity accountCurrent = iAccountRepository.findAccountEntityByEmail(emailCurrentUser) // Giả sử có phương thức này
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + emailCurrentUser));

        // 2. Lấy lịch gốc cần thay đổi
        CalendarEntity calendarToChange = iCalendarRepository.findById(changeRequestDto.getCalendarIdToChange())
                .orElseThrow(() -> new CalendarException("Không tìm thấy lịch gốc với ID: " + changeRequestDto.getCalendarIdToChange()));

        //3.Số lịch nghỉ bù chỉ được phép = lịch chính thức
        StatusEntity statusOff = this.iStatusRepository.findStatusEntityByNameStatus(StatusEnum.OFF.getCode());
        if (statusOff == null) {
            throw new RuntimeException("Không tìm thấy statusOff");
        }
        StatusEntity statusActive = this.iStatusRepository.findStatusEntityByNameStatus(StatusEnum.ACTIVE.getCode());
        if (statusActive == null) {
            throw new RuntimeException("Không tìm thấy statusActive");
        }


        int countCalendarOff = this.iCalendarRepository.countCalendarEntityByStatus(statusOff);
        int countCalendarActive = this.iCalendarRepository.countCalendarEntityByStatus(statusActive);
        if (countCalendarOff == countCalendarActive) {
            throw new CalendarException("Không thể tạo thêm yêu cầu lịch nghĩ bù số lượng lịch bù đã vượt quá kế hoạch cho phép");
        }

        // Kiểm tra xem người dùng có quyền thay đổi lịch này không (ví dụ: GV của lớp tín chỉ đó)
        if (calendarToChange.getCreditClass() != null && !calendarToChange.getCreditClass().getUser().equals(accountCurrent.getUser())) {
            // Hoặc nếu lịch là của GV mượn phòng thì calendarToChange.getUserIdMp_FK()
            if (calendarToChange.getUser() == null || !calendarToChange.getCreditClass().getUser().equals(accountCurrent.getUser())) {
                return new ResponseFailure(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền yêu cầu thay đổi lịch này.");
            }
        }


        // 3. Lấy thông tin thời gian mới đề xuất
        WeekSemesterEntity newWeekSemester = iWeekSemesterRepository.findById(changeRequestDto.getNewWeekSemesterId())
                .orElseThrow(() -> new CalendarException("Không tìm thấy Tuần-Học kỳ mới với ID: " + changeRequestDto.getNewWeekSemesterId()));
        PracticeCaseEntity newPracticeCaseBegin = iPracticeCaseRepository.findById(changeRequestDto.getNewPracticeCaseBeginId())
                .orElseThrow(() -> new CalendarException("Không tìm thấy Tiết bắt đầu mới với ID: " + changeRequestDto.getNewPracticeCaseBeginId()));
        Long newDay = changeRequestDto.getNewDayId();

        // Kiểm tra tính hợp lệ của khoảng thời gian mới với allCase của lịch gốc
        Long allCaseOriginal = calendarToChange.getAllCase();
        List<PracticeCaseEntity> newPracticeCasesForBooking;
        List<PracticeCaseEntity> potentialNewCases = iPracticeCaseRepository.findPracticeCasesStartingFromId(
                newPracticeCaseBegin.getId(), PageRequest.of(0, allCaseOriginal.intValue()));
        if (potentialNewCases.size() < allCaseOriginal) {
            throw new CalendarException(String.format("Thời gian mới đề xuất không đủ %d tiết liên tục từ tiết '%s'.",
                    allCaseOriginal, newPracticeCaseBegin.getNamePracticeCase()));
        }
        // (Thêm kiểm tra ID liên tục cho potentialNewCases nếu cần)
        newPracticeCasesForBooking = potentialNewCases;


        // 4. Tạo PhieuYeuCau
        TicketRequestEntity ticket = new TicketRequestEntity();
        TypeRequestEntity typeTDL = iTypeRequestRepository.findTypeRequestEntityByNameTypeRequest("TDL"); // Giả sử mã là "TDL"
        if (typeTDL == null) {
            throw new RuntimeException("Không tìm thấy loại yêu cầu 'TDL'.");
        }

        ticket.setTypeRequest(typeTDL);
        ticket.setUser(accountCurrent.getUser()); // Người tạo phiếu
        ticket.setDateRequest(new Date());

        // Lưu thông tin lịch gốc
        ticket.setCalendar(calendarToChange); // LichGocID_FK

        // Lưu thông tin lịch đề xuất
        ticket.setWeekSemester(newWeekSemester); // DeXuat_TuanHoc_KiHoc_Id_FK
        ticket.setDay(newDay);                   // DeXuat_Thu
        ticket.setPracticeCase(newPracticeCaseBegin); // DeXuat_SoTietBD_FK
        ticket.setAllCase(allCaseOriginal);      // DeXuat_SoTiet (giữ nguyên từ lịch gốc)
        ticket.setRoom(calendarToChange.getRoom()); // DeXuat_PhongID_FK (giữ nguyên phòng từ lịch gốc)
        ticket.setNoteTicket(changeRequestDto.getNewPurposeUse()); // DeXuat_GhiChu

        // Thiết lập trạng thái ban đầu của phiếu và luồng duyệt
        // Ví dụ: "Thay đổi lịch" cần Trưởng Khoa duyệt trước
        StatusEntity statusChoTKDuyet = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.WAITING_FACILITIES_APPROVAL.getCode()); // Giả sử có mã trạng thái này
        if (statusChoTKDuyet == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'CHO_TK_DUYET'.");
        }

        StatusEntity statusNotRequired = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.NOT_REQUIRED.getCode()); // Giả sử có mã trạng thái này
        if (statusNotRequired == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'statusNotRequired'.");
        }

        StatusEntity statusPendingApproval = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.PENDING_APPROVAL.getCode()); // Giả sử có mã trạng thái này
        if (statusPendingApproval == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'statusPendingApproval'.");
        }
        ticket.setStatusCSVC(statusNotRequired);
        ticket.setStatusGVU(statusNotRequired);
        ticket.setStatusTK(statusPendingApproval);
        ticket.setStatusTicket(statusChoTKDuyet); // TrangThaiPhieuID_FK
        // Các trường duyệt cụ thể (ví dụ: ticket.setStatusTK(statusChoTKDuyet);)

        iTicketRequestRepository.save(ticket);

        // 5. Tạo thông báo cho Trưởng Khoa (nếu cần)
        // Lấy danh sách Trưởng Khoa hoặc người có quyền duyệt TK
        // UserEntity truongKhoa = ... ; // Logic tìm trưởng khoa
        // NotificationEntity notificationToTK = new NotificationEntity();
        // notificationToTK.setUser(truongKhoa); // Người nhận
        // notificationToTK.setNameNotification("Yêu cầu thay đổi lịch cần duyệt");
        // notificationToTK.setContentNotification("Giảng viên " + userRequesting.getTen() + " đã gửi yêu cầu thay đổi lịch (ID phiếu: " + ticket.getId() + ").");
        // notificationToTK.setDateNotification(new Date());
        // notificationToTK.setTicketRequest(ticket);
        // notificationToTK.setUserGui(userRequesting); // Hệ thống hoặc người tạo phiếu
        // notificationToTK.setStatus(statusRepository.findByMaTrangThai("NOTSEEN").get());
        // iNotificationRepository.save(notificationToTK);

        return new ResponseSuccess<>(HttpStatus.CREATED.value(), "Đã gửi yêu cầu thay đổi lịch thành công. Vui lòng chờ duyệt.", ticket.getId());
    }

    @Override
    @Transactional
    public ResponseData<?> processChangeCalendarTicketApproval(TicketApprovalDto approvalDto) {
        // 1. Lấy thông tin người dùng đang duyệt
        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity account = iAccountRepository.findAccountEntityByEmail(emailCurrentUser)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng duyệt với email: " + emailCurrentUser));

        UserEntity approver = account.getUser();
        String approverRole = approver.getRole().getNameRole(); // Giả sử MaQuyen là "TK", "GVU"

        // 2. Lấy phiếu yêu cầu
        TicketRequestEntity ticket = iTicketRequestRepository.findById(approvalDto.getTicketId())
                .orElseThrow(() -> new CalendarException("Không tìm thấy phiếu yêu cầu với ID: " + approvalDto.getTicketId()));

        // Kiểm tra loại phiếu có phải là "Thay đổi lịch" không
        if (!ticket.getTypeRequest().getNameTypeRequest().equals("TDL")) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Phiếu yêu cầu này không phải là loại 'Thay đổi lịch'.");
        }



        if ( !ticket.getStatusTicket().getNameStatus().equals(StatusEnum.WAITING_DEAN_APPROVAL.getCode())){
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Phiếu này không ở trạng thái chờ TK xử lý.");
        }

        // 3. Lấy các trạng thái cần thiết
        StatusEntity statusApproved = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.APPROVED.getCode()); // Hoặc mã trạng thái "APPROVED" của bạn
        if (statusApproved == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'DA_DUYET'.");
        }


        StatusEntity statusRejected = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.REJECTED.getCode()); // Hoặc mã trạng thái "REJECTED"
        if (statusRejected == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'TU_CHOI'.");
        }



        StatusEntity statusThanhCong = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.PROCESSED_SUCCESSFULLY.getCode());
        if (statusThanhCong == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'HOAN_TAT'.");
        }

        StatusEntity statusActive = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.ACTIVE.getCode());
        if (statusActive == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'ACTIVE'.");
        }

        StatusEntity statusDaHuy = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.OFF.getCode());
        if (statusDaHuy == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'statusDaHuy'.");
        }

        MajorEntity majorOfCreditClass = ticket.getCalendar().getCreditClass().getUser().getMajor();

        // 4. Xử lý dựa trên vai trò người duyệt và trạng thái hiện tại của phiếu
        String notificationTitle = "Phản hồi yêu cầu thay đổi lịch (ID: " + ticket.getId() + ")";
        String notificationContent;

        if (approverRole.equals("TK") && approver.getMajor() == majorOfCreditClass ) { // Giả sử "TK" là mã quyền của Trưởng Khoa
            if (!ticket.getStatusTicket().getNameStatus().equals(StatusEnum.WAITING_DEAN_APPROVAL.getCode())) {
                return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Phiếu này không ở trạng thái chờ Trưởng Khoa duyệt.");
            }
            if (approvalDto.getApprovalStatus().equalsIgnoreCase(StatusEnum.AGREE.getCode())) {

                //set lich
                // Lấy thông tin lịch đề xuất từ phiếu
                CalendarEntity calendarToChange = ticket.getCalendar(); // Lịch gốc
                WeekSemesterEntity newWeekSemester = ticket.getWeekSemester();
                Long newDay = ticket.getDay();
                PracticeCaseEntity newPracticeCaseBegin = ticket.getPracticeCase();
                Long newAllCase = ticket.getAllCase(); // allCase giữ nguyên từ lịch gốc
                RoomEntity roomForNewCalendar = ticket.getRoom(); // Phòng giữ nguyên từ lịch gốc
                String newNote = ticket.getNoteTicket();

                // Kiểm tra phòng có trống cho thời gian mới không (loại trừ lịch gốc)
                boolean roomIsFree = isRoomFreeForEntireDuration(
                        calendarToChange.getId(),
                        roomForNewCalendar,
                        newWeekSemester,
                        newDay,
                        newPracticeCaseBegin,
                        newAllCase,
                        statusActive, // Chỉ check với các lịch active khác
                        iCalendarRepository,
                        iPracticeCaseRepository,
                        null // Không cần tempOccupiedSlots vì đang xử lý 1 phiếu
                );

                if (!roomIsFree) {
//                    // GVU từ chối vì phòng không trống
//                    ticket.setStatusGVU(statusRejected); // Cập nhật trạng thái duyệt của GVU
//                    ticket.setUserGVU(approver);
//                    ticket.setDateCreateGVU(new Date());
//                    // ticket.setGVUXuLy_GhiChu("Phòng đã bị chiếm vào thời gian đề xuất mới.");
//
//                    ticket.setStatusTicket(statusRejected); // Đánh dấu phiếu bị từ chối
//                    iTicketRequestRepository.save(ticket);
//                    notificationContent = "Yêu cầu thay đổi lịch của bạn bị từ chối do phòng đã có lịch khác vào thời gian đề xuất mới.";
//                    // Tạo thông báo cho người gửi
//                    createNotificationForUser(ticket.getUser(), notificationTitle, notificationContent, ticket, approver);
                    return new ResponseFailure(HttpStatus.CONFLICT.value(), "Phòng đã có lịch khác vào thời gian đề xuất mới. Yêu cầu bị từ chối.");
                }

                // Hủy lịch cũ (đặt trạng thái là DA_HUY)
                calendarToChange.setStatus(statusDaHuy);
                iCalendarRepository.save(calendarToChange);

                // Tạo lịch mới
                CalendarEntity newCalendar = new CalendarEntity();
                newCalendar.setCreditClass(calendarToChange.getCreditClass());
                newCalendar.setUser(calendarToChange.getUser()); // Hoặc người yêu cầu nếu là lịch GV mượn
                newCalendar.setGroup(calendarToChange.getGroup()); // Giữ nguyên thông tin nhóm
                newCalendar.setWeekSemester(newWeekSemester);
                newCalendar.setDay(newDay);
                newCalendar.setPracticeCase(newPracticeCaseBegin);
                newCalendar.setAllCase(newAllCase);
                newCalendar.setRoom(roomForNewCalendar);
                newCalendar.setStatus(statusActive); // Lịch mới được active
                newCalendar.setNoteCalendar(newNote);
                iCalendarRepository.save(newCalendar);


                ticket.setStatusTK(statusApproved); // Cập nhật trạng thái duyệt của TK
                ticket.setUserTK(approver);     // Người duyệt TK
                ticket.setDateCreateTK(new Date()); // Thời gian TK duyệt
                // ticket.setTruongKhoaDuyet_GhiChu(approvalDto.getApproverNote()); // Nếu có trường này
                ticket.setStatusTicket(statusThanhCong); // Chuyển trạng thái phiếu cho GVU
                notificationContent = "Yêu cầu thay đổi lịch của bạn đã được Trưởng Khoa duyệt và lịch mới đã được tạo.";
                // TODO: Tạo thông báo cho GVU

            } else if (approvalDto.getApprovalStatus().equalsIgnoreCase(StatusEnum.NOT_AGREE.getCode())) {
                ticket.setStatusTK(statusRejected);
                ticket.setUserTK(approver);
                ticket.setDateCreateTK(new Date());
                // ticket.setTruongKhoaDuyet_GhiChu(approvalDto.getApproverNote());

                ticket.setStatusTicket(statusThanhCong); // Phiếu bị từ chối hoàn toàn
                notificationContent = "Yêu cầu thay đổi lịch của bạn đã bị Trưởng Khoa từ chối. Lý do: " + approvalDto.getApproverNote();

            } else {
                return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Trạng thái duyệt không hợp lệ: " + approvalDto.getApprovalStatus());
            }
        } else {
            return new ResponseFailure(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền duyệt phiếu này hoặc Không phải trưởng khoa của khoa ." + majorOfCreditClass.getContentMajor());
        }

        iTicketRequestRepository.save(ticket);

        // Tạo thông báo cho người gửi phiếu
        createNotificationForUser(ticket.getUser(), notificationTitle, notificationContent, ticket, approver);

        return new ResponseSuccess<>(HttpStatus.OK.value(), "Xử lý phiếu yêu cầu thành công. ");
    }


    // Hàm tiện ích tạo thông báo
    @Transactional
    public void createNotificationForUser(UserEntity recipient, String title, String content, TicketRequestEntity relatedTicket, UserEntity sender) {
        StatusEntity statusNotSeen = iStatusRepository.findStatusEntityByNameStatus("NOTSEEN");
        if (statusNotSeen == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'NOTSEEN'.");
        }
        Optional<NotificationEntity> checkNotification = this.iNotificationRepository.findNotificationEntityByTicketRequestAndUser(relatedTicket, recipient);
        if (checkNotification.isPresent()) {
            NotificationEntity notificationCurrent = checkNotification.get();

        } else {
            try {
                NotificationEntity notification = new NotificationEntity();
                notification.setUser(recipient);
                notification.setNameNotification(title);
                notification.setContentNotification(content);
                notification.setDateNotification(new Date());
                notification.setTicketRequest(relatedTicket);
                notification.setUserGui(sender);
                notification.setStatus(statusNotSeen);
                iNotificationRepository.save(notification);
            } catch (RuntimeException e) {
                System.out.println("--ER  createNotificationForUser " + e.getMessage());
                throw new RuntimeException("createNotificationForUser  error.");
            }
        }
    }


    ////////////////////////////////////////////////////test Room


    @Override
    @Transactional
    public ResponseData<?> createRentRoomTicket(TicketRentRoomRequestDto rentRequestDto) {
        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity account = iAccountRepository.findAccountEntityByEmail(emailCurrentUser)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + emailCurrentUser));
        UserEntity userRequesting = account.getUser();
        WeekSemesterEntity proposedWeek = iWeekSemesterRepository.findById(rentRequestDto.getWeekSemesterId())
                .orElseThrow(() -> new CalendarException("Không tìm thấy Tuần-Học kỳ với ID: " + rentRequestDto.getWeekSemesterId()));
        PracticeCaseEntity proposedPcBegin = iPracticeCaseRepository.findById(rentRequestDto.getPracticeCaseBeginId())
                .orElseThrow(() -> new CalendarException("Không tìm thấy Tiết bắt đầu với ID: " + rentRequestDto.getPracticeCaseBeginId()));
        Long proposedDay = rentRequestDto.getDayId();
        Long proposedAllCase = rentRequestDto.getAllCase();

        if (proposedAllCase <= 0) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Số tiết mượn (allCase) phải lớn hơn 0.");
        }
        StatusEntity statusPendingApproval = this.iStatusRepository.findStatusEntityByNameStatus(StatusEnum.PENDING_APPROVAL.getCode());
        if (statusPendingApproval == null) {
            throw new RuntimeException("Không tìm thấy status statusPendingApproval");
        }
        StatusEntity statusNotRequired = this.iStatusRepository.findStatusEntityByNameStatus(StatusEnum.NOT_REQUIRED.getCode());
        if (statusNotRequired == null) {
            throw new RuntimeException("Không tìm thấy status statusNotRequired");
        }

        StatusEntity statusWaitCSVC = this.iStatusRepository.findStatusEntityByNameStatus(StatusEnum.WAITING_FACILITIES_APPROVAL.getCode());
        if (statusWaitCSVC == null) {
            throw new RuntimeException("Không tìm thấy status statusWaitCSVC");
        }

        StatusEntity statusWaitGVU = this.iStatusRepository.findStatusEntityByNameStatus(StatusEnum.WAITING_REGISTRAR_PROCESSING.getCode());
        if (statusWaitGVU == null) {
            throw new RuntimeException("Không tìm thấy status statusWaitGVU");
        }


        // Kiểm tra tính hợp lệ của khoảng thời gian đề xuất
        List<PracticeCaseEntity> practiceCasesForBooking;
        List<PracticeCaseEntity> potentialCases = iPracticeCaseRepository.findPracticeCasesStartingFromId(
                proposedPcBegin.getId(), PageRequest.of(0, proposedAllCase.intValue()));
        if (potentialCases.size() < proposedAllCase) {
            throw new CalendarException(String.format("Thời gian mượn đề xuất không đủ %d tiết liên tục từ tiết '%s'.",
                    proposedAllCase, proposedPcBegin.getNamePracticeCase()));
        }
        // (Thêm kiểm tra ID liên tục cho potentialCases nếu cần)
        practiceCasesForBooking = potentialCases;


        TicketRequestEntity ticket = new TicketRequestEntity();
        TypeRequestEntity typeMP = iTypeRequestRepository.findTypeRequestEntityByNameTypeRequest("MP"); // Giả sử mã là "MP"
        if (typeMP == null) {
            throw new RuntimeException("Không tìm thấy loại yêu cầu 'MP'.");
        }


        ticket.setTypeRequest(typeMP);
        ticket.setUser(userRequesting);
        ticket.setDateRequest(new Date());

        ticket.setCalendar(null); // LichGocID_FK là NULL

        ticket.setWeekSemester(proposedWeek);    // DeXuat_TuanHoc_KiHoc_Id_FK
        ticket.setDay(proposedDay);              // DeXuat_Thu
        ticket.setPracticeCase(proposedPcBegin); // DeXuat_SoTietBD_FK
        ticket.setAllCase(proposedAllCase);      // DeXuat_SoTiet
        ticket.setRoom(null);                    // DeXuat_PhongID_FK ban đầu NULL, GVU sẽ chọn
        ticket.setStatusTK(statusNotRequired);
        ticket.setStatusCSVC(statusNotRequired);
        ticket.setNoteTicket(rentRequestDto.getPurposeUse()); // DeXuat_GhiChu
        // ticket.setCreditClass(creditClassForRent); // Nếu PhieuYeuCau có trường DeXuat_LopTinChiID_FK
        // Hoặc lưu thông tin này vào GhiChu nếu không có trường riêng
        ticket.setStatusTicket(statusWaitGVU);

        iTicketRequestRepository.save(ticket);

        // TODO: Tạo thông báo cho GVU
        // UserEntity gvuUser = ... ; // Logic tìm GVU
        // createNotificationForUser(gvuUser, "Yêu cầu mượn phòng cần xử lý", ..., ticket, userRequesting);

        return new ResponseSuccess<>(HttpStatus.CREATED.value(), "Đã gửi yêu cầu mượn phòng thành công. Vui lòng chờ GVU xử lý.", ticket.getId());
    }


    @Override
    @Transactional
    public ResponseData<?> processRentRoomTicketApproval(TicketApprovalDto approvalDto) {
        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity account = iAccountRepository.findAccountEntityByEmail(emailCurrentUser)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng duyệt với email: " + emailCurrentUser));
        UserEntity approver = account.getUser();
        String approverRole = approver.getRole().getNameRole();

        TicketRequestEntity ticket = iTicketRequestRepository.findById(approvalDto.getTicketId())
                .orElseThrow(() -> new CalendarException("Không tìm thấy phiếu yêu cầu với ID: " + approvalDto.getTicketId()));

        if (!ticket.getTypeRequest().getNameTypeRequest().equals("MP")) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Phiếu yêu cầu này không phải là loại 'Mượn phòng'.");
        }

        if (!approverRole.equals("GVU")) { // Chỉ GVU được xử lý phiếu mượn phòng
            return new ResponseFailure(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền xử lý phiếu mượn phòng.");
        }

        if (!ticket.getStatusTicket().getNameStatus().equals(StatusEnum.WAITING_REGISTRAR_PROCESSING.getCode())) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Phiếu này không ở trạng thái chờ GVU xử lý.");
        }

        StatusEntity statusApproved = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.APPROVED.getCode()); // Hoặc trạng thái "APPROVED" chung
        if (statusApproved == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'DA_DUYET_GVU'.");
        }
        StatusEntity statusRejected = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.REJECTED.getCode());
        if (statusRejected == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'TU_CHOI_GVU'.");
        }
        StatusEntity statusThanhCong = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.PROCESSED_SUCCESSFULLY.getCode());
        if (statusThanhCong == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'HOAN_TAT'.");
        }

        //Xử dụng để check phòng
        StatusEntity statusActive = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.ACTIVE.getCode());
        if (statusActive == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'statusActiveCalendarRentRoom' cho lịch.");
        }

        String notificationTitle = "Phản hồi yêu cầu mượn phòng (ID: " + ticket.getId() + ")";
        String notificationContent;

        // Nếu không có ID lịch => nó thuộc về quyền CSVC ( chỉ đổi phòng không đổi lịch )

        // Nếu không có ID lịch => nó thuộc về quyền CSVC ( chỉ đổi phòng không đổi lịch )

        if (approvalDto.getApprovalStatus().equalsIgnoreCase(StatusEnum.AGREE.getCode())) {
            WeekSemesterEntity proposedWeek = ticket.getWeekSemester();
            Long proposedDay = ticket.getDay();
            PracticeCaseEntity proposedPcBegin = ticket.getPracticeCase();
            Long proposedAllCase = ticket.getAllCase();

            // Xác định số sinh viên cần cho phòng
            long studentsForRent = 35; // Mặc định là 1 nếu không cho LTC
            CreditClassEntity ltcForRent = null;
            // Cần lấy LopTinChiID_FK từ ticket nếu bạn lưu nó khi tạo phiếu.
            // Giả sử bạn lưu ID LTC vào note hoặc có một trường riêng.
            // Ví dụ: if (ticket.getCreditClassForRent() != null) { ltcForRent = ticket.getCreditClassForRent(); studentsForRent = ltcForRent.getSoLuongSvLTC(); }
            // Để đơn giản, ta giả định nếu là mượn cho mục đích chung của GV thì sĩ số không quá lớn.
            // Hoặc, nếu có thông tin LTC trong ghi chú, bạn có thể phân tích nó.
            // Trong ví dụ này, ta sẽ cố gắng tìm phòng không quá quan tâm sĩ số, hoặc bạn thêm trường sĩ số vào phiếu mượn.
            // Tạm thời lấy sĩ số của người mượn nếu là GV (coi như mượn cho 1 người)
            if (ticket.getUser().getRole().getNameRole().equals("GV") && ticket.getTypeRequest().getNameTypeRequest().equals("MP")) {
                // Nếu có trường lưu trữ sĩ số mong muốn trong PhieuYeuCau.DeXuat_SoLuongSV thì dùng nó.
                // studentsForRent = ticket.getDeXuat_SoLuongSV(); // Ví dụ
            }


            // --- LOGIC CHỌN PHÒNG TỰ ĐỘNG ---
            // (Tương tự như logic trong handleCreateCalendarAuto, nhưng đơn giản hơn vì không có nhiều nhóm/iter)
            RoomEntity selectedRoomForRent = null;
            // Lấy Facility từ đâu? Có thể là facility ngầm định hoặc người dùng chọn khi tạo phiếu MP
            // Giả sử có 1 facility mặc định hoặc logic tìm facility phù hợp
            // FacilityEntity facilityForRent = iFacilityRepository.findById(1L).get(); // Ví dụ
            // List<RoomEntity> allRoomsInFacility = iRoomRepository.findRoomEntitiesByFacility(facilityForRent);
            // HOẶC lấy tất cả các phòng và sắp xếp:
            List<RoomEntity> allRoomsSystem = iRoomRepository.findAll(); // Cẩn thận nếu có nhiều cơ sở

            List<RoomEntity> suitableRooms = new ArrayList<>();
            for (RoomEntity roomCandidate : allRoomsSystem) { // Nên lọc theo cơ sở nếu có
                boolean isFree = isRoomFreeForEntireDuration(
                        null, // Không có lịch gốc để loại trừ
                        roomCandidate,
                        proposedWeek,
                        proposedDay,
                        proposedPcBegin,
                        proposedAllCase,
                        statusActive,
                        iCalendarRepository,
                        iPracticeCaseRepository,
                        null // Không có temp slot ở đây
                );
                if (isFree) {
                    // Có thể thêm kiểm tra số máy hoạt động nếu biết số lượng người mượn
                    if (roomCandidate.getNumberOfComputerActive() >= studentsForRent) { // studentsForRent cần được xác định rõ
                        suitableRooms.add(roomCandidate);
                    } else if (studentsForRent == 0) { // Nếu không quan tâm sĩ số (ví dụ mượn phòng họp)
                        suitableRooms.add(roomCandidate);
                    }
                }
            }

            if (suitableRooms.isEmpty()) {
                ticket.setStatusGVU(statusRejected); // Hoặc một trạng thái riêng cho GVU
                ticket.setUserGVU(approver);
                ticket.setDateCreateGVU(new Date());
                // ticket.setGhiChuXuLyGVU("Không tìm thấy phòng trống phù hợp.");
                ticket.setStatusTicket(statusRejected);
                iTicketRequestRepository.save(ticket);
                notificationContent = "Yêu cầu mượn phòng của bạn bị từ chối do không tìm thấy phòng trống phù hợp vào thời gian yêu cầu.";
                createNotificationForUser(ticket.getUser(), notificationTitle, notificationContent, ticket, approver);
                return new ResponseFailure(HttpStatus.NOT_FOUND.value(), "Không tìm thấy phòng trống phù hợp. Yêu cầu bị từ chối.");
            }

            // Sắp xếp và chọn phòng tốt nhất
            suitableRooms.sort((r1, r2) -> Long.compare(r2.getNumberOfComputerActive(), r1.getNumberOfComputerActive())); // Ưu tiên nhiều máy hơn
            selectedRoomForRent = suitableRooms.get(0);
            // --- KẾT THÚC LOGIC CHỌN PHÒNG ---

            // Cập nhật phòng đã chọn vào phiếu yêu cầu
            ticket.setRoom(selectedRoomForRent); // DeXuat_PhongID_FK được cập nhật

            // Tạo lịch mượn phòng mới
            CalendarEntity newRentedCalendar = new CalendarEntity();
            // newRentedCalendar.setCreditClass(ltcForRent); // Nếu mượn cho LTC
            newRentedCalendar.setUser(ticket.getUser()); // Người mượn
            newRentedCalendar.setWeekSemester(proposedWeek);
            newRentedCalendar.setDay(proposedDay);
            newRentedCalendar.setPracticeCase(proposedPcBegin);
            newRentedCalendar.setAllCase(proposedAllCase);
            newRentedCalendar.setRoom(selectedRoomForRent);
            newRentedCalendar.setStatus(statusActive);
            newRentedCalendar.setNoteCalendar(ticket.getNoteTicket() + " (Mượn phòng - YC ID: " + ticket.getId() + ")");
            iCalendarRepository.save(newRentedCalendar);

            ticket.setStatusGVU(statusApproved);
            ticket.setUserGVU(approver);
            ticket.setDateCreateGVU(new Date());
            ticket.setStatusTicket(statusThanhCong);
            notificationContent = "Yêu cầu mượn phòng của bạn đã được chấp thuận. Phòng được xếp: " + selectedRoomForRent.getNameRoom() + ".";

        } else if (approvalDto.getApprovalStatus().equalsIgnoreCase("REJECTED")) {
            ticket.setStatusGVU(statusRejected);
            ticket.setUserGVU(approver);
            ticket.setDateCreateGVU(new Date());
            // ticket.setGhiChuXuLyGVU(approvalDto.getApproverNote());
            ticket.setStatusTicket(statusThanhCong);
            notificationContent = "Yêu cầu mượn phòng của bạn đã bị Giáo Vụ từ chối. Lý do: " + approvalDto.getApproverNote();
        } else {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Trạng thái duyệt không hợp lệ: " + approvalDto.getApprovalStatus());
        }


        iTicketRequestRepository.save(ticket);
        createNotificationForUser(ticket.getUser(), notificationTitle, notificationContent, ticket, approver);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Xử lý phiếu mượn phòng thành công. " + notificationContent);
    }

    @Transactional
    @Override
    public ResponseData<?> createChangeRoomTicket(TicketChangeRoomRequestDto changeRequestDto) {
        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity account = iAccountRepository.findAccountEntityByEmail(emailCurrentUser)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + emailCurrentUser));
        UserEntity userRequesting = account.getUser();


        CalendarEntity calendarForRent = this.iCalendarRepository.findCalendarEntityById(changeRequestDto.getCalendarId());
        if (calendarForRent == null) {
            throw new RuntimeException("Không tìm thấy lịch id");
        }
        StatusEntity statusPendingApproval = this.iStatusRepository.findStatusEntityByNameStatus(StatusEnum.PENDING_APPROVAL.getCode());
        if (statusPendingApproval == null) {
            throw new RuntimeException("Không tìm thấy status statusPendingApproval");
        }
        StatusEntity statusNotRequired = this.iStatusRepository.findStatusEntityByNameStatus(StatusEnum.NOT_REQUIRED.getCode());
        if (statusNotRequired == null) {
            throw new RuntimeException("Không tìm thấy status statusNotRequired");
        }

        StatusEntity statusWaitCSVC = this.iStatusRepository.findStatusEntityByNameStatus(StatusEnum.WAITING_FACILITIES_APPROVAL.getCode());
        if (statusWaitCSVC == null) {
            throw new RuntimeException("Không tìm thấy status statusWaitCSVC");
        }
        TypeRequestEntity typeTDP = iTypeRequestRepository.findTypeRequestEntityByNameTypeRequest("TDP"); // Giả sử mã là "MP"
        if (typeTDP == null) {
            throw new RuntimeException("Không tìm thấy loại yêu cầu 'TDP'.");
        }

        TicketRequestEntity ticket = new TicketRequestEntity();
        ticket.setTypeRequest(typeTDP);
        ticket.setCalendar(calendarForRent);
        ticket.setStatusGVU(statusNotRequired);
        ticket.setStatusTK(statusNotRequired);
        ticket.setStatusCSVC(statusPendingApproval);
        ticket.setNoteTicket(changeRequestDto.getPurposeUse()); // DeXuat_GhiChu
        // ticket.setCreditClass(creditClassForRent); // Nếu PhieuYeuCau có trường DeXuat_LopTinChiID_FK
        // Hoặc lưu thông tin này vào GhiChu nếu không có trường riêng
        ticket.setStatusTicket(statusWaitCSVC);

        return new ResponseSuccess<>(HttpStatus.CREATED.value(), "Đã gửi yêu cầu thay phòng thành công. Vui lòng chờ CSVC xử lý.", ticket.getId());
    }


    @Transactional
    @Override
    public ResponseData<?> processChangeRoomTicketApproval(TicketApprovalDto approvalDto) {
        String emailCurrentUser = SecurityUtils.getPrincipal();
        AccountEntity account = iAccountRepository.findAccountEntityByEmail(emailCurrentUser)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng duyệt với email: " + emailCurrentUser));
        UserEntity approver = account.getUser();
        String approverRole = approver.getRole().getNameRole();


        TicketRequestEntity ticket = iTicketRequestRepository.findById(approvalDto.getTicketId())
                .orElseThrow(() -> new CalendarException("Không tìm thấy phiếu yêu cầu với ID: " + approvalDto.getTicketId()));
        CalendarEntity calendarForChange = ticket.getCalendar();
        if (calendarForChange == null ){
            throw new RuntimeException("Không tìm thấy lịch id");
        }
        if (!ticket.getTypeRequest().getNameTypeRequest().equals("TDP")) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Phiếu yêu cầu này không phải là loại 'Thay đổi phòng'.");
        }

        if (!approverRole.equals("CSVC")) { // Chỉ CSVC được xử lý phiếu thay phòng
            return new ResponseFailure(HttpStatus.FORBIDDEN.value(), "Bạn không có quyền xử lý phiếu mượn phòng.");
        }

        if (!ticket.getStatusTicket().getNameStatus().equals(StatusEnum.WAITING_FACILITIES_APPROVAL.getCode())) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Phiếu này không ở trạng thái chờ CSVC xử lý.");
        }

        StatusEntity statusApproved = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.APPROVED.getCode()); // Hoặc trạng thái "APPROVED" chung
        if (statusApproved == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'DA_DUYET_GVU'.");
        }
        StatusEntity statusRejected = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.REJECTED.getCode());
        if (statusRejected == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'TU_CHOI_GVU'.");
        }
        StatusEntity statusThanhCong = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.PROCESSED_SUCCESSFULLY.getCode());
        if (statusThanhCong == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'HOAN_TAT'.");
        }

        //Xử dụng để check phòng
        StatusEntity statusActive = iStatusRepository.findStatusEntityByNameStatus(StatusEnum.ACTIVE.getCode());
        if (statusActive == null) {
            throw new RuntimeException("Không tìm thấy trạng thái 'statusActiveCalendarRentRoom' cho lịch.");
        }

        String notificationTitle = "Phản hồi yêu cầu thay phòng (ID: " + ticket.getId() + ")";
        String notificationContent;

        // Nếu không có ID lịch => nó thuộc về quyền CSVC ( chỉ đổi phòng không đổi lịch )

        // Nếu không có ID lịch => nó thuộc về quyền CSVC ( chỉ đổi phòng không đổi lịch )

        if (approvalDto.getApprovalStatus().equalsIgnoreCase(StatusEnum.AGREE.getCode())) {
            WeekSemesterEntity proposedWeek = calendarForChange.getWeekSemester();
            Long proposedDay = calendarForChange.getDay();
            PracticeCaseEntity proposedPcBegin = calendarForChange.getPracticeCase();
            Long proposedAllCase = calendarForChange.getAllCase();





            // --- LOGIC CHỌN PHÒNG TỰ ĐỘNG ---
            // (Tương tự như logic trong handleCreateCalendarAuto, nhưng đơn giản hơn vì không có nhiều nhóm/iter)
            RoomEntity selectedRoomForRent = null;
            // Lấy Facility từ đâu? Có thể là facility ngầm định hoặc người dùng chọn khi tạo phiếu MP
            // Giả sử có 1 facility mặc định hoặc logic tìm facility phù hợp
            // FacilityEntity facilityForRent = iFacilityRepository.findById(1L).get(); // Ví dụ
            // List<RoomEntity> allRoomsInFacility = iRoomRepository.findRoomEntitiesByFacility(facilityForRent);
            // HOẶC lấy tất cả các phòng và sắp xếp:
            List<RoomEntity> allRoomsSystem = iRoomRepository.findAll(); // Cẩn thận nếu có nhiều cơ sở

            List<RoomEntity> suitableRooms = new ArrayList<>();
            for (RoomEntity roomCandidate : allRoomsSystem) { // Nên lọc theo cơ sở nếu có
                boolean isFree = isRoomFreeForEntireDuration(
                        null, // Không có lịch gốc để loại trừ
                        roomCandidate,
                        proposedWeek,
                        proposedDay,
                        proposedPcBegin,
                        proposedAllCase,
                        statusActive,
                        iCalendarRepository,
                        iPracticeCaseRepository,
                        null // Không có temp slot ở đây
                );
                if (isFree) {
                    // Có thể thêm kiểm tra số máy hoạt động nếu biết số lượng người mượn
                    if (roomCandidate.getNumberOfComputerActive() == 35) { // studentsForRent cần được xác định rõ
                        suitableRooms.add(roomCandidate);
                    } else  { // Nếu không quan tâm sĩ số (ví dụ mượn phòng họp)
                        suitableRooms.add(roomCandidate);
                    }
                }
            }

            if (suitableRooms.isEmpty()) {

                return new ResponseFailure(HttpStatus.NOT_FOUND.value(), "Không tìm thấy phòng trống phù hợp");
            }

            // Sắp xếp và chọn phòng tốt nhất
            suitableRooms.sort((r1, r2) -> Long.compare(r2.getNumberOfComputerActive(), r1.getNumberOfComputerActive())); // Ưu tiên nhiều máy hơn
            selectedRoomForRent = suitableRooms.get(0);
            // --- KẾT THÚC LOGIC CHỌN PHÒNG ---

            // Cập nhật phòng đã chọn vào phiếu yêu cầu
            ticket.setRoom(selectedRoomForRent); // DeXuat_PhongID_FK được cập nhật

            // update phòng cho lịch cũ

            // newRentedCalendar.setCreditClass(ltcForRent); // Nếu mượn cho LTC

            calendarForChange.setRoom(selectedRoomForRent);
            calendarForChange.setNoteCalendar(ticket.getNoteTicket() + " (Thay phòng - YC ID: " + ticket.getId() + ")");
            iCalendarRepository.save(calendarForChange);

            ticket.setStatusCSVC(statusApproved);
            ticket.setUserCSVC(approver);
            ticket.setDateCreateCSVC(new Date());
            ticket.setStatusTicket(statusThanhCong);
            notificationContent = "Yêu cầu thay phòng của bạn đã được chấp thuận. Phòng được xếp: " + selectedRoomForRent.getNameRoom() + ".";

        } else if (approvalDto.getApprovalStatus().equalsIgnoreCase("REJECTED")) {
            ticket.setStatusCSVC(statusRejected);
            ticket.setUserCSVC(approver);
            ticket.setDateCreateCSVC(new Date());
            // ticket.setGhiChuXuLyGVU(approvalDto.getApproverNote());
            ticket.setStatusTicket(statusThanhCong);
            notificationContent = "Yêu cầu thay phòng của bạn đã bị Nhân viên cơ sở vật chất từ chối. Lý do: " + approvalDto.getApproverNote();
        } else {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Trạng thái duyệt không hợp lệ: " + approvalDto.getApprovalStatus());
        }


        iTicketRequestRepository.save(ticket);
        createNotificationForUser(ticket.getUser(), notificationTitle, notificationContent, ticket, approver);
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Xử lý Thay đổi phòng thành công. " + notificationContent);
    }


}
