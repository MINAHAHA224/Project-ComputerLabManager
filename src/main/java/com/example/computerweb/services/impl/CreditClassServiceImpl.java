package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.classroomResponse.ClassroomRpDto;
import com.example.computerweb.DTO.dto.creditClassResponse.CreditClassRpDetail;
import com.example.computerweb.DTO.dto.creditClassResponse.CreditClassRpPageIndexDto;
import com.example.computerweb.DTO.dto.semesterResponse.WeekSemesterRpDto;
import com.example.computerweb.DTO.dto.subjectResponse.SubjectRpDto;
import com.example.computerweb.DTO.dto.userResponse.TeacherRpDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseFailure;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.creditClassRequest.CreditClassRqCreateDto;
import com.example.computerweb.DTO.requestBody.creditClassRequest.CreditClassRqUpdateDto;
import com.example.computerweb.models.entity.*;
import com.example.computerweb.repositories.*;
import com.example.computerweb.services.ICreditClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CreditClassServiceImpl implements ICreditClassService {
    private final ICalendarRepository iCalendarRepository;
    private final ICreditClassToRepository iCreditClassToRepository;
    private final IMajorRepository iMajorRepository;
    private final IRoleRepository iRoleRepository;
    private final IUserRepository iUserRepository;
    private final   IWeekSemesterRepository iWeekSemesterRepository;
    private final ICreditClassRepository iCreditClassRepository;
    private final IClassroomRepository  iClassroomRepository;
    private final ISubjectRepository iSubjectRepository;

    @Override
    public ResponseData<?> handleGetDataForCreditIndexPage() {

        List<CreditClassRpPageIndexDto> results = this.iCreditClassRepository.findAllCreditForIndexPage();
        if (results != null && !results.isEmpty()) {

            return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công", results);
        } else {
            return new ResponseSuccess<>(HttpStatus.OK.value(), "Không có dữ liệu", results);
        }

    }

    @Override
    public ResponseData<?> handleGetSubjectForCreditClass() {
        List<SubjectRpDto> listResult = this.iSubjectRepository.findSubjectExistsSoTHH();
        if (listResult!=null && !listResult.isEmpty() ){
            return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công", listResult);
        }
        else {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Không có dữ liệu");
        }
    }

    @Override
    public ResponseData<?> handleGetClassForCreditClass() {
        List<ClassroomRpDto> listAnswer = new ArrayList<>();

        List<ClassroomEntity> listResult = this.iClassroomRepository.findAll();
        for ( ClassroomEntity result : listResult){
            ClassroomRpDto classroomRpDto = new ClassroomRpDto();
            classroomRpDto.setClassroomId(result.getId());
            classroomRpDto.setContent(result.getNameClassroom() +" - SLSV: " + result.getNumberOfStudents());
            listAnswer.add(classroomRpDto);
        }

         if ( listAnswer!=null && !listAnswer.isEmpty()){
             return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công" , listAnswer);
         }else {
             return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Không có dữ liệu" );
         }

    }

    @Override
    public ResponseData<?> handleGetTeacherForCreditClass(String codeSubject) {
        List<TeacherRpDto> listResult = new ArrayList<>();
        // set list teacher
        String characterCodeSubject = codeSubject.substring(0, 3);
        if (characterCodeSubject.equals("INT")) {
            RoleEntity roleGv = this.iRoleRepository.findRoleEntityById(1L).get();
            MajorEntity majorCNTT = this.iMajorRepository.findMajorEntityByCodeMajor("CNTT");
            Optional<List<UserEntity>> allTeacher = this.iUserRepository.findAllByRoleAndMajor(roleGv, majorCNTT);

            if (allTeacher.isPresent()) {
                List<UserEntity> results = allTeacher.get();

                for (UserEntity result : results) {
                    TeacherRpDto answer = new TeacherRpDto();
                    answer.setTeacherId(result.getId());
                    answer.setContent(result.getCodeUser() + " - " + result.getFirstName() + " " + result.getLastName());
                    listResult.add(answer);
                }

            }

        }
        if ( listResult !=null && !listResult.isEmpty()){
            return new ResponseSuccess<>(HttpStatus.OK.value(),"Thực hiện thành công" , listResult);
        }else {
            return  new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Không có dữ liệu");
        }

    }


    @Override
    @Transactional
    public ResponseData<?> handleCreateCreditClass(CreditClassRqCreateDto creditClassRqCreateDto) {

        SubjectEntity subject = this.iSubjectRepository.findSubjectEntityById(creditClassRqCreateDto.getIdSubject());
        UserEntity teacher = this.iUserRepository.findUserEntityById(creditClassRqCreateDto.getTeacherId());
        Long students = creditClassRqCreateDto.getNumberOfStudentLTC();
        Long creditLTC = subject.getAllCredit();
        String group = creditClassRqCreateDto.getGroup();
        ClassroomEntity classroom = this.iClassroomRepository.findClassroomEntityById(creditClassRqCreateDto.getIdClassroom());
        Long numberOfStudents = classroom.getNumberOfStudents();

        // handle Save Create creditClass
        try {
            CreditClassEntity newCreditClass = new CreditClassEntity();
            newCreditClass.setNameCreditClass(creditClassRqCreateDto.getCodeCreditClass());
//            newCreditClass.setCredits(creditClassRqCreateDto.getCreditLTC());
            newCreditClass.setCredits(creditLTC);
            newCreditClass.setGroup(creditClassRqCreateDto.getGroup());
            if ( creditClassRqCreateDto.getNumberOfStudentLTC() <  numberOfStudents){
                return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Số lượng sinh viên không đủ để tạo lớp tín chỉ");
            }
            newCreditClass.setNumberOfStudentsLTC(creditClassRqCreateDto.getNumberOfStudentLTC());
            newCreditClass.setSubject(subject);
            newCreditClass.setClassroom(classroom);
            newCreditClass.setUser(teacher);
            newCreditClass.setNameCreditClass(creditClassRqCreateDto.getCodeCreditClass());
            CreditClassEntity creditClass = this.iCreditClassRepository.save(newCreditClass);


            long loop = 1;
            if (students > 35) {
                loop = (long) Math.ceil(students / 35.0);
            }
            // Group must be 02 or ... , not 01
            if (group.equals("02")) {
                for (long i = 1; i <= loop; i++) {
                    CreditClassToEntity newCreditClassTo = new CreditClassToEntity();
                    newCreditClassTo.setCreditClass(creditClass);
                    newCreditClassTo.setMato("0" + i);
                    this.iCreditClassToRepository.save(newCreditClassTo);
                }
            }
            return new ResponseSuccess<>(HttpStatus.OK.value(), "Tạo lớp tín chỉ thành công");

        } catch (RuntimeException e) {
            System.out.println("--ER save creditClass and creditClassTo" + e.getMessage());
            e.printStackTrace();
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Tạo lớp tín chỉ thất bại");
        }
    }

    @Override
    public ResponseData<?> handleGetDataCreditClassDetails(Long creditClassId) {

        CreditClassRpDetail creditClassRpDetail = new CreditClassRpDetail();
        CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(creditClassId);
        creditClassRpDetail.setCreditClassId(creditClass.getId());
        creditClassRpDetail.setCodeCreditClass(creditClass.getNameCreditClass());
        creditClassRpDetail.setClassroom(creditClass.getClassroom().getNameClassroom());
        creditClassRpDetail.setNumberOfStudentLTC(creditClass.getNumberOfStudentsLTC().toString());
        creditClassRpDetail.setCodeSubject(creditClass.getSubject().getCodeSubject());
        creditClassRpDetail.setTeacher(creditClass.getUser().getFirstName() + " " + creditClass.getUser().getLastName());
        creditClassRpDetail.setGroup(creditClass.getGroup());

        List<CreditClassToEntity> creditClassToEntities = creditClass.getCreditClassToEntities();
        if (creditClassToEntities != null && !creditClassToEntities.isEmpty()) {
            StringJoiner combination = new StringJoiner(",");
            for (CreditClassToEntity creditClassTo : creditClassToEntities) {
                combination.add(creditClassTo.getMato());
            }
            creditClassRpDetail.setCombination(combination.toString());
        } else {
            creditClassRpDetail.setCombination("");
        }
        creditClassRpDetail.setCredit(creditClass.getCredits().toString());


        // set list teacher
        String characterCodeSubject = creditClass.getSubject().getCodeSubject().substring(0, 3);
        if (characterCodeSubject.equals("INT")) {
            RoleEntity roleGv = this.iRoleRepository.findRoleEntityById(1L).get();
            MajorEntity majorCNTT = this.iMajorRepository.findMajorEntityByCodeMajor("CNTT");
            Optional<List<UserEntity>> allTeacher = this.iUserRepository.findAllByRoleAndMajor(roleGv, majorCNTT);
            List<TeacherRpDto> answers = new ArrayList<>();
            if (allTeacher.isPresent()) {
                List<UserEntity> results = allTeacher.get();

                for (UserEntity result : results) {
                    TeacherRpDto answer = new TeacherRpDto();
                    answer.setTeacherId(result.getId());
                    answer.setContent(result.getCodeUser() + " - " + result.getFirstName() + " " + result.getLastName());
                    answers.add(answer);
                }
                creditClassRpDetail.setListTeacher(answers);
            } else {
                creditClassRpDetail.setListTeacher(answers);
            }

        }
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công", creditClassRpDetail);
    }

    @Override
    @Transactional
    public ResponseData<?> handleUpdateCreditClassDetail(CreditClassRqUpdateDto creditClassRqUpdateDto) {
        Long creditClassId = creditClassRqUpdateDto.getCreditClassId();
        Long studentsNew = creditClassRqUpdateDto.getNumberOfStudentLTC();
        Long teacherIdNew = creditClassRqUpdateDto.getTeacherId();
        UserEntity teacherNew = this.iUserRepository.findUserEntityById(teacherIdNew);
        String groupNew = creditClassRqUpdateDto.getGroup().trim();

        CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(creditClassId);
        String groupCurrent = creditClass.getGroup().trim();
        Long studentsCurrent = creditClass.getNumberOfStudentsLTC();

       boolean checkCreditClassHaveCalendar = this.iCalendarRepository.existsByCreditClass(creditClass);
       if ( !groupNew.equals(groupCurrent) || !Objects.equals(studentsNew, studentsCurrent) ){
           if(checkCreditClassHaveCalendar){
               return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Đã lên lịch lớp tín chỉ, không thể thay đổi học viên hoặc nhóm");
           }
       }
        //set teacher
        creditClass.setUser(teacherNew);


        if ( !groupNew.equals("02")){
            try {
                // delete creditClassTo first
                this.iCreditClassToRepository.deleteByCreditClass(creditClass);

                // then save creditClass
                creditClass.setNumberOfStudentsLTC(studentsNew);
                creditClass.setGroup(groupNew);

                this.iCreditClassRepository.save(creditClass);
                return new ResponseSuccess<>(HttpStatus.OK.value(), "Cập nhật thành công lớp tín chỉ");
            }catch (RuntimeException e){
                System.out.println("--ER groupNew != 02 delete  and save creditClass" +e.getMessage());
                e.printStackTrace();
            }

        }else {
            if (!Objects.equals(studentsNew, studentsCurrent)){
                long loop = 1;
                if (studentsNew > 35) {
                    loop = (long) Math.ceil(studentsNew / 35.0);
                }
                try {
                    // delete creditClassTo current
                    this.iCreditClassToRepository.deleteByCreditClass(creditClass);
                    // tai vi dang trong 1 transaction => xoa , no se ko kip , neu ko co dong nay ma save
                    // luon se gay loi duplicate , ( tuc la no chua xoa xong ma da save cai moi r )
                    // flush() bat no xoa ngay NHUNG chi xoa trong bo nho dem => neu co loi van rollback duoc
                    // co the sai @Modifying dat o sau @Transaction de thang spring no quan ly tu dong cung duoc
                    // nhung de chu dong thi sai flush()
                    this.iCreditClassToRepository.flush();
                    // then save creditClass
                    creditClass.setNumberOfStudentsLTC(studentsNew);
                    creditClass.setGroup(groupNew);

                    this.iCreditClassRepository.save(creditClass);

                    for (long i = 1; i <= loop; i++) {
                        CreditClassToEntity newCreditClassTo = new CreditClassToEntity();
                        newCreditClassTo.setCreditClass(creditClass);
                        newCreditClassTo.setMato("0" + i);
                        this.iCreditClassToRepository.save(newCreditClassTo);
                    }
                    return new ResponseSuccess<>(HttpStatus.OK.value(), "Cập nhật thành công lớp tín chỉ");

                }catch (RuntimeException e) {
                    System.out.println("--ER save creditClass after that check groupNew == 02 but " +
                            "numberOfStudentNew != numberOfStudentOld delete and save creditClassTo again" + e.getMessage());
                    e.printStackTrace();
                }


            }
        }

        return null;
    }

    @Override
    @Transactional
    public ResponseData<?> handleDeleteCreditClass(Long creditClassId) {
       CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(creditClassId);
        // check first
        boolean checkCreditClassHaveCalendar  = this.iCalendarRepository.existsByCreditClass(creditClass);
        if (!checkCreditClassHaveCalendar ){
            try {
                // handle delete
                this.iCreditClassRepository.deleteById(creditClassId);
                return  new ResponseSuccess<>(HttpStatus.OK.value(),"Xóa lớp tín chỉ thành công");
            }catch (RuntimeException e){
                System.out.println("--ER handleDeleteCreditClass " +e.getMessage());
                e.printStackTrace();
            }

        }else {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(),  "Đã lên lịch lớp tín chỉ, không thể xóa");
        }
        return null;
    }
}
