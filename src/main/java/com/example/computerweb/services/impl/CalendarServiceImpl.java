package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.CalendarManagementDto;
import com.example.computerweb.DTO.dto.CalendarResponseDto;
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
    public Map<String, Map<String ,String>> handleGetDataForCreatePage() {
        //purposeUse
        Map<String , String> purposeUse = PurposeUse.getPurposeUse();
        //teachers
        RoleEntity roleEntity = this.iRoleRepository.findById(1).get();
        List<UserEntity> users = this.iUserRepository.findAllByRole(roleEntity);
        Map<String, String> teachers = new HashMap<>();
        for ( UserEntity user : users ){
            teachers.put(user.getId().toString() , user.getFirstName() +" "+user.getLastName());
        }
        // rooms
        List<RoomEntity> roomEntities = this.iRoomRepository.findAll();
        Map<String , String> rooms = new HashMap<>();
        for ( RoomEntity roomEntity : roomEntities ){
            rooms.put(roomEntity.getId().toString(), roomEntity.getNameRoom() +" - (" +roomEntity.getNumberOfComputers().toString() +" slot)" );
        }
        // classrooms
        List<ClassroomEntity> classroomEntities = this.iClassroomRepository.findAll();
        Map<String , String> classrooms = new HashMap<>();
        for ( ClassroomEntity classroomEntity : classroomEntities ){
            classrooms.put(classroomEntity.getId().toString(), classroomEntity.getNameClassroom() +" - (" +classroomEntity.getNumberOfStudents().toString() +" student)" );
        }
        // practiceCases
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        List<PracticeCaseEntity> practiceCaseEntities = this.iPracticeCaseRepository.findAll();
        Map<String , String> practiceCases = new HashMap<>();
        for ( PracticeCaseEntity practiceCaseEntity : practiceCaseEntities ){
            practiceCases.put(practiceCaseEntity.getId().toString(), practiceCaseEntity.getNamePracticeCase() +" - (" +practiceCaseEntity.getTimeStart().format(dateTimeFormat) +" - " + practiceCaseEntity.getTimeEnd().format(dateTimeFormat) +")" );
        }
        // subjects
        List<SubjectEntity> subjectEntities = this.iSubjectRepository.findAll();
        Map<String , String> subjects = new HashMap<>();
        for ( SubjectEntity subjectEntity : subjectEntities ){
            subjects.put(subjectEntity.getId().toString(), subjectEntity.getNameSubject() +" - (" +subjectEntity.getSoTTH().toString() +" credit)" );
        }

        Map<String , Map<String , String >> datas = new TreeMap<>();
        datas.put("purposeUses" ,purposeUse);
        datas.put("teachers" ,teachers);
        datas.put("rooms" ,rooms);
        datas.put("classrooms" ,classrooms);
        datas.put("practiceCases" ,practiceCases);
        datas.put("subjects" ,subjects);

        return datas;
    }

    @Override
    public CalendarResponseDto handleGetDataForUpdatePage(Long calendarId) {
        Map<String, Map<String ,String>> data = handleGetDataForCreatePage();
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
        CalendarResponseDto calendarResponseDto = new CalendarResponseDto();
        calendarResponseDto.setData(data);
        calendarResponseDto.setCalendarManagementDto(calendarManagementDto);

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
