package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.calendarResponse.CalendarManagementDto;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseDto;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseFields;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseOneDto;
import com.example.computerweb.DTO.dto.creditClassResponse.CreditClassEligibleDto;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestDto;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestOneDto;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestRoomDto;
import com.example.computerweb.exceptions.CalendarException;
import com.example.computerweb.models.entity.*;
import com.example.computerweb.models.enums.Day;
import com.example.computerweb.models.enums.PracticeCase;
import com.example.computerweb.repositories.*;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarServiceImpl implements ICalendarService {


    private final ICalendarRepository iCalendarRepository;
    private final IUserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final IRoomRepository iRoomRepository;
    private final IClassroomRepository iClassroomRepository;
    private final IPracticeCaseRepository iPracticeCaseRepository;
    private final ISubjectRepository iSubjectRepository;
    private final ICreditClassRepository iCreditClassRepository;
    private final IWeekSemesterRepository iWeekSemesterRepository;
    private  final IStatusRepository iStatusRepository;
    private final ITypeRequestRepository iTypeRequestRepository;
    private final IAccountRepository iAccountRepository;

    @Override
    public List<CalendarManagementDto> handleGetAllDataCalendar() {
        List<CalendarManagementDto> listResult = this.iCalendarRepository.findAllCustom();
        return listResult;
    }

    @Override
    public CalendarResponseFields handleGetDataForCreatePage() {
        CalendarResponseFields calendarResponseFields = new CalendarResponseFields();
        // CreditClass
        List<CreditClassEligibleDto> ListCreditClassDto = this.iCreditClassRepository.findAllCreditClassEligible();

        ArrayList<Map<String, String>> arrayCreditClass = new ArrayList<>();
        for (CreditClassEligibleDto creditClassDto : ListCreditClassDto) {
            Map<String, String> creditClassDetails = new TreeMap<>();
            creditClassDetails.put("idCredit", creditClassDto.getCreditClassId());
            creditClassDetails.put("codeCreditClass", creditClassDto.getCodeCreditClass());
            creditClassDetails.put("nameSubject", creditClassDto.getNameSubject());
            creditClassDetails.put("studentClassroom", creditClassDto.getStudentClassroom());
            creditClassDetails.put("lessonDataBase", creditClassDto.getLessonSum());
            creditClassDetails.put("lessonCurrent", creditClassDto.getLessonHave());
            arrayCreditClass.add(creditClassDetails);
        }

        calendarResponseFields.setCreditClass(arrayCreditClass);



        // WeekSemester
        // semester 2 year 2024-2025


        // group
//        Map<String, String> dataGroups = Group.getGroup();
//        ArrayList<Map<String, String>> arrayGroup = new ArrayList<>();
//        for (Map.Entry<String, String> dataGroup : dataGroups.entrySet()) {
//            Map<String, String> groupDetail = new HashMap<>();
//            groupDetail.put("idGroup", dataGroup.getKey());
//            groupDetail.put("name", dataGroup.getValue());
//            arrayGroup.add( groupDetail);
//        }
//        calendarResponseFields.setGroup(arrayGroup);

        // day
        Map<String, String> dataDays = Day.getDay();
        ArrayList<Map<String, String>> arrayDay= new ArrayList<>();
        for (Map.Entry<String, String> dataDay : dataDays.entrySet()) {
            Map<String, String> dayDetail = new HashMap<>();
            dayDetail.put("idDay", dataDay.getKey());
            dayDetail.put("name", dataDay.getValue());
            arrayDay.add( dayDetail);
        }
        calendarResponseFields.setDay(arrayDay);


        // PracticeCase Begin and End
        Map<String, String> dataPracticeCases = PracticeCase.getPracticeCaseE();
        ArrayList<Map<String, String>> arrayCase= new ArrayList<>();
        for (Map.Entry<String, String> dataPracticeCase : dataPracticeCases.entrySet()) {
            Map<String, String> practiceCaseDetail = new HashMap<>();
            practiceCaseDetail.put("idPracticeCase", dataPracticeCase.getKey());
            practiceCaseDetail.put("name", dataPracticeCase.getValue());
            arrayCase.add( practiceCaseDetail);
        }
        calendarResponseFields.setPracticeCase(arrayCase);

        // rooms
        List<RoomEntity> roomEntities = this.iRoomRepository.findAll();
        ArrayList<Map<String, String>> arrayRoom= new ArrayList<>();
        for (RoomEntity roomEntity : roomEntities) {
            Map<String, String> roomDetails = new TreeMap<>();
            roomDetails.put("idRoom", roomEntity.getId().toString());
            roomDetails.put("quantity", roomEntity.getNumberOfComputers().toString());
            roomDetails.put("facility", roomEntity.getFacility());
            arrayRoom.add(roomDetails);
        }
        calendarResponseFields.setRoom(arrayRoom);



        return calendarResponseFields;
    }

    @Override
    public ArrayList<Map<String, String>> handleWeekStudyForCreateCreditClass(Long codeCreditClass) {
        CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(codeCreditClass);

        SubjectEntity subject = creditClass.getSubject();
        Long semesterOfSubject = Long.valueOf(subject.getSemesterPlan().substring(0,1)) ;

        // week_semester  on 2024-2025
        List<WeekSemesterEntity> weekSemesterEntities = this.iWeekSemesterRepository.findAllBySemesterStudy(semesterOfSubject);
        ArrayList<Map<String, String>> arrayWeekSemester = new ArrayList<>();
        for (WeekSemesterEntity weekSemesterEntity : weekSemesterEntities) {
            Map<String, String> weekSemesterDetails = new TreeMap<>();
            weekSemesterDetails.put("idWeekSemester", weekSemesterEntity.getId().toString());
            weekSemesterDetails.put("time", "Week :" + weekSemesterEntity.getWeekStudy() + "[From " + DateUtils.convertToString(weekSemesterEntity.getDateBegin()) + " to " + DateUtils.convertToString(weekSemesterEntity.getDateEnd()) + "]");
            arrayWeekSemester.add(weekSemesterDetails);
        }

        return arrayWeekSemester;
    }


    @Override
    public CalendarResponseFields handleGetDataForCreateRoomPage() {
        CalendarResponseFields calendarResponseFields = new CalendarResponseFields();
       // teachers
//        RoleEntity roleEntity = this.iRoleRepository.findRoleEntityById(1L).get();
//        List<UserEntity> users = this.iUserRepository.findUserEntitiesByRole(roleEntity);
//        ArrayList<Map<String, String>> arrayTeacher= new ArrayList<>();
//        for (UserEntity user : users) {
//            Map<String, String> teacherDetail = new TreeMap<>();
//            teacherDetail.put("idTeacher" , user.getId().toString());
//            teacherDetail.put("name" , user.getFirstName() +" "+ user.getLastName());
//            teacherDetail.put("major" , user.getMajor().getCodeMajor());
//            arrayTeacher.add(teacherDetail );
//        }
//        calendarResponseFields.setTeacher(arrayTeacher);


        // week_semester  on 2024-2025


        // WeekSemester
        // semester 2 year 2024-2025
//        SemesterEntity semester = this.iSemesterRepository.findSemesterEntityById(2L);
//        // week_semester  on 2024-2025
//        List<WeekSemesterEntity> weekSemesterEntities = this.iWeekSemesterRepository.findAllBySemester(semester);
//        ArrayList<Map<String, String>> arrayWeekSemester = new ArrayList<>();
//        for (WeekSemesterEntity weekSemesterEntity : weekSemesterEntities) {
//            Map<String, String> weekSemesterDetails = new TreeMap<>();
//            weekSemesterDetails.put("idWeekSemester", weekSemesterEntity.getId().toString());
//            weekSemesterDetails.put("time", weekSemesterEntity.getWeek().getNameWeek() + "[From " + DateUtils.convertToString(weekSemesterEntity.getDateBegin()) + " to " + DateUtils.convertToString(weekSemesterEntity.getDateEnd()) + "]");
//            arrayWeekSemester.add(weekSemesterDetails);
//        }
//        calendarResponseFields.setWeekSemester(arrayWeekSemester);


        // day
        Map<String, String> dataDays = Day.getDay();
        ArrayList<Map<String, String>> arrayDay= new ArrayList<>();
        for (Map.Entry<String, String> dataDay : dataDays.entrySet()) {
            Map<String, String> dayDetail = new HashMap<>();
            dayDetail.put("idDay", dataDay.getKey());
            dayDetail.put("name", dataDay.getValue());
            arrayDay.add( dayDetail);
        }
        calendarResponseFields.setDay(arrayDay);


        // PracticeCase Begin and End
        Map<String, String> dataPracticeCases = PracticeCase.getPracticeCaseE();
        ArrayList<Map<String, String>> arrayCase= new ArrayList<>();
        for (Map.Entry<String, String> dataPracticeCase : dataPracticeCases.entrySet()) {
            Map<String, String> practiceCaseDetail = new HashMap<>();
            practiceCaseDetail.put("idPracticeCase", dataPracticeCase.getKey());
            practiceCaseDetail.put("name", dataPracticeCase.getValue());
            arrayCase.add( practiceCaseDetail);
        }
        calendarResponseFields.setPracticeCase(arrayCase);

        // rooms
        List<RoomEntity> roomEntities = this.iRoomRepository.findAll();
        ArrayList<Map<String, String>> arrayRoom= new ArrayList<>();
        for (RoomEntity roomEntity : roomEntities) {
            Map<String, String> roomDetails = new TreeMap<>();
            roomDetails.put("idRoom", roomEntity.getId().toString());
            roomDetails.put("quantity", roomEntity.getNumberOfComputers().toString());
            roomDetails.put("facility", roomEntity.getFacility());
            arrayRoom.add(roomDetails);
        }
        calendarResponseFields.setRoom(arrayRoom);


        return calendarResponseFields;
    }

    @Override
    public CalendarResponseDto handleGetDataForUpdatePage(Long calendarId) {
        CalendarResponseFields data = handleGetDataForCreatePage();
        CalendarEntity calendarEntity = this.iCalendarRepository.findCalendarEntityById(calendarId);
        CalendarResponseOneDto calendarResponseOneDto = new CalendarResponseOneDto();
        calendarResponseOneDto.setCalendarId(calendarEntity.getId().toString());
        calendarResponseOneDto.setCreditClassId(calendarEntity.getCreditClass() != null ? calendarEntity.getCreditClass().getId().toString() : null);
        calendarResponseOneDto.setUserIdMp_Fk(calendarEntity.getUser()!=null ?calendarEntity.getUser().getFirstName()+ calendarEntity.getUser().getLastName() : null );
//        calendarResponseOneDto.setGroupId(calendarEntity.getGroup() != null ? calendarEntity.getGroup() : null);
        calendarResponseOneDto.setWeekSemesterId(calendarEntity.getWeekSemester().getId().toString());
        calendarResponseOneDto.setDayId(calendarEntity.getDay().toString());
        calendarResponseOneDto.setPracticeCaseBeginId(calendarEntity.getPracticeCase().getId().toString());
        calendarResponseOneDto.setAllCase(calendarEntity.getAllCase().toString());
        calendarResponseOneDto.setRoomId(calendarEntity.getRoom().getId().toString());
        calendarResponseOneDto.setPurposeUse(calendarEntity.getNoteCalendar());
        CalendarResponseDto calendarResponseDto = new CalendarResponseDto();
        calendarResponseDto.setDataBase(data);
        calendarResponseDto.setUserCurrent(calendarResponseOneDto);
        return calendarResponseDto;
    }

    @Override
    @Transactional
    public ResponseEntity<String> handleCreateCalendar(CalendarRequestDto calendarRequestDto) {
       if (  calendarRequestDto.getGroupId2() == null){
           CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(calendarRequestDto.getCreditClassId());
           WeekSemesterEntity weekSemester1 = this.iWeekSemesterRepository.findWeekSemesterEntityById(calendarRequestDto.getWeekSemesterId1());
           Long day1 = calendarRequestDto.getDayId1();
           PracticeCaseEntity practiceCase1 = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarRequestDto.getPracticeCaseBeginId1());
           RoomEntity room1 = this.iRoomRepository.findRoomEntityById(calendarRequestDto.getRoomId1());
           StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("ACTIVE");

           boolean checkExisting1 = this.iCalendarRepository.existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus( weekSemester1, day1, practiceCase1, room1,status);
           try {

               if (!checkExisting1) {
                   CalendarEntity calendarEntity1 = new CalendarEntity();
                   calendarEntity1.setCreditClass(creditClass);
//                   calendarEntity1.setGroup("0" + calendarRequestDto.getGroupId1());
//                   calendarEntity1.setOrganization("01");
                   calendarEntity1.setWeekSemester(weekSemester1);
                   calendarEntity1.setDay(day1);
                   calendarEntity1.setPracticeCase(practiceCase1);
                   calendarEntity1.setAllCase(calendarRequestDto.getAllCase1());
                   calendarEntity1.setRoom(room1);
                   calendarEntity1.setStatus(status);
                   calendarEntity1.setNoteCalendar(calendarRequestDto.getPurposeUse1() != null ? calendarRequestDto.getPurposeUse1() : "");
                   // save calendarEntity1
                   this.iCalendarRepository.save(calendarEntity1);
                   return ResponseEntity.ok().body("Create calendar success");
               }else {
                   throw new  CalendarException("Existed  calendar 1 with day : " +
                           day1 + " , Week : "+weekSemester1.getWeekStudy() + " Time : [" +
                           DateUtils.convertToString(weekSemester1.getDateBegin())
                           + "]-["
                           + DateUtils.convertToString(weekSemester1.getDateEnd())
                           + "]"
                           + " , PracticeCaseBegin : "
                           + practiceCase1.getNamePracticeCase()
                   );
               }
           }catch (CalendarException e){
               System.out.println("------ER error create calendar" + e.getMessage());
               e.printStackTrace();
               throw new  CalendarException(e.getMessage());
           }

       }
    if (calendarRequestDto.getGroupId1() != null && calendarRequestDto.getGroupId2() != null ){
        CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(calendarRequestDto.getCreditClassId());
        WeekSemesterEntity weekSemester1 = this.iWeekSemesterRepository.findWeekSemesterEntityById(calendarRequestDto.getWeekSemesterId1());
        Long day1 = calendarRequestDto.getDayId1();
        PracticeCaseEntity practiceCase1 = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarRequestDto.getPracticeCaseBeginId1());
        RoomEntity room1 = this.iRoomRepository.findRoomEntityById(calendarRequestDto.getRoomId1());
        StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("ACTIVE");

        boolean checkExisting1 = this.iCalendarRepository.existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus( weekSemester1, day1, practiceCase1, room1,status);
        try {

            if (!checkExisting1) {
                CalendarEntity calendarEntity1 = new CalendarEntity();
                calendarEntity1.setCreditClass(creditClass);
//                calendarEntity1.setGroup("0" + calendarRequestDto.getGroupId1());
//                calendarEntity1.setOrganization("01");
                calendarEntity1.setWeekSemester(weekSemester1);
                calendarEntity1.setDay(day1);
                calendarEntity1.setPracticeCase(practiceCase1);
                calendarEntity1.setAllCase(calendarRequestDto.getAllCase1());
                calendarEntity1.setRoom(room1);
                calendarEntity1.setStatus(status);
                calendarEntity1.setNoteCalendar(calendarRequestDto.getPurposeUse1() != null ? calendarRequestDto.getPurposeUse1() : "");
                // save calendarEntity1
                this.iCalendarRepository.save(calendarEntity1);

                WeekSemesterEntity weekSemester2 = this.iWeekSemesterRepository.findWeekSemesterEntityById(calendarRequestDto.getWeekSemesterId2());
                Long day2 = calendarRequestDto.getDayId1();
                PracticeCaseEntity practiceCase2 = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarRequestDto.getPracticeCaseBeginId2());
                RoomEntity room2 = this.iRoomRepository.findRoomEntityById(calendarRequestDto.getRoomId2());
                // check Second

                boolean checkExisting2 = this.iCalendarRepository.existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus(  weekSemester2, day2, practiceCase2, room2,status);

                     if (!checkExisting2) {
                         CalendarEntity calendarEntity2 = new CalendarEntity();
                         calendarEntity2.setCreditClass(creditClass);
//                         calendarEntity2.setGroup("0" + calendarRequestDto.getGroupId2());
//                         calendarEntity2.setOrganization("01");
                         calendarEntity2.setWeekSemester(weekSemester2);
                         calendarEntity2.setDay(day2);
                         calendarEntity2.setPracticeCase(practiceCase2);
                         calendarEntity2.setAllCase(calendarRequestDto.getAllCase2());
                         calendarEntity2.setRoom(room2);
                         calendarEntity2.setStatus(status);
                         calendarEntity2.setNoteCalendar(calendarRequestDto.getPurposeUse2() != null ? calendarRequestDto.getPurposeUse2() : "");
                         this.iCalendarRepository.save(calendarEntity2);
                     } else {

                         throw new  CalendarException("Existed a calendar 2 day : " +
                                 day2 + " , Week : "+weekSemester2.getWeekStudy()+" Time : [" +
                                 DateUtils.convertToString(weekSemester2.getDateBegin())
                                 + "]-["
                                 + DateUtils.convertToString(weekSemester2.getDateEnd())
                                 + "]"
                                 + " , PracticeCaseBegin : "
                                 + practiceCase2.getNamePracticeCase()
                         );

                     }

                // Check calendar2

                return ResponseEntity.ok().body("Create calendar success");

            } else {
                throw new  CalendarException("Existed a calendar 1 day : " +
                        day1 + " , Week : "+weekSemester1.getWeekStudy()+" Time : [" +
                        DateUtils.convertToString(weekSemester1.getDateBegin())
                        + "]-["
                        + DateUtils.convertToString(weekSemester1.getDateEnd())
                        + "]"
                        + " , PracticeCaseBegin : "
                        + practiceCase1.getNamePracticeCase()
                );
            }
        } catch (CalendarException e) {
            System.out.println("---> ER :" + e.getMessage());
            e.printStackTrace();
            throw new  CalendarException(e.getMessage());
        }
    }
        return null;
    }

    @Override
    @Transactional
    public ResponseEntity<String> handleCreateRoom(CalendarRequestRoomDto calendarRequestRoomDto) {
        WeekSemesterEntity weekSemester = this.iWeekSemesterRepository.findWeekSemesterEntityById(calendarRequestRoomDto.getWeekSemesterId());
        Long day = calendarRequestRoomDto.getDayId();
        PracticeCaseEntity practiceCase = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarRequestRoomDto.getPracticeCaseBeginId());
        Long allCase = calendarRequestRoomDto.getAllCase();
        RoomEntity room = this.iRoomRepository.findRoomEntityById(calendarRequestRoomDto.getRoomId());
        UserEntity userIdMp_FK = this.iUserRepository.findUserEntityById(calendarRequestRoomDto.getUserIdMp_FK());
        // check first

        StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("ACTIVE");

        boolean checkExisting = this.iCalendarRepository.existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus( weekSemester, day, practiceCase, room , status);
        try {
            if (!checkExisting){
                CalendarEntity calendar = new CalendarEntity();
                calendar.setCreditClass(null);
                calendar.setUser(userIdMp_FK);
//                calendar.setGroup(null);
//                calendar.setOrganization(null);
                calendar.setWeekSemester(weekSemester);
                calendar.setDay(day);
                calendar.setPracticeCase(practiceCase);
                calendar.setAllCase(allCase);
                calendar.setRoom(room);
                calendar.setNoteCalendar("Lich muon phong");
                this.iCalendarRepository.save(calendar);
                return ResponseEntity.ok().body("Create calendar rent room success");
            }else {
                throw new  CalendarException("Existed a calendar with day : " +
                        day + " , Week :"+weekSemester.getWeekStudy()+" Time : [" +
                        DateUtils.convertToString(weekSemester.getDateBegin())
                        + "]-["
                        + DateUtils.convertToString(weekSemester.getDateEnd())
                        + "]"
                        + " , PracticeCaseBegin : "
                        + practiceCase.getNamePracticeCase()
                );
            }

        }catch (CalendarException e){
            System.out.println("-----ER error Create rent room" + e.getMessage());
            e.printStackTrace();
            throw new  CalendarException(e.getMessage());
        }


    }

    @Transactional
    @Override
    public ResponseEntity<String> handleUpdateCalendar(CalendarRequestOneDto calendarRequestOneDto) {
        CalendarEntity calendar = this.iCalendarRepository.findCalendarEntityById(calendarRequestOneDto.getCalendarId());
        WeekSemesterEntity weekSemesterNew = this.iWeekSemesterRepository.findWeekSemesterEntityById(calendarRequestOneDto.getWeekSemesterIdNew());
        Long dayNew = calendarRequestOneDto.getDayId();
        PracticeCaseEntity practiceCaseNew = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarRequestOneDto.getPracticeCaseBeginIdNew());
        RoomEntity roomNew = this.iRoomRepository.findRoomEntityById(calendarRequestOneDto.getRoomIdNew());
        String noteNew = calendarRequestOneDto.getPurposeUseNew() != null ? calendarRequestOneDto.getPurposeUseNew() : "";
        StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("ACTIVE");

        boolean checkFirst = this.iCalendarRepository.existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus( weekSemesterNew, dayNew , practiceCaseNew , roomNew , status);

        try {
            // checkFirst
            if (!checkFirst) {

                calendar.setDay(dayNew);
                calendar.setPracticeCase(practiceCaseNew);
                calendar.setRoom(roomNew);
                calendar.setWeekSemester(weekSemesterNew);
                calendar.setNoteCalendar(noteNew);
                this.iCalendarRepository.save(calendar);
                return ResponseEntity.ok().body("Update calendar success");
            } else {
                throw new  CalendarException("Existed a calendar with day : " +
                        dayNew + " , Week : "+weekSemesterNew.getWeekStudy()+" Time : [" +
                        DateUtils.convertToString(weekSemesterNew.getDateBegin())
                        + "]-["
                        + DateUtils.convertToString(weekSemesterNew.getDateEnd())
                        + "]"
                        + " , PracticeCaseBegin : "
                        + practiceCaseNew.getNamePracticeCase()
                );
            }
        } catch (CalendarException e) {
            System.out.println("---> ER :" + e.getMessage());
            e.printStackTrace();
           throw  new CalendarException(e.getMessage());
        }


    }

    @Override
    @Transactional
    public void handleDeleteCalendar(String calendarId) {
        try {
            if (calendarId.contains(",")) {
                String [] arrayCalendarId = calendarId.split(",");
                for ( String id : arrayCalendarId){
                    this.iCalendarRepository.deleteById(Long.valueOf(id));
                }
            }else {
                this.iCalendarRepository.deleteById(Long.valueOf(calendarId));
            }
        } catch (CalendarException e) {
            System.out.println("--->ER error delete both" + e.getMessage());
            e.printStackTrace();
            throw new CalendarException("Can delete calendar Id : " + calendarId);
        }
    }
}
