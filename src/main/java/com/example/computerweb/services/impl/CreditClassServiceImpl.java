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
import com.example.computerweb.exceptions.DataConflictException;
import com.example.computerweb.exceptions.DataNotFoundException;
import com.example.computerweb.models.entity.*;
import com.example.computerweb.repositories.*;
import com.example.computerweb.services.ICreditClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
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
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công", results);
    }

    @Override
    public ResponseData<?> handleGetSubjectForCreditClass() {
        List<SubjectRpDto> listResult = this.iSubjectRepository.findSubjectExistsSoTHH();
        return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công", listResult);
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

        return new ResponseSuccess<>(HttpStatus.OK.value(), "Thực hiện thành công" , listAnswer);

    }

    @Override
    public ResponseData<?> handleGetTeacherForCreditClass(String codeSubject) {
        List<TeacherRpDto> listResult = new ArrayList<>();
        // set list teacher
        if ( codeSubject== null || codeSubject.isEmpty()){
            throw new DataNotFoundException("Mã môn học đang bị trống dữ liệu");
        }
        String characterCodeSubject = codeSubject.substring(0, 3);
        if (characterCodeSubject.equals("INT")) {
            RoleEntity roleGv = this.iRoleRepository.findRoleEntityById(1L).orElseThrow(
                    () -> new DataNotFoundException("Lỗi cấu hình: Không tìm thấy vai trò Giảng Viên.")
            );

            MajorEntity majorCNTT = this.iMajorRepository.findMajorEntityByCodeMajor("CNTT");
             if (majorCNTT == null ){
                 throw  new DataNotFoundException("Lỗi cấu hình: Không tìm thấy chuyên ngành CNTT.");
             }
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
            //co the thay bang doan logic sau
            // Tìm, map và trả về kết quả trong một câu lệnh
//            List<TeacherRpDto> listResult = iUserRepository.findAllByRoleAndMajor(roleGv, majorCNTT)
//                    .map(users -> users.stream()
//                            .map(user -> new TeacherRpDto(user.getId(), user.getCodeUser() + " - " + user.getFirstName() + " " + user.getLastName()))
//                            .collect(Collectors.toList()))
//                    .orElse(Collections.emptyList()); // Dùng Collections.emptyList() hiệu quả hơn new ArrayList<>()



        }
        return new ResponseSuccess<>(HttpStatus.OK.value(),"Thực hiện thành công" , listResult);

    }


