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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarServiceImpl implements ICalendarService {


private  final ICalendarRepository iCalendarRepository;
private final IUserRepository  iUserRepository;
private  final IRoleRepository iRoleRepository;
private final IRoomRepository iRoomRepository;
private final IClassroomRepository iClassroomRepository;
private final IPracticeCaseRepository iPracticeCaseRepository;
private  final ISubjectRepository iSubjectRepository;
    @Override
    public List<CalendarManagementDto> handleGetAllDataCalendar() {
        List<CalendarManagementDto> results = this.iCalendarRepository.findAllCustom();
        return results;
    }

    @Override
    public CalendarResponseFields handleGetDataForCreatePage() {
        //purposeUse
        Map<String , String> purposeUse = PurposeUse.getPurposeUse();
        //teachers
        RoleEntity roleEntity = this.iRoleRepository.findById(1).get();
        List<UserEntity> users = this.iUserRepository.findAllByRole(roleEntity);
        Map<String, String> teachers = new TreeMap<>();
        for ( UserEntity user : users ){
            teachers.put(user.getId().toString() , user.getFirstName() +" "+user.getLastName());
        }
        // rooms
        List<RoomEntity> roomEntities = this.iRoomRepository.findAll();
        Map<String , Map<String,String>> rooms = new TreeMap<>();
        for ( RoomEntity roomEntity : roomEntities ){
            Map<String,String> roomDetails = new TreeMap<>();
            roomDetails.put("name" ,roomEntity.getNameRoom() );
            roomDetails.put("quantity" , roomEntity.getNumberOfComputers().toString());
            rooms.put(roomEntity.getId().toString(),roomDetails);
        }
        // classrooms
        List<ClassroomEntity> classroomEntities = this.iClassroomRepository.findAll();
        Map<String , Map<String,String>> classrooms = new TreeMap<>();
        for ( ClassroomEntity classroomEntity : classroomEntities ){
            Map<String,String> classroomDetails = new TreeMap<>();
            classroomDetails.put("name" , classroomEntity.getNameClassroom());
            classroomDetails.put("quantity" ,classroomEntity.getNumberOfStudents().toString() );
            classrooms.put(classroomEntity.getId().toString(),classroomDetails );
        }
        // practiceCases
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        List<PracticeCaseEntity> practiceCaseEntities = this.iPracticeCaseRepository.findAll();
        Map<String , Map<String,String>> practiceCases = new TreeMap<>();
        for ( PracticeCaseEntity practiceCaseEntity : practiceCaseEntities ){
            Map<String,String>  practiceCaseDetails = new TreeMap<>();
            practiceCaseDetails.put("name" ,practiceCaseEntity.getNamePracticeCase() );
            practiceCaseDetails.put("timeStart" ,practiceCaseEntity.getTimeStart().format(dateTimeFormat) );
            practiceCaseDetails.put("timeEnd" ,practiceCaseEntity.getTimeEnd().format(dateTimeFormat) );
            practiceCases.put(practiceCaseEntity.getId().toString(), practiceCaseDetails );
        }
        // subjects
        List<SubjectEntity> subjectEntities = this.iSubjectRepository.findAll();
        Map<String , Map<String,String>> subjects = new TreeMap<>();
        for ( SubjectEntity subjectEntity : subjectEntities ){
            Map<String,String> subjectDetails = new TreeMap<>();
            subjectDetails.put("name" ,subjectEntity.getNameSubject() );
            subjectDetails.put("credit" ,subjectEntity.getSoTTH().toString() );
            subjects.put(subjectEntity.getId().toString(), subjectDetails );
        }

        Map<String , Map<String , String >> field = new TreeMap<>();
        field.put("purposeUses" ,purposeUse);
        field.put("teachers" ,teachers);
        Map<String , Map<String , Map<String,String> >> fields = new TreeMap<>();
        fields.put("rooms" ,rooms);
        fields.put("classrooms" ,classrooms);
        fields.put("practiceCases" ,practiceCases);
        fields.put("subjects" ,subjects);
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
    public void handleCreateCalendar(CalendarRequestDto calendarRequestDto) {
        try {
            CalendarEntity calendarEntity = new CalendarEntity();
            calendarEntity.setDateOfCalendar(DateUtils.convertToDate(calendarRequestDto.getDate()));
            calendarEntity.setNoteCalendar(calendarRequestDto.getPurposeUse().toString());
            UserEntity teacher = this.iUserRepository.findUserEntityById(calendarRequestDto.getTeacherId());
            calendarEntity.setUser(teacher);
            ClassroomEntity classroom = this.iClassroomRepository.findClassroomEntityById(calendarRequestDto.getClassroomId());
            calendarEntity.setClassroom(classroom);
            PracticeCaseEntity practiceCase = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarRequestDto.getPracticeCaseId());
            calendarEntity.setPracticeCase(practiceCase);
            SubjectEntity subjectEntity = this.iSubjectRepository.findSubjectEntityById(calendarRequestDto.getSubjectId());
            calendarEntity.setSubject(subjectEntity);
            CalendarEntity savedCalendar =  this.iCalendarRepository.save(calendarEntity);
            Long calendarId = savedCalendar.getId();
            this.iRoomRepository.saveLTH_Phong(calendarId , calendarRequestDto.getRoomId());

        }catch (Exception e){
            System.out.println("---> ER :" + e.getMessage());
            e.printStackTrace();
        }

    }
    @Transactional
    @Override
    public void handleUpdateCalendar(CalendarRequestDto calendarRequestDto) {
        try {
            // Must delete LTH_Phong first , then delete calendar because The DELETE statement conflicted with the REFERENCE constraint
            this.iRoomRepository.deleteLTH_Phong(calendarRequestDto.getId());
            this.iCalendarRepository.deleteById(calendarRequestDto.getId());
            CalendarEntity calendarEntity = new CalendarEntity();
            calendarEntity.setDateOfCalendar(DateUtils.convertToDate(calendarRequestDto.getDate()));
            calendarEntity.setNoteCalendar(calendarRequestDto.getPurposeUse().toString());
            UserEntity teacher = this.iUserRepository.findUserEntityById(calendarRequestDto.getTeacherId());
            calendarEntity.setUser(teacher);
            ClassroomEntity classroom = this.iClassroomRepository.findClassroomEntityById(calendarRequestDto.getClassroomId());
            calendarEntity.setClassroom(classroom);
            PracticeCaseEntity practiceCase = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarRequestDto.getPracticeCaseId());
            calendarEntity.setPracticeCase(practiceCase);
            SubjectEntity subjectEntity = this.iSubjectRepository.findSubjectEntityById(calendarRequestDto.getSubjectId());
            calendarEntity.setSubject(subjectEntity);
            CalendarEntity savedCalendar =  this.iCalendarRepository.save(calendarEntity);
            Long calendarId = savedCalendar.getId();
            this.iRoomRepository.saveLTH_Phong(calendarId , calendarRequestDto.getRoomId());

        }catch (Exception e){
            System.out.println("---> ER :" + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    @Transactional
    public void handleDeleteCalendar(Long calendarId) {
        try {
            this.iRoomRepository.deleteLTH_Phong(calendarId);
            this.iCalendarRepository.deleteById(calendarId);
        }catch (Exception e){
            System.out.println("--->ER error delete both LTH_Phong , calendar" +e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public CalendarEntity handleGetCalendarById(Long id) {
        return this.iCalendarRepository.findCalendarEntityById(id);
    }
}
