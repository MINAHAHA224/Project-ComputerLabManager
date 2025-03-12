package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.CalendarManagementDto;
import com.example.computerweb.DTO.dto.CalendarResponseDto;
import com.example.computerweb.DTO.dto.CalendarResponseFields;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestDto;
import com.example.computerweb.models.entity.*;
import com.example.computerweb.models.enums.PurposeUse;
import com.example.computerweb.repositories.*;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public List<CalendarManagementDto> handleGetAllDataCalendar() {
        List<CalendarManagementDto> results = this.iCalendarRepository.findAllCustom();
        return results;
    }

    @Override
    public CalendarResponseFields handleGetDataForCreatePage() {
        //purposeUse
        Map<String, String> purposeUse = PurposeUse.getPurposeUse();
        //teachers
        RoleEntity roleEntity = this.iRoleRepository.findById(1).get();
        List<UserEntity> users = this.iUserRepository.findAllByRole(roleEntity);
        Map<String, String> teachers = new TreeMap<>();
        for (UserEntity user : users) {
            teachers.put(user.getId().toString(), user.getFirstName() + " " + user.getLastName());
        }
        // rooms
        List<RoomEntity> roomEntities = this.iRoomRepository.findAll();
        Map<String, Map<String, String>> rooms = new TreeMap<>();
        for (RoomEntity roomEntity : roomEntities) {
            Map<String, String> roomDetails = new TreeMap<>();
            roomDetails.put("name", roomEntity.getNameRoom());
            roomDetails.put("quantity", roomEntity.getNumberOfComputers().toString());
            roomDetails.put("facility", roomEntity.getFacilityEntityRoom().getNameFacility());
            rooms.put(roomEntity.getId().toString(), roomDetails);
        }
        // classrooms
        List<ClassroomEntity> classroomEntities = this.iClassroomRepository.findAll();
        Map<String, Map<String, String>> classrooms = new TreeMap<>();
        for (ClassroomEntity classroomEntity : classroomEntities) {
            Map<String, String> classroomDetails = new TreeMap<>();
            classroomDetails.put("name", classroomEntity.getNameClassroom());
            classroomDetails.put("quantity", classroomEntity.getNumberOfStudents().toString());
            classroomDetails.put("facility", classroomEntity.getFacilityEntityClassroom().getNameFacility());
            classrooms.put(classroomEntity.getId().toString(), classroomDetails);
        }
        // practiceCases
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        List<PracticeCaseEntity> practiceCaseEntities = this.iPracticeCaseRepository.findAll();
        Map<String, Map<String, String>> practiceCases = new TreeMap<>();
        for (PracticeCaseEntity practiceCaseEntity : practiceCaseEntities) {
            Map<String, String> practiceCaseDetails = new TreeMap<>();
            practiceCaseDetails.put("name", practiceCaseEntity.getNamePracticeCase());
            practiceCaseDetails.put("timeStart", practiceCaseEntity.getTimeStart().format(dateTimeFormat));
            practiceCaseDetails.put("timeEnd", practiceCaseEntity.getTimeEnd().format(dateTimeFormat));
            practiceCases.put(practiceCaseEntity.getId().toString(), practiceCaseDetails);
        }
        // subjects
        List<SubjectEntity> subjectEntities = this.iSubjectRepository.findAll();
        Map<String, Map<String, String>> subjects = new TreeMap<>();
        for (SubjectEntity subjectEntity : subjectEntities) {
            Map<String, String> subjectDetails = new TreeMap<>();
            subjectDetails.put("name", subjectEntity.getNameSubject());
            subjectDetails.put("credit", subjectEntity.getSoTTH().toString());
            subjects.put(subjectEntity.getId().toString(), subjectDetails);
        }

        Map<String, Map<String, String>> field = new TreeMap<>();
        field.put("purposeUses", purposeUse);
        field.put("teachers", teachers);
        Map<String, Map<String, Map<String, String>>> fields = new TreeMap<>();
        fields.put("rooms", rooms);
        fields.put("classrooms", classrooms);
        fields.put("practiceCases", practiceCases);
        fields.put("subjects", subjects);
        CalendarResponseFields calendarResponseFields = new CalendarResponseFields();
        calendarResponseFields.setField(field);
        calendarResponseFields.setFields(fields);
        return calendarResponseFields;
    }

    @Override
    public CalendarResponseDto handleGetDataForUpdatePage(Long calendarId) {
        CalendarResponseFields data = handleGetDataForCreatePage();
        String roomId = this.iRoomRepository.getIdRoomsByCalendarIdOnLTH_Phong(calendarId).stream()
                .map(id -> String.valueOf(id))
                .collect(Collectors.joining(","));
        CalendarEntity calendarEntity = handleGetCalendarById(calendarId);
        CalendarManagementDto calendarManagementDto = new CalendarManagementDto();
        calendarManagementDto.setDate(calendarEntity.getDateOfCalendar().toString());
        calendarManagementDto.setNote(calendarEntity.getNoteCalendar());
        calendarManagementDto.setTeacher(calendarEntity.getUser().getId().toString());
        calendarManagementDto.setClassroom(calendarEntity.getClassroom().getId().toString());
        calendarManagementDto.setPracticeCase(calendarEntity.getPracticeCase().getId().toString());
        calendarManagementDto.setSubject(calendarEntity.getSubject().getId().toString());
        calendarManagementDto.setRoom(roomId);
        calendarManagementDto.setId(calendarEntity.getId().toString());
        CalendarResponseDto calendarResponseDto = new CalendarResponseDto();
        calendarResponseDto.setDataBase(data);
        calendarResponseDto.setUserCurrent(calendarManagementDto);

        return calendarResponseDto;
    }

    @Override
    @Transactional
    public ResponseEntity<String> handleCreateCalendar(CalendarRequestDto calendarRequestDto) {
        Date dateOfCalendar = DateUtils.convertToDate(calendarRequestDto.getDate());
        UserEntity teacher = this.iUserRepository.findUserEntityById(calendarRequestDto.getTeacherId());
        PracticeCaseEntity practiceCase = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarRequestDto.getPracticeCaseId());
        // check first
        boolean checkFirst = this.iCalendarRepository.existsByDateOfCalendarAndUserAndPracticeCase(dateOfCalendar, teacher, practiceCase);


        try {
            if (!checkFirst) {
                CalendarEntity calendarEntity = new CalendarEntity();
                calendarEntity.setDateOfCalendar(dateOfCalendar);
                calendarEntity.setNoteCalendar(calendarRequestDto.getPurposeUse().toString());

                calendarEntity.setUser(teacher);
                ClassroomEntity classroom = this.iClassroomRepository.findClassroomEntityById(calendarRequestDto.getClassroomId());
                calendarEntity.setClassroom(classroom);

                calendarEntity.setPracticeCase(practiceCase);
                SubjectEntity subjectEntity = this.iSubjectRepository.findSubjectEntityById(calendarRequestDto.getSubjectId());
                calendarEntity.setSubject(subjectEntity);
                // check second
                ResponseEntity<String> checkSecond = this.handleCheckExistCalendar1E(dateOfCalendar, practiceCase, calendarRequestDto.getRoomId());
                if (checkSecond.getStatusCode() == HttpStatus.OK) {
                    CalendarEntity savedCalendar = this.iCalendarRepository.save(calendarEntity);
                    Long calendarId = savedCalendar.getId();
                    this.iRoomRepository.saveLTH_Phong(calendarId, calendarRequestDto.getRoomId());
                    return ResponseEntity.ok().body("Create calendar success");
                } else {
                    return ResponseEntity.badRequest().body(checkSecond.getBody());
                }

            } else {
                return ResponseEntity.badRequest().body("Existed a calendar with Date : " +
                        dateOfCalendar.toString() + " , PracticeCase : " +
                        practiceCase.getNamePracticeCase() + " And teacher : " +
                        teacher.getFirstName() + " " + teacher.getLastName());
            }

        } catch (Exception e) {
            System.out.println("---> ER :" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Create calendar failed");
        }


    }

    @Transactional
    @Override
    public ResponseEntity<String> handleUpdateCalendar(CalendarRequestDto calendarRequestDto) {
        Date dateOfCalendar = DateUtils.convertToDate(calendarRequestDto.getDate());
        UserEntity teacher = this.iUserRepository.findUserEntityById(calendarRequestDto.getTeacherId());
        PracticeCaseEntity practiceCase = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarRequestDto.getPracticeCaseId());
        boolean checkFirst = this.iCalendarRepository.existsByDateOfCalendarAndUserAndPracticeCase(dateOfCalendar, teacher, practiceCase);

        try {
            // checkFirst
            if (checkFirst) {
                // Must delete LTH_Phong first , then delete calendar because The DELETE statement conflicted with the REFERENCE constraint
                this.iRoomRepository.deleteLTH_Phong(calendarRequestDto.getId());
                this.iCalendarRepository.deleteById(calendarRequestDto.getId());
                CalendarEntity calendarEntity = new CalendarEntity();
                calendarEntity.setDateOfCalendar(dateOfCalendar);
                calendarEntity.setNoteCalendar(calendarRequestDto.getPurposeUse().toString());
                calendarEntity.setUser(teacher);
                ClassroomEntity classroom = this.iClassroomRepository.findClassroomEntityById(calendarRequestDto.getClassroomId());
                calendarEntity.setClassroom(classroom);
                calendarEntity.setPracticeCase(practiceCase);
                SubjectEntity subjectEntity = this.iSubjectRepository.findSubjectEntityById(calendarRequestDto.getSubjectId());
                calendarEntity.setSubject(subjectEntity);
                // check second
                ResponseEntity<String> checkSecond = this.handleCheckExistCalendar1E(dateOfCalendar, practiceCase, calendarRequestDto.getRoomId());
                if (checkSecond.getStatusCode() == HttpStatus.OK) {
                    CalendarEntity savedCalendar = this.iCalendarRepository.save(calendarEntity);
                    Long calendarId = savedCalendar.getId();
                    this.iRoomRepository.saveLTH_Phong(calendarId, calendarRequestDto.getRoomId());
                    return ResponseEntity.ok().body("Update calendar success");
                } else {
                    return ResponseEntity.badRequest().body(checkSecond.getBody());
                }

            } else {
                return ResponseEntity.badRequest().body("Existed a calendar with Date : " +
                        dateOfCalendar.toString() + " , PracticeCase : " +
                        practiceCase.getNamePracticeCase() + " And teacher : " +
                        teacher.getFirstName() + " " + teacher.getLastName());
            }
        } catch (Exception e) {
            System.out.println("---> ER :" + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Update calendar failed");
        }


    }

    @Override
    @Transactional
    public void handleDeleteCalendar(Long calendarId) {
        try {
            this.iRoomRepository.deleteLTH_Phong(calendarId);
            this.iCalendarRepository.deleteById(calendarId);
        } catch (Exception e) {
            System.out.println("--->ER error delete both LTH_Phong , calendar" + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public ResponseEntity<String> handleCheckExistCalendar2E(Date date, PracticeCaseEntity practiceCase, UserEntity user, List<Long> listIdRoom) {
        boolean checkFirstExist = this.iCalendarRepository.existsByDateOfCalendarAndUserAndPracticeCase(date, user, practiceCase);
        if (checkFirstExist) {
            return ResponseEntity.badRequest().body("Existed a calendar with Date : " +
                    date.toString() + " , PracticeCase : " +
                    practiceCase.getNamePracticeCase() + " And teacher : " +
                    user.getFirstName() + " " + user.getLastName());
        } else {
            Optional<List<CalendarEntity>> calendarEntities = this.iCalendarRepository.findAllByDateOfCalendarAndPracticeCase(date, practiceCase);
            if (calendarEntities.isPresent()) {
                List<Long> listIdCalendar = new ArrayList<>();
                for (CalendarEntity calendarEntity : calendarEntities.get()) {
                    listIdCalendar.add(calendarEntity.getId());
                }
                ResponseEntity<String> checkSecondExist = this.iRoomRepository.checkExistCalendarAndRoom(listIdCalendar, listIdRoom);
                if (checkSecondExist.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    return ResponseEntity.badRequest().body(checkSecondExist.getBody());
                } else {
                    return ResponseEntity.ok().body("Calendar is empty");
                }

            } else {
                return ResponseEntity.ok().body("Calendar is empty");
            }
        }

    }

    @Override
    public ResponseEntity<String> handleCheckExistCalendar1E(Date date, PracticeCaseEntity practiceCase, List<Long> listIdRoom) {
        Optional<List<CalendarEntity>> calendarEntities = this.iCalendarRepository.findAllByDateOfCalendarAndPracticeCase(date, practiceCase);
        if (calendarEntities.isPresent()) {
            List<Long> listIdCalendar = new ArrayList<>();
            for (CalendarEntity calendarEntity : calendarEntities.get()) {
                listIdCalendar.add(calendarEntity.getId());
            }
            ResponseEntity<String> checkSecondExist = this.iRoomRepository.checkExistCalendarAndRoom(listIdCalendar, listIdRoom);
            if (checkSecondExist.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return ResponseEntity.badRequest().body(checkSecondExist.getBody());
            } else {
                return ResponseEntity.ok().body("Calendar is empty");
            }

        } else {
            return ResponseEntity.ok().body("Calendar is empty");
        }
    }

    @Override
    public CalendarEntity handleGetCalendarById(Long id) {
        return this.iCalendarRepository.findCalendarEntityById(id);
    }
}