//    @Override
//    @Transactional
//    public ResponseData<?> handleCreateCreditClass(CreditClassRqCreateDto creditClassRqCreateDto) {
//
//        SubjectEntity subject = this.iSubjectRepository.findSubjectEntityById(creditClassRqCreateDto.getIdSubject());
//        UserEntity teacher = this.iUserRepository.findUserEntityById(creditClassRqCreateDto.getTeacherId());
//        Long students = creditClassRqCreateDto.getNumberOfStudentLTC();
//        Long creditLTC = subject.getAllCredit();
//        String group = creditClassRqCreateDto.getGroup();
//        ClassroomEntity classroom = this.iClassroomRepository.findClassroomEntityById(creditClassRqCreateDto.getIdClassroom());
//        Long numberOfStudents = classroom.getNumberOfStudents();
//
//        // handle Save Create creditClass
//        try {
//            CreditClassEntity newCreditClass = new CreditClassEntity();
//            newCreditClass.setNameCreditClass(creditClassRqCreateDto.getCodeCreditClass());
////            newCreditClass.setCredits(creditClassRqCreateDto.getCreditLTC());
//            newCreditClass.setCredits(creditLTC);
//            newCreditClass.setGroup(creditClassRqCreateDto.getGroup());
//            if ( creditClassRqCreateDto.getNumberOfStudentLTC() <  numberOfStudents){
//                return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Số lượng sinh viên không đủ để tạo lớp tín chỉ");
//            }
//            newCreditClass.setNumberOfStudentsLTC(creditClassRqCreateDto.getNumberOfStudentLTC());
//            newCreditClass.setSubject(subject);
//            newCreditClass.setClassroom(classroom);
//            newCreditClass.setUser(teacher);
//            newCreditClass.setNameCreditClass(creditClassRqCreateDto.getCodeCreditClass());
//            CreditClassEntity creditClass = this.iCreditClassRepository.save(newCreditClass);
//
//
//            long loop = 1;
//            if (students > 35) {
//                loop = (long) Math.ceil(students / 35.0);
//            }
//            // Group must be 02 or ... , not 01
//            if (group.equals("02")) {
//                for (long i = 1; i <= loop; i++) {
//                    CreditClassToEntity newCreditClassTo = new CreditClassToEntity();
//                    newCreditClassTo.setCreditClass(creditClass);
//                    newCreditClassTo.setMato("0" + i);
//                    this.iCreditClassToRepository.save(newCreditClassTo);
//                }
//            }
//            return new ResponseSuccess<>(HttpStatus.OK.value(), "Tạo lớp tín chỉ thành công");
//
//        } catch (RuntimeException e) {
//            System.out.println("--ER save creditClass and creditClassTo" + e.getMessage());
//            e.printStackTrace();
//            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Tạo lớp tín chỉ thất bại");
//        }
//    }
@Override
@Transactional
public ResponseData<?> handleCreateCreditClass(CreditClassRqCreateDto creditClassRqCreateDto) {
    // === BƯỚC 1: VALIDATE SỰ TỒN TẠI CỦA CÁC THỰC THỂ LIÊN QUAN ===
    // Ném ra DataNotFoundException nếu không tìm thấy, GlobalExceptionHandler sẽ bắt và trả về lỗi 404.

    SubjectEntity subject = iSubjectRepository.findById(creditClassRqCreateDto.getIdSubject())
            .orElseThrow(() -> new DataNotFoundException("Không tìm thấy Môn học với ID: " + creditClassRqCreateDto.getIdSubject()));

    UserEntity teacher = iUserRepository.findById(creditClassRqCreateDto.getTeacherId())
            .orElseThrow(() -> new DataNotFoundException("Không tìm thấy Giáo viên với ID: " + creditClassRqCreateDto.getTeacherId()));

    ClassroomEntity classroom = iClassroomRepository.findById(creditClassRqCreateDto.getIdClassroom())
            .orElseThrow(() -> new DataNotFoundException("Không tìm thấy Lớp học với ID: " + creditClassRqCreateDto.getIdClassroom()));

    boolean checkExistSubjectAndClassroom = this.iCreditClassRepository.existsCreditClassEntitiesBySubjectAndAndClassroom(subject , classroom);
    if ( checkExistSubjectAndClassroom){
        throw  new DataConflictException("Đã tồn tại lớp tín chỉ với lớp "+classroom.getNameClassroom()+ " - môn học "+ subject.getNameSubject());
    }
    // === BƯỚC 2: KIỂM TRA CÁC QUY TẮC NGHIỆP VỤ ===
    // Ném ra các exception nghiệp vụ cụ thể nếu vi phạm.

    // Ví dụ: Kiểm tra sĩ số lớp tín chỉ không được lớn hơn sĩ số của lớp học chính quy.
    if (creditClassRqCreateDto.getNumberOfStudentLTC().compareTo(classroom.getNumberOfStudents()) > 0) {
        throw new DataConflictException(String.format(
                "Sĩ số lớp tín chỉ (%d) không được lớn hơn sĩ số lớp học chính quy '%s' (%d).",
                creditClassRqCreateDto.getNumberOfStudentLTC(),
                classroom.getNameClassroom(),
                classroom.getNumberOfStudents()
        ));
    }

    // Có thể thêm các quy tắc khác ở đây, ví dụ:
    // if (iCreditClassRepository.existsByNameCreditClass(creditClassRqCreateDto.getCodeCreditClass())) {
    //     throw new DataConflictException("Mã lớp tín chỉ đã tồn tại.");
    // }

    // === BƯỚC 3: TẠO VÀ LƯU LỚP TÍN CHỈ ===
    CreditClassEntity newCreditClass = new CreditClassEntity();
    newCreditClass.setNameCreditClass(creditClassRqCreateDto.getCodeCreditClass());
    newCreditClass.setCredits(subject.getAllCredit()); // Lấy tín chỉ từ môn học
    newCreditClass.setGroup(creditClassRqCreateDto.getGroup());
    newCreditClass.setNumberOfStudentsLTC(creditClassRqCreateDto.getNumberOfStudentLTC());
    newCreditClass.setSubject(subject);
    newCreditClass.setClassroom(classroom);
    newCreditClass.setUser(teacher);
    // Giả sử SoTcDc (số tín chỉ đã cấp) mặc định là 0, đã được set ở Entity hoặc DB.

    // Lưu và lấy lại thực thể đã được quản lý bởi JPA (có ID)
    CreditClassEntity savedCreditClass = iCreditClassRepository.save(newCreditClass);

    // === BƯỚC 4: TẠO CÁC TỔ HỢP (NẾU CẦN) ===
    String group = creditClassRqCreateDto.getGroup().trim();
    // Logic tạo tổ hợp chỉ áp dụng khi nhóm là '02' (hoặc các nhóm thực hành khác)
    if ("02".equals(group)) {
        createCombinationsForClass(savedCreditClass);
    }

    // Nếu mọi thứ thành công, trả về ResponseSuccess
    return new ResponseSuccess<>(HttpStatus.OK.value(), "Tạo lớp tín chỉ thành công");
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

//    @Override
//    @Transactional
//    public ResponseData<?> handleUpdateCreditClassDetail(CreditClassRqUpdateDto creditClassRqUpdateDto) {
//        Long creditClassId = creditClassRqUpdateDto.getCreditClassId();
//        Long studentsNew = creditClassRqUpdateDto.getNumberOfStudentLTC();
//        Long teacherIdNew = creditClassRqUpdateDto.getTeacherId();
//        UserEntity teacherNew = this.iUserRepository.findUserEntityById(teacherIdNew);
//        String groupNew = creditClassRqUpdateDto.getGroup().trim();
//
//        CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(creditClassId);
//        String groupCurrent = creditClass.getGroup().trim();
//        Long studentsCurrent = creditClass.getNumberOfStudentsLTC();
//
//       boolean checkCreditClassHaveCalendar = this.iCalendarRepository.existsByCreditClass(creditClass);
//       if ( !groupNew.equals(groupCurrent) || !Objects.equals(studentsNew, studentsCurrent) ){
//           if(checkCreditClassHaveCalendar){
//               return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Đã lên lịch lớp tín chỉ, không thể thay đổi học viên hoặc nhóm");
//           }
//       }
//        //set teacher
//        creditClass.setUser(teacherNew);
//
//
//        if ( !groupNew.equals("02")){
//            try {
//                // delete creditClassTo first
//                this.iCreditClassToRepository.deleteByCreditClass(creditClass);
//
//                // then save creditClass
//                creditClass.setNumberOfStudentsLTC(studentsNew);
//                creditClass.setGroup(groupNew);
//
//                this.iCreditClassRepository.save(creditClass);
//                return new ResponseSuccess<>(HttpStatus.OK.value(), "Cập nhật thành công lớp tín chỉ");
//            }catch (RuntimeException e){
//                System.out.println("--ER groupNew != 02 delete  and save creditClass" +e.getMessage());
//                e.printStackTrace();
//            }
//
//        }else {
//            if (!Objects.equals(studentsNew, studentsCurrent)){
//                long loop = 1;
//                if (studentsNew > 35) {
//                    loop = (long) Math.ceil(studentsNew / 35.0);
//                }
//                try {
//                    // delete creditClassTo current
//                    this.iCreditClassToRepository.deleteByCreditClass(creditClass);
//                    // tai vi dang trong 1 transaction => xoa , no se ko kip , neu ko co dong nay ma save
//                    // luon se gay loi duplicate , ( tuc la no chua xoa xong ma da save cai moi r )
//                    // flush() bat no xoa ngay NHUNG chi xoa trong bo nho dem => neu co loi van rollback duoc
//                    // co the sai @Modifying dat o sau @Transaction de thang spring no quan ly tu dong cung duoc
//                    // nhung de chu dong thi sai flush()
//                    this.iCreditClassToRepository.flush();
//                    // then save creditClass
//                    creditClass.setNumberOfStudentsLTC(studentsNew);
//                    creditClass.setGroup(groupNew);
//
//                    this.iCreditClassRepository.save(creditClass);
//
//                    for (long i = 1; i <= loop; i++) {
//                        CreditClassToEntity newCreditClassTo = new CreditClassToEntity();
//                        newCreditClassTo.setCreditClass(creditClass);
//                        newCreditClassTo.setMato("0" + i);
//                        this.iCreditClassToRepository.save(newCreditClassTo);
//                    }
//                    return new ResponseSuccess<>(HttpStatus.OK.value(), "Cập nhật thành công lớp tín chỉ");
//
//                }catch (RuntimeException e) {
//                    System.out.println("--ER save creditClass after that check groupNew == 02 but " +
//                            "numberOfStudentNew != numberOfStudentOld delete and save creditClassTo again" + e.getMessage());
//                    e.printStackTrace();
//                }
//
//
//            }
//        }
//
//        return null;
//    }
@Override
@Transactional
public ResponseData<?> handleUpdateCreditClassDetail(CreditClassRqUpdateDto creditClassRqUpdateDto) {
    // === BƯỚC 1: VALIDATE SỰ TỒN TẠI CỦA CÁC THỰC THỂ LIÊN QUAN ===
    Long creditClassId = creditClassRqUpdateDto.getCreditClassId();
    CreditClassEntity creditClass = iCreditClassRepository.findById(creditClassId)
            .orElseThrow(() -> new DataNotFoundException("Không tìm thấy Lớp tín chỉ với ID: " + creditClassId));

    UserEntity newTeacher = iUserRepository.findById(creditClassRqUpdateDto.getTeacherId())
            .orElseThrow(() -> new DataNotFoundException("Không tìm thấy Giáo viên với ID: " + creditClassRqUpdateDto.getTeacherId()));

    // === BƯỚC 2: KIỂM TRA CÁC QUY TẮC NGHIỆP VỤ ===
    Long newStudents = creditClassRqUpdateDto.getNumberOfStudentLTC();
    String newGroup = creditClassRqUpdateDto.getGroup().trim();

    boolean isGroupOrStudentChanged = !creditClass.getGroup().trim().equals(newGroup) ||
            !creditClass.getNumberOfStudentsLTC().equals(newStudents);

    // Nếu sĩ số hoặc nhóm thay đổi, phải kiểm tra xem lớp đã được xếp lịch chưa.
    if (isGroupOrStudentChanged) {
        boolean hasCalendar = iCalendarRepository.existsByCreditClass(creditClass);
        if (hasCalendar) {
            throw new DataConflictException("Lớp tín chỉ đã được xếp lịch, không thể thay đổi sĩ số hoặc nhóm thực hành.");
        }
    }

    // (Optional) Kiểm tra sĩ số mới có hợp lệ so với lớp chính quy không
    if (newStudents < 15) {
        throw new DataConflictException("Số lượng sinh viên phải ít nhất 15 mới có thể mở lớp tín chỉ");
    }

    // === BƯỚC 3: CẬP NHẬT THÔNG TIN LỚP TÍN CHỈ ===
    creditClass.setUser(newTeacher); // Luôn cho phép cập nhật giáo viên
    creditClass.setNumberOfStudentsLTC(newStudents);
    creditClass.setGroup(newGroup);

    // Lưu các thay đổi cơ bản vào DB
    CreditClassEntity updatedCreditClass = iCreditClassRepository.save(creditClass);

    // === BƯỚC 4: TÁI TẠO CÁC TỔ HỢP NẾU CẦN ===
    // Nếu sĩ số hoặc nhóm thay đổi, ta cần tái tạo lại danh sách tổ hợp
    if (isGroupOrStudentChanged) {
        // Xóa tất cả các tổ hợp cũ
        iCreditClassToRepository.deleteByCreditClass(updatedCreditClass);

        // Tạo lại các tổ hợp mới dựa trên thông tin mới
        if ("02".equals(newGroup)) { // Hoặc logic khác để xác định nhóm thực hành
            createCombinationsForClass(updatedCreditClass);
        }
    }

    return new ResponseSuccess<>(HttpStatus.OK.value(), "Cập nhật lớp tín chỉ thành công");
}


    @Override
    @Transactional
    public ResponseData<?> handleDeleteCreditClass(Long creditClassId) {
        if (creditClassId == null) {
            throw new DataNotFoundException("Không tìm thấy lớp tín chỉ với ID = null ");
        }
       CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(creditClassId);
        if (creditClass == null) {
            throw new DataNotFoundException("Không tìm thấy lớp tín chỉ với ID = " +creditClassId );
        }

        // check first
        boolean checkCreditClassHaveCalendar  = this.iCalendarRepository.existsByCreditClass(creditClass);
        if (!checkCreditClassHaveCalendar ){
            try {
                // handle delete
                this.iCreditClassRepository.deleteById(creditClassId);
                return  new ResponseSuccess<>(HttpStatus.OK.value(),"Xóa lớp tín chỉ thành công");
            }catch (DataIntegrityViolationException e ){
            log.error("--ER handleDeleteCreditClass : {}" , e.getMessage(),e);
            throw new DataConflictException("Lớp tín chỉ đã được lên lịch không thể xóa.");
        }

        }else {
            throw  new DataConflictException("Lớp tín chỉ đã được lên lịch không thể xóa");
        }
    }



    private void createCombinationsForClass(CreditClassEntity creditClass) {
        long students = creditClass.getNumberOfStudentsLTC();
        if (students <= 0) {
            return; // Không cần tạo tổ hợp nếu không có sinh viên
        }

        // Logic tính số tổ hợp: mỗi tổ hợp tối đa 35 sinh viên.
        // Dùng Math.ceil để làm tròn lên.
        // Ví dụ: 36 sinh viên -> 2 tổ; 70 sinh viên -> 2 tổ; 71 sinh viên -> 3 tổ.
        long numberOfCombinations = (long) Math.ceil((double) students / 35.0);

        for (int i = 1; i <= numberOfCombinations; i++) {
            CreditClassToEntity newCombination = new CreditClassToEntity();
            newCombination.setCreditClass(creditClass);
            // Định dạng mã tổ hợp, ví dụ: "01", "02", ...
            newCombination.setMato(String.format("%02d", i));
            iCreditClassToRepository.save(newCombination);
        }
}
}
