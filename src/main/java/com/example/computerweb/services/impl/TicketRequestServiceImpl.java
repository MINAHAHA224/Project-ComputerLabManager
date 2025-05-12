package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseDto;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseFields;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseOneDto;
import com.example.computerweb.DTO.dto.notificationResponse.NotificationDetailResponseDto;
import com.example.computerweb.DTO.dto.notificationResponse.NotificationResponseDto;
import com.example.computerweb.DTO.dto.requestTicketResponse.RequestTicketResponseDto;
import com.example.computerweb.DTO.dto.ticketResponse.TicketResponseMgmDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketChangeDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketManagementRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRentDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRequestOneDto;
import com.example.computerweb.exceptions.CalendarException;
import com.example.computerweb.models.entity.*;
import com.example.computerweb.repositories.*;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.services.ITicketRequestService;
import com.example.computerweb.utils.DateUtils;
import com.example.computerweb.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
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
    private  final IRoomRepository iRoomRepository;
    private final ITypeRequestRepository iTypeRequestRepository;
    private final IClassroomRepository iClassroomRepository;
    private  final ISubjectRepository iSubjectRepository;
    private final  IAccountRepository iAccountRepository;


    @Override
    public List<TicketRequestOneDto> handleGetAllDataForRqManagementPage() {
        StatusEntity statusPending = this.iStatusRepository.findStatusEntityById(1L);
        StatusEntity statusApproval = this.iStatusRepository.findStatusEntityById(2L);
        List<TicketRequestOneDto> data = new ArrayList<>();

        String email = SecurityUtils.getPrincipal();
        AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(email).get();
        RoleEntity roleUser = account.getUser().getRole();
        String nameRole = roleUser.getNameRole();
        if (nameRole.equals("GVU")){
            List<TicketRequestEntity> allTicket = this.iTicketRequestRepository.findAllByStatusCSVC(statusApproval);
            for ( TicketRequestEntity ticket : allTicket){
                TicketRequestOneDto ticketRequestOneDto = new TicketRequestOneDto();
                ticketRequestOneDto.setRequestId(ticket.getId().toString());
                ticketRequestOneDto.setTypeRequestId(ticket.getTypeRequest().getNameTypeRequest());
                ticketRequestOneDto.setDateRequest(DateUtils.convertToString(ticket.getDateRequest()));
                ticketRequestOneDto.setUserRequest(ticket.getUser().getFirstName() + ticket.getUser().getLastName() );
                ticketRequestOneDto.setStatus(ticket.getStatusTicket().getNameStatus());
                data.add(ticketRequestOneDto);
            }
        }else if (nameRole.equals("CSVC")){

            data = this.iTicketRequestRepository.findListTicketRequestForCSVC();

        }

        return data;
    }

    @Override
    public TicketResponseMgmDto handleGetDetailRequest(Long ticketId) {
        TicketRequestEntity ticketRequest = this.iTicketRequestRepository.getTicketRequestEntityById(ticketId);
        TicketResponseMgmDto ticket = new TicketResponseMgmDto();
        ticket.setRequestId(ticketRequest.getId().toString());
        ticket.setTypeRequest(ticketRequest.getTypeRequest().getNameTypeRequest());
        ticket.setDateRequest(DateUtils.dateTimeConvertToString(ticketRequest.getDateRequest()));
        ticket.setUserRequest(ticketRequest.getUser().getFirstName()+" "+ticketRequest.getUser().getLastName());

        StatusEntity doneCSVC = ticketRequest.getStatusCSVC();
        Date dateCreateCSVC = ticketRequest.getDateCreateCSVC();
        UserEntity userCSVC = ticketRequest.getUserCSVC();

        StatusEntity doneGVU = ticketRequest.getStatusGVU();
        Date dateCreateGVU = ticketRequest.getDateCreateGVU();
        UserEntity userGVU = ticketRequest.getUserGVU();

        ticket.setDoneCSVC(doneCSVC != null ? doneCSVC.getNameStatus() : null);
        ticket.setCreated_CSVC(dateCreateCSVC!=null ? DateUtils.convertToString(dateCreateCSVC) : null);
        ticket.setModified_CSVC(userCSVC!= null ? userCSVC.getFirstName()+" " + userCSVC.getLastName(): null);

        ticket.setDoneGVU(doneGVU != null ? doneGVU.getNameStatus() : null);
        ticket.setCreated_GVU(dateCreateGVU!=null ? DateUtils.convertToString(dateCreateGVU) : null);
        ticket.setModified_GVU(userGVU!= null ? userGVU.getFirstName()+" " + userGVU.getLastName(): null);



        String  nameTypeRequest = ticketRequest.getTypeRequest().getNameTypeRequest();


        if (nameTypeRequest.equals("TDL")  ){
            CalendarEntity calendarOld = ticketRequest.getCalendar();
            WeekSemesterEntity weekSemesterEntityOld = calendarOld.getWeekSemester();
            ticket.setWeekSemesterOld("Tuần: "+weekSemesterEntityOld.getWeekStudy() + "[Từ " + DateUtils.convertToString(weekSemesterEntityOld.getDateBegin()) + " Đến " + DateUtils.convertToString(weekSemesterEntityOld.getDateEnd()) + "]");
            ticket.setDayOld(calendarOld.getDay().toString());
            ticket.setPracticeCaseBeginOld(calendarOld.getPracticeCase().getNamePracticeCase());
            ticket.setAllCaseOld(calendarOld.getAllCase().toString());
            ticket.setRoomOld(calendarOld.getRoom().getNameRoom());
            ticket.setNoteOld(calendarOld.getNoteCalendar());

            // new use data on ticket
            WeekSemesterEntity weekSemesterEntityNew = ticketRequest.getWeekSemester();
            ticket.setWeekSemesterNew("Tuần : " +weekSemesterEntityNew.getWeekStudy() + "[Từ " + DateUtils.convertToString(weekSemesterEntityNew.getDateBegin()) + " Đến " + DateUtils.convertToString(weekSemesterEntityNew.getDateEnd()) + "]");
            ticket.setDayNew(ticketRequest.getDay().toString());
            ticket.setPracticeCaseBeginNew(ticketRequest.getPracticeCase().getNamePracticeCase());
            ticket.setAllCaseNew(ticketRequest.getAllCase().toString());
            ticket.setRoomNew(ticketRequest.getRoom().getNameRoom());
            ticket.setNoteNew(ticketRequest.getNoteTicket());


        }else if ( nameTypeRequest.equals("MP")) {
            // rent room
            WeekSemesterEntity weekSemesterEntityNew = ticketRequest.getWeekSemester();
            ticket.setWeekSemesterNew("Tuần : "+weekSemesterEntityNew.getWeekStudy()+ "[Từ " + DateUtils.convertToString(weekSemesterEntityNew.getDateBegin()) + " Đến " + DateUtils.convertToString(weekSemesterEntityNew.getDateEnd()) + "]");
            ticket.setDayNew(ticketRequest.getDay().toString());
            ticket.setPracticeCaseBeginNew(ticketRequest.getPracticeCase().getNamePracticeCase());
            ticket.setAllCaseNew(ticketRequest.getAllCase().toString());
            ticket.setRoomNew(ticketRequest.getRoom().getNameRoom());
            ticket.setNoteNew(ticketRequest.getNoteTicket());
        }
        else if ( nameTypeRequest.equals("HUY")){
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
    public ResponseEntity<String> HandleTicketRequest(TicketManagementRequestDto ticketManagementRequestDto) {
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
                }
                this.iTicketRequestRepository.save(ticketRequestEntity);

                NotificationEntity notificationEntity = new NotificationEntity();
                notificationEntity.setUser(teacher);
                notificationEntity.setNameNotification("Thay đổi lịch hoặc thay đổi phòng");
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
                    notificationEntity.setNameNotification("Rent room");
                    notificationEntity.setContentNotification(ticketManagementRequestDto.getNoteNotification());
                    notificationEntity.setDateNotification(new Date()); // xử lí date and time
                    notificationEntity.setTicketRequest(ticketRequestEntity);
                    notificationEntity.setUserGui(userCurrent);
                    notificationEntity.setStatus(statusNotSeen);
                    this.iNotificationRepository.save(notificationEntity);

                    return ResponseEntity.ok().body("Lịch mượn phòng đã tạo thành công");
                }
                else if (ticketRequestEntity.getTypeRequest() == typeRequestHUY){
                    try {
                        CalendarEntity calendarEntity = ticketRequestEntity.getCalendar();
                        this.iCalendarRepository.deleteById(calendarEntity.getId());
                    } catch (Exception e) {
                        System.out.println("--ER : error Huy calendar" + e.getMessage());
                        e.printStackTrace();
                        throw  e;
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
                    notificationEntity.setNameNotification("Hủy phòng");
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

                    boolean checkRequestMp = this.iCalendarRepository.existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus(weekSemesterNew, dayNew, practiceCaseBeginNew,roomNew , status);
                    if (!checkRequestMp){
                        ticketRequestEntity.setStatusCSVC(statusApproval);
                        ticketRequestEntity.setDateCreateCSVC(new Date());
                        ticketRequestEntity.setUserCSVC(userCurrent);
                        this.iTicketRequestRepository.save(ticketRequestEntity);
                    }else {
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
        }catch (Exception e ){
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
        calendarCurrent.setCreditClassId(calendarEntity.getCreditClass() !=null ? calendarEntity.getCreditClass().getId().toString() : null);
        calendarCurrent.setUserIdMp_Fk(calendarEntity.getUser()!=null ? calendarEntity.getUser().getFirstName()+" " +calendarEntity.getUser().getLastName() : null);
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
            PracticeCaseEntity caseBegin = this.iPracticeCaseRepository.findPracticeCaseEntityById(ticketChangeDto.getCaseBeginId());
            Long allCase = ticketChangeDto.getAllCase();
            RoomEntity room = this.iRoomRepository.findRoomEntityById(ticketChangeDto.getRoomId());
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
            newTicket.setAllCase(allCase);
            newTicket.setRoom(room);
            newTicket.setNoteTicket(note);
            newTicket.setStatusTicket(status);
            newTicket.setStatusCSVC(status);
            newTicket.setStatusGVU(status);

            this.iTicketRequestRepository.save(newTicket);
            return ResponseEntity.ok().body("Yêu cầu thay đổi lịch thành công");
        }catch (Exception e){
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
        }catch (Exception e){
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
    public  List<NotificationResponseDto> handleGetAllNotificationOfUser() {
        String emailUser = SecurityUtils.getPrincipal();
        AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(emailUser).get();
        UserEntity user = account.getUser();
        List<NotificationEntity> allNotification = this.iNotificationRepository.findAllByUser(user);
        List<NotificationResponseDto> noteResponses = new ArrayList<>();
        for (NotificationEntity note :allNotification ){
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
            noteResponseDto.setUserSent(userGui.getFirstName() + " " +userGui.getLastName() );
            noteResponseDto.setDepartment(department);
            noteResponseDto.setNameNotification(notification.getNameNotification());
            noteResponseDto.setContentNotification(notification.getContentNotification());
            noteResponseDto.setDateNotification(DateUtils.dateTimeConvertToString(notification.getDateNotification()));
            noteResponseDto.setStatus(status.getNameStatus());
        }catch (Exception e ){
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
            listId  = Arrays.asList(noteId.split(","));
        }else {
            listId.add(noteId);
        }
        try {
            for ( String id  : listId ){
                this.iNotificationRepository.deleteById(Long.valueOf(id));
            }
        }catch (Exception e){
            System.out.println("--ER error delete notification" + e.getMessage());
            e.printStackTrace();

        }

        return ResponseEntity.ok().body("Xóa thông báo thành công");
    }

    @Override
    public List<RequestTicketResponseDto> handleGetAllRequestTicketGV() {
        String emailUser = SecurityUtils.getPrincipal();
        AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(emailUser).get();
        UserEntity user = account.getUser();
        List<TicketRequestEntity> listTicketRequest = this.iTicketRequestRepository.findAllByUser(user);
        List<RequestTicketResponseDto> requestTkResponses = new ArrayList<>();
        for (TicketRequestEntity ticketRequest :  listTicketRequest ){
            RequestTicketResponseDto requestTkResponse = new RequestTicketResponseDto();
            requestTkResponse.setRequestTicketId(ticketRequest.getId().toString());
            requestTkResponse.setTypeRequest(ticketRequest.getDateRequest().toString());
            requestTkResponse.setUserSent(ticketRequest.getUser().getFirstName() + " " + ticketRequest.getUser().getLastName());
            requestTkResponse.setTypeRequest(ticketRequest.getTypeRequest().getNameTypeRequest());
            requestTkResponse.setDateSent(DateUtils.convertToString(ticketRequest.getDateRequest()) );
            requestTkResponse.setStatusCSVC(ticketRequest.getStatusCSVC().getNameStatus());
            requestTkResponse.setStatusGVU(ticketRequest.getStatusGVU().getNameStatus());
            requestTkResponses.add(requestTkResponse);
        }
        return requestTkResponses;
    }

    @Override
    public TicketResponseMgmDto handleGetRequestTicketGV(Long ticketId) {
        String email = SecurityUtils.getPrincipal();
        AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(email).get();
        UserEntity teacher = account.getUser();
        TicketRequestEntity ticketRequest = this.iTicketRequestRepository.getTicketRequestEntityById(ticketId);
        TicketResponseMgmDto ticket = new TicketResponseMgmDto();
        ticket.setRequestId(ticketRequest.getId().toString());
        ticket.setTypeRequest(ticketRequest.getTypeRequest().getNameTypeRequest());
        ticket.setDateRequest(DateUtils.dateTimeConvertToString(ticketRequest.getDateRequest()));
        ticket.setUserRequest(teacher.getFirstName()+" "+teacher.getLastName());

        StatusEntity doneCSVC = ticketRequest.getStatusCSVC();
        Date dateCreateCSVC = ticketRequest.getDateCreateCSVC();
        UserEntity userCSVC = ticketRequest.getUserCSVC();

        StatusEntity doneGVU = ticketRequest.getStatusGVU();
        Date dateCreateGVU = ticketRequest.getDateCreateGVU();
        UserEntity userGVU = ticketRequest.getUserGVU();

        ticket.setDoneCSVC(doneCSVC != null ? doneCSVC.getNameStatus() : null);
        ticket.setCreated_CSVC(dateCreateCSVC!=null ? DateUtils.convertToString(dateCreateCSVC) : null);
        ticket.setModified_CSVC(userCSVC!= null ? userCSVC.getFirstName()+" " + userCSVC.getLastName(): null);

        ticket.setDoneGVU(doneGVU != null ? doneGVU.getNameStatus() : null);
        ticket.setCreated_GVU(dateCreateGVU!=null ? DateUtils.convertToString(dateCreateGVU) : null);
        ticket.setModified_GVU(userGVU!= null ? userGVU.getFirstName()+" " + userGVU.getLastName(): null);

        String  nameTypeRequest = ticketRequest.getTypeRequest().getNameTypeRequest();


        if (nameTypeRequest.equals("TDL")  ){
            CalendarEntity calendarOld = ticketRequest.getCalendar();
            WeekSemesterEntity weekSemesterEntityOld = calendarOld.getWeekSemester();
            ticket.setWeekSemesterOld( "Tuần : " +weekSemesterEntityOld.getWeekStudy() + "[Từ " + DateUtils.convertToString(weekSemesterEntityOld.getDateBegin()) + " to " + DateUtils.convertToString(weekSemesterEntityOld.getDateEnd()) + "]");
            ticket.setDayOld(calendarOld.getDay().toString());
            ticket.setPracticeCaseBeginOld(calendarOld.getPracticeCase().getNamePracticeCase());
            ticket.setAllCaseOld(calendarOld.getAllCase().toString());
            ticket.setRoomOld(calendarOld.getRoom().getNameRoom());
            ticket.setNoteOld(calendarOld.getNoteCalendar());

            // new use data on ticket
            WeekSemesterEntity weekSemesterEntityNew = ticketRequest.getWeekSemester();
            ticket.setWeekSemesterNew("Tuần : "+weekSemesterEntityNew.getWeekStudy() + "[Từ " + DateUtils.convertToString(weekSemesterEntityNew.getDateBegin()) + " Đến " + DateUtils.convertToString(weekSemesterEntityNew.getDateEnd()) + "]");
            ticket.setDayNew(ticketRequest.getDay().toString());
            ticket.setPracticeCaseBeginNew(ticketRequest.getPracticeCase().getNamePracticeCase());
            ticket.setAllCaseNew(ticketRequest.getAllCase().toString());
            ticket.setRoomNew(ticketRequest.getRoom().getNameRoom());
            ticket.setNoteNew(ticketRequest.getNoteTicket());


        }else if ( nameTypeRequest.equals("MP")) {
            // rent room
            WeekSemesterEntity weekSemesterEntityNew = ticketRequest.getWeekSemester();
            ticket.setWeekSemesterNew("Tuần : "+weekSemesterEntityNew.getWeekStudy() + "[Từ " + DateUtils.convertToString(weekSemesterEntityNew.getDateBegin()) + " Đến " + DateUtils.convertToString(weekSemesterEntityNew.getDateEnd()) + "]");
            ticket.setDayNew(ticketRequest.getDay().toString());
            ticket.setPracticeCaseBeginNew(ticketRequest.getPracticeCase().getNamePracticeCase());
            ticket.setAllCaseNew(ticketRequest.getAllCase().toString());
            ticket.setRoomNew(ticketRequest.getRoom().getNameRoom());
            ticket.setNoteNew(ticketRequest.getNoteTicket());
        }else if( nameTypeRequest.equals("HUY")){

            if (ticketRequest.getCalendar() != null ){
                CalendarEntity calendarOld = ticketRequest.getCalendar() ;
                WeekSemesterEntity weekSemesterEntityOld = calendarOld.getWeekSemester();
                ticket.setWeekSemesterOld("Tuần : "+weekSemesterEntityOld.getWeekStudy() + "[Từ " + DateUtils.convertToString(weekSemesterEntityOld.getDateBegin()) + " Đến " + DateUtils.convertToString(weekSemesterEntityOld.getDateEnd()) + "]");
                ticket.setDayOld(calendarOld.getDay().toString());
                ticket.setPracticeCaseBeginOld(calendarOld.getPracticeCase().getNamePracticeCase());
                ticket.setAllCaseOld(calendarOld.getAllCase().toString());
                ticket.setRoomOld(calendarOld.getRoom().getNameRoom());
                ticket.setNoteOld(calendarOld.getNoteCalendar());
            }

        }
        return ticket;
    }

    @Override
    @Transactional
    public ResponseEntity<String> handleDeleteOneOrMoreTicketRequest(String requestTicketId) {
        List<String> idTickets = new ArrayList<>();
        if (requestTicketId.contains(",")) {
            idTickets = Arrays.asList(requestTicketId.split(","));
        }else {
            idTickets.add(requestTicketId);
        }

        try {
            for (String  idTicket : idTickets ){
                this.iTicketRequestRepository.deleteById(Long.valueOf(idTicket));
            }
        }catch (Exception e){
            System.out.println("--ER error delete ticketRequest " + e.getMessage());
            e.printStackTrace();
        }


        return ResponseEntity.ok().body("Xóa phiếu yêu cầu thành công");
    }
}
