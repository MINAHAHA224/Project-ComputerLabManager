package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.TicketResponseMgmDto;
import com.example.computerweb.DTO.requestBody.ticketRequest.TicketManagementRequestDto;
import com.example.computerweb.models.entity.*;
import com.example.computerweb.models.enums.PurposeUse;
import com.example.computerweb.repositories.*;
import com.example.computerweb.services.ITicketRequestService;
import com.example.computerweb.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TicketRequestServiceImpl implements ITicketRequestService {
    private final ITicketRequestRepository iTicketRequestRepository;
    private final IStatusRepository iStatusRepository;
    private final INotificationRepository iNotificationRepository;
    private final IUserRepository iUserRepository;
    private final ICalendarRepository iCalendarRepository;
    private final IPracticeCaseRepository iPracticeCaseRepository;
    private final IRoomRepository iRoomRepository;
    private final ITypeRequestRepository iTypeRequestRepository;

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


                if(nameRole.equals("GVU")){
                    ticketRequestEntity.setDoneGVU(statusReject);
                    ticketRequestEntity.setStatus(statusReject);
                }else  if (nameRole.equals("CSVC")){
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
                    if (ticketRequestEntity.getPracticeCaseNew().contains(",")) {
                        // check exist calendar only for CSVC not for GVU , GVU only save
                        String[] loopPracticeCases = ticketRequestEntity.getPracticeCaseNew().split(",");
                        for (String loopPracticeCase : loopPracticeCases) {
                            PracticeCaseEntity practiceCaseEntity = this.iPracticeCaseRepository.findPracticeCaseEntityById(Long.valueOf(loopPracticeCase));
//                        boolean checkExistCalendarToMP = this.iCalendarRepository.existsByDateOfCalendarAndUserAndPracticeCase(ticketRequestEntity.getDateNew() , ticketRequestEntity.getUser() , practiceCaseEntity);
//                        if (!checkExistCalendarToMP ){
                            try {
                                CalendarEntity calendarEntity = new CalendarEntity();
                                calendarEntity.setNoteCalendar(PurposeUse.LICH_MUON_PHONG.toString());
                                calendarEntity.setDateOfCalendar(ticketRequestEntity.getDateNew());
                                calendarEntity.setUser(ticketRequestEntity.getUser());

                                calendarEntity.setPracticeCase(practiceCaseEntity);
                                calendarEntity.setClassroom(ticketRequestEntity.getClassroomEntity());
                                calendarEntity.setSubject(ticketRequestEntity.getSubject());
                                CalendarEntity newCalendar = this.iCalendarRepository.save(calendarEntity);
                                if (ticketRequestEntity.getRoomNew().contains(",")) {
                                    String[] loopRooms = ticketRequestEntity.getRoomNew().split(",");
                                    List<Long> roomIds = new ArrayList<>();
                                    for (String loopRoom : loopRooms) {
                                        roomIds.add(Long.valueOf(loopRoom));
                                    }
                                    this.iRoomRepository.saveLTH_Phong(newCalendar.getId(), roomIds);
                                } else {
                                    List<Long> roomIds = new ArrayList<>();
                                    roomIds.add(Long.valueOf(ticketRequestEntity.getRoomNew()));
                                    this.iRoomRepository.saveLTH_Phong(newCalendar.getId(), roomIds);
                                }
                            } catch (Exception e) {
                                System.out.println("--ER : error save calendar loopPracticeCase" + e.getMessage());
                                e.printStackTrace();
                            }

//                        }else {
//                            return ResponseEntity.badRequest().body("Calendar is duplicated because practiceCase " +
//                                    practiceCaseEntity.getNamePracticeCase() + " on day " + ticketRequestEntity.getDateNew() +
//                                    " already exists" );
//                        }
                        }
                    } else {
                        PracticeCaseEntity practiceCaseEntity = this.iPracticeCaseRepository.findPracticeCaseEntityById(Long.valueOf(ticketRequestEntity.getPracticeCaseNew()));
//                    boolean checkExistCalendarToMP = this.iCalendarRepository.existsByDateOfCalendarAndUserAndPracticeCase(ticketRequestEntity.getDateNew() , ticketRequestEntity.getUser() , practiceCaseEntity);
//                     if( !checkExistCalendarToMP){
                        try {
                            CalendarEntity calendarEntity = new CalendarEntity();
                            calendarEntity.setNoteCalendar(PurposeUse.LICH_MUON_PHONG.toString());
                            calendarEntity.setDateOfCalendar(ticketRequestEntity.getDateNew());
                            calendarEntity.setUser(ticketRequestEntity.getUser());
                            calendarEntity.setPracticeCase(practiceCaseEntity);
                            calendarEntity.setClassroom(ticketRequestEntity.getClassroomEntity());
                            calendarEntity.setSubject(ticketRequestEntity.getSubject());
                            CalendarEntity newCalendar = this.iCalendarRepository.save(calendarEntity);
                            if (ticketRequestEntity.getRoomNew().contains(",")) {
                                String[] loopRooms = ticketRequestEntity.getRoomNew().split(",");
                                List<Long> roomIds = new ArrayList<>();
                                for (String loopRoom : loopRooms) {
                                    roomIds.add(Long.valueOf(loopRoom));
                                }
                                this.iRoomRepository.saveLTH_Phong(newCalendar.getId(), roomIds);
                            } else {
                                List<Long> roomIds = new ArrayList<>();
                                roomIds.add(Long.valueOf(ticketRequestEntity.getRoomNew()));
                                this.iRoomRepository.saveLTH_Phong(newCalendar.getId(), roomIds);
                            }
                        } catch (Exception e) {
                            System.out.println("--ER : error save calendar practiceCase" + e.getMessage());
                            e.printStackTrace();
                        }

//                     }else {
//                         return ResponseEntity.badRequest().body("Calendar is duplicated because practiceCase " +
//                                 practiceCaseEntity.getNamePracticeCase() + " on day " + ticketRequestEntity.getDateNew() +
//                                 " already exists" );
//                     }

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
                    PracticeCaseEntity practiceCaseEntity = this.iPracticeCaseRepository.findPracticeCaseEntityById(Long.valueOf(ticketRequestEntity.getPracticeCaseNew()));
                    try {
                        CalendarEntity calendarEntityTdlChange = new CalendarEntity();
                        calendarEntityTdlChange.setId(ticketRequestEntity.getCalendarId());
                        calendarEntityTdlChange.setUser(ticketRequestEntity.getUser());
                        calendarEntityTdlChange.setDateOfCalendar(ticketRequestEntity.getDateNew());
                        calendarEntityTdlChange.setPracticeCase(practiceCaseEntity);
                        calendarEntityTdlChange.setSubject(ticketRequestEntity.getSubject());
                        calendarEntityTdlChange.setClassroom(ticketRequestEntity.getClassroomEntity());
                        if (noteCalendarCurrent.equals(PurposeUse.LICH_THAY_DOI.toString())) {
                            calendarEntityTdlChange.setNoteCalendar(PurposeUse.LICH_THAY_DOI.toString());
                        } else {
                            calendarEntityTdlChange.setNoteCalendar(PurposeUse.LICH_MUON_PHONG.toString());
                        }

                        this.iCalendarRepository.save(calendarEntityTdlChange);
                        if (ticketRequestEntity.getRoomNew().contains(",")) {
                            String[] loopRoomNews = ticketRequestEntity.getRoomNew().split(",");
                            List<Long> roomIdNews = new ArrayList<>();
                            for (String loopRoomNew : loopRoomNews) {
                                roomIdNews.add(Long.valueOf(loopRoomNew));
                            }
                            this.iRoomRepository.deleteLTH_Phong(ticketRequestEntity.getCalendarId());
                            this.iRoomRepository.saveLTH_Phong(ticketRequestEntity.getCalendarId(), roomIdNews);
                        } else {
                            List<Long> roomIds = new ArrayList<>();
                            roomIds.add(Long.valueOf(ticketRequestEntity.getRoomNew()));
                            this.iRoomRepository.deleteLTH_Phong(ticketRequestEntity.getCalendarId());
                            this.iRoomRepository.saveLTH_Phong(ticketRequestEntity.getCalendarId(), roomIds);

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
            } else if ( nameRole.equals("CSVC")){
                try {
                    ticketRequestEntity.setDoneCSVC(statusApproval);
                    this.iTicketRequestRepository.save(ticketRequestEntity);
                    return ResponseEntity.ok().body("Approval ticket success (CSVC)");
                }catch ( Exception e){
                    System.out.println("---ER error save doneCSVC" + e.getMessage());
                    e.printStackTrace();
                }

            }

            // type : rent room when rent room practice case can have 1->2->3 case , change room or calendar can't only have one case

        }

        return null;
    }
}
