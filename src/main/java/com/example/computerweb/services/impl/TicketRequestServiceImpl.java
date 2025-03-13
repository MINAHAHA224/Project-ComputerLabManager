package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.*;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketChangeDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketManagementRequestDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketRentDto;
import com.example.computerweb.models.entity.*;
import com.example.computerweb.models.enums.PurposeUse;
import com.example.computerweb.repositories.*;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.services.ITicketRequestService;
import com.example.computerweb.utils.DateUtils;
import com.example.computerweb.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketRequestServiceImpl implements ITicketRequestService {
    private final ICalendarService iCalendarService;
    private final ITicketRequestRepository iTicketRequestRepository;
    private final IStatusRepository iStatusRepository;
    private final INotificationRepository iNotificationRepository;
    private final IUserRepository iUserRepository;
    private final ICalendarRepository iCalendarRepository;
    private final IPracticeCaseRepository iPracticeCaseRepository;
    private final IRoomRepository iRoomRepository;
    private final ITypeRequestRepository iTypeRequestRepository;
    private final IClassroomRepository iClassroomRepository;
    private  final ISubjectRepository iSubjectRepository;
    @Override
    public List<TicketResponseMgmDto> handleGetAllDataForRqManagementPage() {
        return this.iTicketRequestRepository.getAllDataRequestManagement();
    }

    @Override
    @Transactional
    public ResponseEntity<String> HandleTicketRequest(TicketManagementRequestDto ticketManagementRequestDto) {
        TicketRequestEntity ticketRequestEntity = this.iTicketRequestRepository.getTicketRequestEntityById(ticketManagementRequestDto.getTicketId());
        String noteCalendarCurrent = null;
        UserEntity userEntity = this.iUserRepository.findUserEntityByEmail(SecurityUtils.getPrincipal()).get();
        String nameRole = userEntity.getRole().getNameRole();
        StatusEntity statusReject = this.iStatusRepository.findStatusEntityByNameStatus("REJECT");
        StatusEntity statusApproval = this.iStatusRepository.findStatusEntityByNameStatus("APPROVAL");
        if (ticketRequestEntity.getCalendarId() != null) {
            CalendarEntity calendarCurrent = this.iCalendarRepository.findCalendarEntityById(ticketRequestEntity.getCalendarId());
            noteCalendarCurrent = calendarCurrent.getNoteCalendar();
        }
        if (ticketManagementRequestDto.getStatus().equals("REJECT")) {
            try {

                UserEntity teacher = ticketRequestEntity.getUser();
                // save ticket


                if (nameRole.equals("GVU")) {
                    ticketRequestEntity.setDoneGVU(statusReject);
                    ticketRequestEntity.setStatus(statusReject);
                } else if (nameRole.equals("CSVC")) {
                    ticketRequestEntity.setDoneCSVC(statusReject);
                    ticketRequestEntity.setStatus(statusReject);
                }

                this.iTicketRequestRepository.save(ticketRequestEntity);
                // save notification
                NotificationEntity notificationEntity = new NotificationEntity();
                notificationEntity.setUser(teacher);
                notificationEntity.setNameNotification("Reject");
                notificationEntity.setContentNotification(ticketManagementRequestDto.getNoteNotification());
                notificationEntity.setDateNotification(ticketManagementRequestDto.getDate()); // xử lí date and time
                notificationEntity.setStatus(this.iStatusRepository.findStatusEntityByNameStatus("NOTSEEN"));
                this.iNotificationRepository.save(notificationEntity);
            } catch (Exception e) {
                System.out.println("--ER : error save ticketRequest" + e.getMessage());
                e.printStackTrace();
            }
            return ResponseEntity.ok().body("Ticket reject had announcement for teacher ");


        } else if (ticketManagementRequestDto.getStatus().equals("APPROVAL")) {
            TypeRequestEntity typeRequestMp = this.iTypeRequestRepository.findTypeRequestEntityByNameTypeRequest("MP");
            TypeRequestEntity typeRequestTdl = this.iTypeRequestRepository.findTypeRequestEntityByNameTypeRequest("TDL");

            if (nameRole.equals("GVU")) {

                if (ticketRequestEntity.getTypeRequest() == typeRequestMp) {
                    Date dateOfCalendar = ticketRequestEntity.getDateNew();
                    UserEntity teacher = ticketRequestEntity.getUser();
                    PracticeCaseEntity practiceCaseEntity = this.iPracticeCaseRepository.findPracticeCaseEntityById(Long.valueOf(ticketRequestEntity.getPracticeCaseNew()));
                    boolean checkFirst = this.iCalendarRepository.existsByDateOfCalendarAndUserAndPracticeCase(dateOfCalendar, teacher, practiceCaseEntity);


                    try {
                        // check first

                        if (!checkFirst) {
                            CalendarEntity calendarEntity = new CalendarEntity();
                            calendarEntity.setNoteCalendar(PurposeUse.LICH_MUON_PHONG.toString());
                            calendarEntity.setDateOfCalendar(dateOfCalendar);
                            calendarEntity.setUser(teacher);
                            calendarEntity.setPracticeCase(practiceCaseEntity);
                            calendarEntity.setClassroom(ticketRequestEntity.getClassroomEntity());
                            calendarEntity.setSubject(ticketRequestEntity.getSubject());

                            List<Long> roomIds = new ArrayList<>();

                            if (ticketRequestEntity.getRoomNew().contains(",")) {
                                String[] loopRooms = ticketRequestEntity.getRoomNew().split(",");
                                for (String loopRoom : loopRooms) {
                                    roomIds.add(Long.valueOf(loopRoom));
                                }
                            } else {
                                roomIds.add(Long.valueOf(ticketRequestEntity.getRoomNew()));
                            }
                            // check second
                            ResponseEntity<String> checkSecond = this.iCalendarService.handleCheckExistCalendar1E(dateOfCalendar, practiceCaseEntity, roomIds);
                            if (checkSecond.getStatusCode() == HttpStatus.OK) {
                                CalendarEntity newCalendar = this.iCalendarRepository.save(calendarEntity);
                                // save idCalendar with room ( List<Long> idRoom )
                                this.iRoomRepository.saveLTH_Phong(newCalendar.getId(), roomIds);

                            } else {
                                return ResponseEntity.badRequest().body(checkSecond.getBody());
                            }
                        } else {
                            return ResponseEntity.badRequest().body("Existed a calendar with Date : " +
                                    dateOfCalendar.toString() + " , PracticeCase : " +
                                    practiceCaseEntity.getNamePracticeCase() + " And teacher : " +
                                    userEntity.getFirstName() + " " + userEntity.getLastName());
                        }

                    } catch (Exception e) {
                        System.out.println("--ER : error save calendar practiceCase" + e.getMessage());
                        e.printStackTrace();
                    }

                    // save ticket
                    StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("APPROVAL");
                    ticketRequestEntity.setDoneGVU(status);
                    ticketRequestEntity.setStatus(status);
                    this.iTicketRequestRepository.save(ticketRequestEntity);
                    // done
                    return ResponseEntity.ok().body("Calendar rent room had created success ");

                }
                // type : change calendar ( room , calendar )
                else if (ticketRequestEntity.getTypeRequest() == typeRequestTdl) {
                    Date dateOfCalendar = ticketRequestEntity.getDateNew();
                    UserEntity teacher = ticketRequestEntity.getUser();
                    PracticeCaseEntity practiceCaseEntity = this.iPracticeCaseRepository.findPracticeCaseEntityById(Long.valueOf(ticketRequestEntity.getPracticeCaseNew()));
                    boolean checkFirst = this.iCalendarRepository.existsByDateOfCalendarAndUserAndPracticeCase(dateOfCalendar, teacher, practiceCaseEntity);

                    try {
                        // check first
                        if (!checkFirst) {
                            CalendarEntity calendarEntityTdlChange = new CalendarEntity();
                            calendarEntityTdlChange.setId(ticketRequestEntity.getCalendarId());
                            calendarEntityTdlChange.setUser(teacher);
                            calendarEntityTdlChange.setDateOfCalendar(dateOfCalendar);
                            calendarEntityTdlChange.setPracticeCase(practiceCaseEntity);
                            calendarEntityTdlChange.setSubject(ticketRequestEntity.getSubject());
                            calendarEntityTdlChange.setClassroom(ticketRequestEntity.getClassroomEntity());
                            if (noteCalendarCurrent.equals(PurposeUse.LICH_THAY_DOI.toString())) {
                                calendarEntityTdlChange.setNoteCalendar(PurposeUse.LICH_THAY_DOI.toString());
                            } else {
                                calendarEntityTdlChange.setNoteCalendar(PurposeUse.LICH_MUON_PHONG.toString());
                            }


                            List<Long> roomIdNews = new ArrayList<>();

                            if (ticketRequestEntity.getRoomNew().contains(",")) {
                                String[] loopRoomNews = ticketRequestEntity.getRoomNew().split(",");
                                for (String loopRoomNew : loopRoomNews) {
                                    roomIdNews.add(Long.valueOf(loopRoomNew));
                                }
                            } else {
                                roomIdNews.add(Long.valueOf(ticketRequestEntity.getRoomNew()));
                            }

                            // check second
                            ResponseEntity<String> checkSecond = this.iCalendarService.handleCheckExistCalendar1E(dateOfCalendar, practiceCaseEntity, roomIdNews);
                            // must delete idCalendar before save new room , because delete idCalendar old
                            if (checkSecond.getStatusCode() == HttpStatus.OK) {
                              CalendarEntity newCalendarTdl =  this.iCalendarRepository.save(calendarEntityTdlChange);
                              // delete idCalendar OLD remember this id Old
                                this.iRoomRepository.deleteLTH_Phong(ticketRequestEntity.getCalendarId());
                                // save idCalendar NEW with new room update ( mac du ID Old , New nhu nhau )
                                this.iRoomRepository.saveLTH_Phong(newCalendarTdl.getId(), roomIdNews);

                            } else {
                                return ResponseEntity.badRequest().body(checkSecond.getBody());
                            }
                        } else {
                            return ResponseEntity.badRequest().body("Existed a calendar with Date : " +
                                    dateOfCalendar.toString() + " , PracticeCase : " +
                                    practiceCaseEntity.getNamePracticeCase() + " And teacher : " +
                                    userEntity.getFirstName() + " " + userEntity.getLastName());
                        }

                    } catch (Exception e) {
                        System.out.println("--ER : error save change calendar practiceCase" + e.getMessage());
                        e.printStackTrace();
                    }
                    // save ticket
                    StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("APPROVAL");
                    ticketRequestEntity.setDoneGVU(status);
                    ticketRequestEntity.setStatus(status);
                    this.iTicketRequestRepository.save(ticketRequestEntity);
                    // done
                    return ResponseEntity.ok().body("Calendar change had been success");
                }
            } else if (nameRole.equals("CSVC")) {
                try {
                    //                    boolean checkExistCalendarToMP = this.iCalendarRepository.existsByDateOfCalendarAndUserAndPracticeCase(ticketRequestEntity.getDateNew() , ticketRequestEntity.getUser() , practiceCaseEntity);
//                     if( !checkExistCalendarToMP){
                    ticketRequestEntity.setDoneCSVC(statusApproval);
                    this.iTicketRequestRepository.save(ticketRequestEntity);
                    return ResponseEntity.ok().body("Approval ticket success (CSVC)");
                } catch (Exception e) {
                    System.out.println("---ER error save doneCSVC" + e.getMessage());
                    e.printStackTrace();
                }

            }

            // type : rent room when rent room practice case can have 1->2->3 case , change room or calendar can't only have one case

        }

        return null;
    }

    @Override
    public CalendarResponseDto handleGetCreateTicketChangeCalendar(Long calendarId) {
        CalendarResponseDto calendarResponseAllData = new CalendarResponseDto();
        // get data calendar current
        CalendarEntity calendarEntity = this.iCalendarRepository.findCalendarEntityById(calendarId);
        // get listIdRoom
        List<Long> listRoomOfCalendar = this.iRoomRepository.getIdRoomsByCalendarIdOnLTH_Phong(calendarId);
        String listIdRoom = listRoomOfCalendar.stream().map(Object::toString).collect(Collectors.joining(","));

        CalendarManagementDto calendarCurrent = new CalendarManagementDto();
        calendarCurrent.setId(calendarEntity.getId().toString());
        calendarCurrent.setDate(calendarEntity.getDateOfCalendar().toString());
        calendarCurrent.setTeacher(calendarEntity.getUser().getFirstName() + " " + calendarEntity.getUser().getLastName());
        calendarCurrent.setRoom(listIdRoom);
        calendarCurrent.setSubject(calendarEntity.getSubject().getId().toString());
        calendarCurrent.setClassroom(calendarEntity.getClassroom().getId().toString());
        calendarCurrent.setPracticeCase(calendarEntity.getPracticeCase().getId().toString());
        calendarCurrent.setNote(calendarEntity.getNoteCalendar());
        // get database
        CalendarResponseFields calendarResponseFields = this.iCalendarService.handleGetDataForCreatePage();

        // set data , then response

        calendarResponseAllData.setUserCurrent(calendarCurrent);
        calendarResponseAllData.setDataBase(calendarResponseFields);

        return calendarResponseAllData;
    }

    @Override
    @Transactional
    public ResponseEntity<String> handlePostCreateTicketChangeCalendar(TicketChangeDto ticketChangeDto) {
        try {
            CalendarEntity currentCalendar = this.iCalendarRepository.findCalendarEntityById(ticketChangeDto.getIdCalendar());
            TicketRequestEntity newTicket = new TicketRequestEntity();
            // set NoteTicket
            newTicket.setNoteTicket(ticketChangeDto.getNote());
            //set Date Old
            newTicket.setDateOld(currentCalendar.getDateOfCalendar());
            //set dateNew
            Date dateNew = DateUtils.convertToDate(ticketChangeDto.getDateNew());
            newTicket.setDateNew(dateNew);
            //set practiceCaseOld
            newTicket.setPracticeCaseOld(currentCalendar.getPracticeCase().getId().toString());
            // set practiceCaseNew
            newTicket.setPracticeCaseNew(ticketChangeDto.getPracticeCaseNew().toString());
            // get listIdRoom
            List<Long> listRoomOfCalendar = this.iRoomRepository.getIdRoomsByCalendarIdOnLTH_Phong(ticketChangeDto.getIdCalendar());
            String listIdRoom = listRoomOfCalendar.stream().map(Object::toString).collect(Collectors.joining(","));
            newTicket.setRoomOld(listIdRoom);
            //set roomNew
            String listIdRoomNew = ticketChangeDto.getListRoomNew().stream().map(Object::toString).collect(Collectors.joining(","));
            newTicket.setRoomNew(listIdRoomNew);
            // set idCalendar change
            newTicket.setCalendarId(ticketChangeDto.getIdCalendar());
            // set user request ticket
            newTicket.setUser(currentCalendar.getUser());

            // set TypeRequest
            TypeRequestEntity typeRequest = this.iTypeRequestRepository.findTypeRequestEntityById(2L);
            newTicket.setTypeRequest(typeRequest);

            // set Classroom
            newTicket.setClassroomEntity(currentCalendar.getClassroom());

            //set Subject
            newTicket.setSubject(currentCalendar.getSubject());

            //set status
            StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("PENDING");
            newTicket.setStatus(status);

            // set dateRequest
            newTicket.setDateRequest(new Date());

            // set DuyetCSVC
            // SET DuyetGVU
            newTicket.setDoneCSVC(status);
            newTicket.setDoneGVU(status);
            this.iTicketRequestRepository.save(newTicket);
            return ResponseEntity.ok().body("Request change calendar success");
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
            TicketRequestEntity newTicketRent = new TicketRequestEntity();
            // set NoteTicket
            newTicketRent.setNoteTicket(ticketRentDto.getNote());
           //set date old
            newTicketRent.setRoomOld(null);
            //set dateNew
            Date dateNew = DateUtils.convertToDate(ticketRentDto.getDate());
            newTicketRent.setDateNew(dateNew);
            //set practiceCaseOld
            newTicketRent.setPracticeCaseOld(null);
            // set practiceCaseNew
            newTicketRent.setPracticeCaseNew(ticketRentDto.getPracticeCaseId().toString());
            // get listIdRoom old

            newTicketRent.setRoomOld(null);
            //set roomNew
            String listIdRoomNew = ticketRentDto.getRoomId().stream().map(Object::toString).collect(Collectors.joining(","));
            newTicketRent.setRoomNew(listIdRoomNew);
            // set idCalendar change
            newTicketRent.setCalendarId(null);
            // set user request ticket
            UserEntity user = this.iUserRepository.findUserEntityById(ticketRentDto.getTeacherId());
            newTicketRent.setUser(user);

            // set TypeRequest
            TypeRequestEntity typeRequest = this.iTypeRequestRepository.findTypeRequestEntityById(1L);
            newTicketRent.setTypeRequest(typeRequest);

            // set Classroom
            ClassroomEntity classroom = this
                    .iClassroomRepository.findClassroomEntityById(ticketRentDto.getClassroomId());
            newTicketRent.setClassroomEntity(classroom);

            //set Subject
            SubjectEntity subject = this.iSubjectRepository.findSubjectEntityById(ticketRentDto.getSubjectId());
            newTicketRent.setSubject(subject);

            //set status
            StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("PENDING");
            newTicketRent.setStatus(status);

            // set dateRequest
            newTicketRent.setDateRequest(new Date());

            // set DuyetCSVC
            // SET DuyetGVU
            newTicketRent.setDoneCSVC(status);
            newTicketRent.setDoneGVU(status);
            this.iTicketRequestRepository.save(newTicketRent);
            return ResponseEntity.ok().body("Request rent room success");
        }catch (Exception e){
            System.out.println("---ER error save ticketRequest on request GV" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public  List<NotificationResponseDto> handleGetAllNotificationOfUser() {
        String emailUser = SecurityUtils.getPrincipal();
        UserEntity user = this.iUserRepository.findUserEntityByEmail(emailUser).get();
        List<NotificationEntity> allNotification = this.iNotificationRepository.findAllByUser(user);
        List<NotificationResponseDto> noteResponses = new ArrayList<>();
        for (NotificationEntity note :allNotification ){
            NotificationResponseDto noteResponse = new NotificationResponseDto();
            noteResponse.setId(note.getId().toString());
            noteResponse.setNameNotification(note.getNameNotification());
            noteResponse.setContentNotification(note.getContentNotification());
            noteResponse.setDateNotification(note.getDateNotification().toString());
            noteResponse.setStatus(note.getStatus().getNameStatus());
            noteResponses.add(noteResponse);
        }
        return noteResponses;
    }

    @Override
    @Transactional
    public NotificationResponseDto handleChangeStatusNote(Long notificationId) {
        NotificationEntity notification = this.iNotificationRepository.findNotificationEntityById(notificationId);
        NotificationResponseDto noteResponseDto = new NotificationResponseDto();
        noteResponseDto.setNameNotification(notification.getNameNotification());
        noteResponseDto.setContentNotification(notification.getContentNotification());
        noteResponseDto.setDateNotification(notification.getDateNotification().toString());

        try {
            StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("SEEN");
            notification.setStatus(status);
            this.iNotificationRepository.save(notification);
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

        return ResponseEntity.ok().body("Delete notification success");
    }
}
