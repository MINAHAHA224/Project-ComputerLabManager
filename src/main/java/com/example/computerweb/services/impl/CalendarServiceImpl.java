package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.calendarResponse.CalendarManagementDto;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseDto;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseFields;
import com.example.computerweb.DTO.dto.calendarResponse.CalendarResponseOneDto;
import com.example.computerweb.DTO.dto.creditClassResponse.CreditClassEligibleDto;
import com.example.computerweb.DTO.dto.semesterResponse.SemesterYearDto;
import com.example.computerweb.DTO.dto.semesterResponse.WeekTimeDto;
import com.example.computerweb.DTO.reponseBody.ResponseData;
import com.example.computerweb.DTO.reponseBody.ResponseFailure;
import com.example.computerweb.DTO.reponseBody.ResponseSuccess;
import com.example.computerweb.DTO.requestBody.calendarRequest.CalendarRequestDetailDto;
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
import org.springframework.http.HttpStatus;
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
    private final IFacilityRepository iFacilityRepository;

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
//        List<RoomEntity> roomEntities = this.iRoomRepository.findAll();
//        ArrayList<Map<String, String>> arrayRoom= new ArrayList<>();
//        for (RoomEntity roomEntity : roomEntities) {
//            Map<String, String> roomDetails = new TreeMap<>();
//            roomDetails.put("idRoom", roomEntity.getId().toString());
//            roomDetails.put("quantity", roomEntity.getNumberOfComputers().toString());
//            roomDetails.put("facility", roomEntity.getFacility().getNameFacility());
//            arrayRoom.add(roomDetails);
//        }
//        calendarResponseFields.setRoom(arrayRoom);


        //Facility
        List<FacilityEntity>  facilityEntities = this.iFacilityRepository.findAll();
        ArrayList<Map<String, String>> arrayFacility= new ArrayList<>();
        for (FacilityEntity facility : facilityEntities) {
            Map<String, String> facilityDetails = new TreeMap<>();
            facilityDetails.put("idFacility", facility.getId().toString());
            facilityDetails.put("nameFacility", facility.getNameFacility());
            arrayFacility.add(facilityDetails);
        }
        calendarResponseFields.setFacility(arrayFacility);

        // semesterYear
        List<SemesterYearDto>  listResult = this.iWeekSemesterRepository.findAllSemesterYear();
        ArrayList<Map<String, String>> arrayResult= new ArrayList<>();
        for (SemesterYearDto result  : listResult) {
            Map<String, String> resultDetails = new TreeMap<>();
            resultDetails.put("idSemesterYear",result.getIdSemester() +"-"+result.getIdYear());
            resultDetails.put("content", " Học kỳ "+result.getIdSemester()+" năm học " + result.getIdYear());
            arrayResult.add(resultDetails);
        }
        calendarResponseFields.setSemesterYear(arrayResult);


        return calendarResponseFields;
    }



    @Override
    public ArrayList<Map<String, String>> handleWeekStudyForCreateCreditClass(String semesterYear) {



        // week_semester  on 2024-2025
        List<WeekTimeDto> listResult = this.iWeekSemesterRepository.findAllWeekTimeOfSemesterYear(semesterYear.trim());
        ArrayList<Map<String, String>> arrayWeekTime = new ArrayList<>();
        for (WeekTimeDto result : listResult) {
            Map<String, String> resultDetails = new TreeMap<>();
            resultDetails.put("idWeekTime",result.getIdWeekTime());
            resultDetails.put("time", "Tuần " + result.getWeek() + " [Từ " + DateUtils.convertToString(result.getTimeBegin()) + " đến " + DateUtils.convertToString(result.getTimeEnd()) + "]");
            arrayWeekTime.add(resultDetails);
        }

        return arrayWeekTime;
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
//        List<RoomEntity> roomEntities = this.iRoomRepository.findAll();
//        ArrayList<Map<String, String>> arrayRoom= new ArrayList<>();
//        for (RoomEntity roomEntity : roomEntities) {
//            Map<String, String> roomDetails = new TreeMap<>();
//            roomDetails.put("idRoom", roomEntity.getId().toString());
//            roomDetails.put("quantity", roomEntity.getNumberOfComputers().toString());
//            roomDetails.put("facility", roomEntity.getFacility().getNameFacility());
//            arrayRoom.add(roomDetails);
//        }
//        calendarResponseFields.setRoom(arrayRoom);
        //Facility
        List<FacilityEntity>  facilityEntities = this.iFacilityRepository.findAll();
        ArrayList<Map<String, String>> arrayFacility= new ArrayList<>();
        for (FacilityEntity facility : facilityEntities) {
            Map<String, String> facilityDetails = new TreeMap<>();
            facilityDetails.put("idFacility", facility.getId().toString());
            facilityDetails.put("nameFacility", facility.getNameFacility());
            arrayFacility.add(facilityDetails);
        }
        calendarResponseFields.setFacility(arrayFacility);


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

//    @Override
//    @Transactional
//    public ResponseData<?> handleCreateCalendar(CalendarRequestDto calendarRequestDto) {
//        //LTC
//        CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(calendarRequestDto.getCreditClassId());
//        //SLSV
//        Long numberOfStudent = creditClass.getNumberOfStudentsLTC();
//        // CoSo
//        FacilityEntity facility = this.iFacilityRepository.findFacilityEntityById(calendarRequestDto.getIdFacility());
//        // Danh sach phong cua co so
//        List<RoomEntity> listRoom = this.iRoomRepository.findRoomEntitiesByFacility(facility);
//
//        // check credit class if lessonCurrent = lessonDb can't create calendar
//        List<CreditClassEligibleDto> listCredit = this.iCreditClassRepository.findAllCreditClassEligible();
//        for ( CreditClassEligibleDto credit : listCredit ){
//            if ( credit.getCreditClassId().equals(calendarRequestDto.getCreditClassId().toString())    && credit.getLessonHave().equals(credit.getLessonSum()) ){
//                return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Credit created full , please choose another credit");
//            }
//        }
//
//        Map<String , String> messageIfCreateSuccessButRoomHaveComputerError = new HashMap<>();
//
//
//        List<CalendarRequestDetailDto> listCalendarCreate = calendarRequestDto.getCalendarDetail();
//        try {
//            for (CalendarRequestDetailDto calendarCreate : listCalendarCreate) {
//
//                WeekSemesterEntity weekSemester = this.iWeekSemesterRepository.findWeekSemesterEntityById(calendarCreate.getWeekSemesterId()) ;
//                Long day = calendarCreate.getDayId();
//                PracticeCaseEntity practiceCase = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarCreate.getPracticeCaseBeginId());
//                Long allCase = calendarCreate.getAllCase();
//                String note = calendarCreate.getPurposeUse();
//                String toHop = "0"+ calendarCreate.getGroupId();
//                // 6 : Active
//                StatusEntity status = this.iStatusRepository.findStatusEntityById(6L);
//                CalendarEntity newCalendar = new CalendarEntity();
//
//                // check Number of case must greater than allCase
//                Long countPracticeCase = this.iPracticeCaseRepository.count();
//                if ( countPracticeCase - practiceCase.getId() + 1 < allCase){
//                    return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Number of case must greater than allCase");
//                }
//
//
//                // choose the room can create
//                Iterator<RoomEntity> iterator = listRoom.iterator();
//                while (iterator.hasNext()){
//                    RoomEntity room = iterator.next();
//                    boolean checkCalendarExistRoom = this.iCalendarRepository.existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus(weekSemester,day,practiceCase,room,status);
//                    if ( ! checkCalendarExistRoom){
//                        newCalendar.setRoom(room);
//                        break;
//                    }
//                }
//                // if does not have any room to create calendar
//                if ( newCalendar.getRoom()== null){
//                    throw new  CalendarException("Calendar : " +
//                            day + " , Week : "+weekSemester.getWeekStudy() + " Time : [" +
//                           DateUtils.convertToString(weekSemester.getDateBegin())
//                           + "]-["
//                           + DateUtils.convertToString(weekSemester.getDateEnd())
//                           + "]"
//                           + " , PracticeCaseBegin : "
//                           + practiceCase.getNamePracticeCase() + "does not have Room empty , please choose Another "
//                   );
//                }
//
//                // set data
//                newCalendar.setCreditClass(creditClass);
//                newCalendar.setWeekSemester(weekSemester);
//                newCalendar.setDay(day);
//                newCalendar.setPracticeCase(practiceCase);
//                newCalendar.setAllCase(allCase);
//                newCalendar.setNoteCalendar(note);
//                newCalendar.setGroup(toHop);
//                newCalendar.setStatus(status);
//                try {
//                    this.iCalendarRepository.save(newCalendar);
//                }catch (RuntimeException e){
//                    throw new  CalendarException("Existed  calendar with day : " +
//                           day + " , Week : "+weekSemester.getWeekStudy() + " Time : [" +
//                           DateUtils.convertToString(weekSemester.getDateBegin())
//                           + "]-["
//                           + DateUtils.convertToString(weekSemester.getDateEnd())
//                           + "]"
//                           + " , PracticeCaseBegin : "
//                           + practiceCase.getNamePracticeCase() + " room : " + newCalendar.getRoom().getNameRoom()
//                   );
//                }
//
//
//            }
//
//            return  new ResponseSuccess<>(HttpStatus.OK.value(), "Create calendar success"  );
//        } catch (RuntimeException e) {
//            System.out.println("--ER handleCreateCalendar " + e.getMessage());
//            e.printStackTrace();
//            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Create calendar fail");
//        }
//
//
//
//
//    }

    @Override
    @Transactional
    public ResponseData<?> handleCreateCalendar(CalendarRequestDto calendarRequestDto) {
        //LTC
        CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(calendarRequestDto.getCreditClassId());
        //SLSV
        Long numberOfStudent = creditClass.getNumberOfStudentsLTC();
        // CoSo
        FacilityEntity facility = this.iFacilityRepository.findFacilityEntityById(calendarRequestDto.getIdFacility());
        // Danh sach phong cua co so
        List<RoomEntity> listRoom = this.iRoomRepository.findRoomEntitiesByFacility(facility);

        // check credit class if lessonCurrent = lessonDb can't create calendar
        List<CreditClassEligibleDto> listCredit = this.iCreditClassRepository.findAllCreditClassEligible();
        for ( CreditClassEligibleDto credit : listCredit ){
            if ( credit.getCreditClassId().equals(calendarRequestDto.getCreditClassId().toString())    && credit.getLessonHave().equals(credit.getLessonSum()) ){
                return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Lớp tín chỉ đã đủ tín , vui lòng chọn lớp tín chỉ khác");
            }
        }

        Map<String , String> messageIfCreateSuccessButRoomHaveComputerError = new HashMap<>();

        //begin check test

        Long totalStudentsInCreditClass = creditClass.getNumberOfStudentsLTC();
        Integer numberOfGroupsToSchedule = calendarRequestDto.getCalendarDetail().size();

        long baseStudentsPerGroup = 0L;
        long remainingStudentsToDistribute = 0L;

        if (numberOfGroupsToSchedule != 0) {
            baseStudentsPerGroup = totalStudentsInCreditClass / numberOfGroupsToSchedule;
            remainingStudentsToDistribute = totalStudentsInCreditClass % numberOfGroupsToSchedule;
        } else {
            // Xử lý fallback, có thể throw exception hoặc gán giá trị mặc định

            throw new CalendarException("Số lượng nhóm phải lớn hơn 0 để thực hiện lịch.");
        }

        // end check test


        List<CalendarRequestDetailDto> listCalendarCreate = calendarRequestDto.getCalendarDetail();
        try {
            for (int i = 0; i < numberOfGroupsToSchedule; i++) {
                CalendarRequestDetailDto calendarCreate = listCalendarCreate.get(i);

                long studentsForThisBooking = baseStudentsPerGroup + (i < remainingStudentsToDistribute ? 1 : 0);

                if (studentsForThisBooking == 0) { // Nếu lớp tín chỉ không có SV, hoặc đã phân bổ hết
                    continue;
                }

                // Lấy thông tin cần thiết từ calendarCreate
                WeekSemesterEntity weekSemester = this.iWeekSemesterRepository.findWeekSemesterEntityById(calendarCreate.getWeekSemesterId());
                Long day = calendarCreate.getDayId();
                PracticeCaseEntity practiceCase = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarCreate.getPracticeCaseBeginId());
                Long allCase = calendarCreate.getAllCase(); // Cần xem xét logic này nếu allCase > 1, phòng phải trống cho tất cả các ca đó
                String note = calendarCreate.getPurposeUse();
                String toHop = "0" + calendarCreate.getGroupId();
                StatusEntity status = this.iStatusRepository.findStatusEntityById(6L); // Active
                // check Number of case must greater than allCase
                Long countPracticeCase = this.iPracticeCaseRepository.count();
                if ( countPracticeCase - practiceCase.getId() + 1 < allCase){
                    return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Number of case must greater than allCase");
                }
                // --- LOGIC CHỌN PHÒNG BẮT ĐẦU TỪ ĐÂY ---
                RoomEntity selectedRoom = null;
                boolean usedRoomWithBrokenComputers = false;

                // Lấy danh sách phòng có sẵn tại thời điểm đó (chưa bị đặt)
                List<RoomEntity> availableRoomsForSlot = new ArrayList<>();
                for (RoomEntity roomCandidate : listRoom) { // listRoom là phòng của cơ sở đã fetch ở đầu
                    // TODO: Nếu allCase > 1, bạn cần kiểm tra phòng trống cho TẤT CẢ các ca liên tiếp.
                    // Ví dụ: nếu practiceCase là Ca1, allCase là 2, thì phải check Ca1 và Ca2.
                    // Đoạn code dưới đây giả sử chỉ check cho practiceCaseBeginId.
                    boolean checkCalendarExistRoom = this.iCalendarRepository.existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus(
                            weekSemester, day, practiceCase, roomCandidate, status
                    );
                    if (!checkCalendarExistRoom) {
                        availableRoomsForSlot.add(roomCandidate);
                    }
                }

                if (availableRoomsForSlot.isEmpty()) {
                    throw new CalendarException("Không còn phòng trống tại cơ sở " + facility.getNameFacility() +
                            " cho lịch: Thứ " + day + ", Tuần " + weekSemester.getWeekStudy() +
                            ", Ca " + practiceCase.getNamePracticeCase());
                }

                // Ưu tiên phòng:
                // 1. Đủ máy hoạt động, không có máy hỏng
                // 2. Đủ máy hoạt động, có máy hỏng
                // Sắp xếp danh sách phòng trống để dễ chọn
                availableRoomsForSlot.sort((r1, r2) -> {
                    long workingMachinesR1 = r1.getNumberOfComputers() - r1.getNumberOfComputerError();
                    long workingMachinesR2 = r2.getNumberOfComputers() - r2.getNumberOfComputerError();

                    // Ưu tiên phòng đủ máy cho số sinh viên hiện tại
                    boolean r1CanFit = workingMachinesR1 >= studentsForThisBooking;
                    boolean r2CanFit = workingMachinesR2 >= studentsForThisBooking;

                    if (r1CanFit && !r2CanFit) return -1;
                    if (!r1CanFit && r2CanFit) return 1;
                    if (!r1CanFit && !r2CanFit) { // Cả 2 đều không đủ máy hoạt động, ưu tiên phòng có nhiều máy hoạt động hơn
                        return Long.compare(workingMachinesR2, workingMachinesR1); // Nhiều hơn thì tốt hơn (ưu tiên)
                    }

                    // Cả 2 đều đủ máy hoạt động
                    // Ưu tiên phòng không có máy hỏng
                    if (r1.getNumberOfComputerError() == 0 && r2.getNumberOfComputerError() != 0) return -1;
                    if (r1.getNumberOfComputerError() != 0 && r2.getNumberOfComputerError() == 0) return 1;

                    // Cả 2 đều có máy hỏng (hoặc đều không có), ưu tiên phòng có nhiều máy hoạt động hơn (chỗ ngồi thoải mái hơn)
                    // hoặc ít máy hỏng hơn nếu số máy hoạt động bằng nhau
                    if (workingMachinesR1 != workingMachinesR2) {
                        return Long.compare(workingMachinesR2, workingMachinesR1); // Nhiều máy hoạt động hơn thì tốt hơn
                    }
                    return Long.compare(r1.getNumberOfComputerError(), r2.getNumberOfComputerError()); // Ít máy hỏng hơn thì tốt hơn
                });

                // Chọn phòng đầu tiên trong danh sách đã sắp xếp mà đáp ứng được
                for (RoomEntity room : availableRoomsForSlot) {
                    long workingComputers = room.getNumberOfComputers() - room.getNumberOfComputerError();
                    if (workingComputers >= studentsForThisBooking) {
                        selectedRoom = room;
                        if (room.getNumberOfComputerError() > 0) {
                            usedRoomWithBrokenComputers = true;
                        }
                        break; // Đã tìm được phòng phù hợp
                    }
                }

                // --- KẾT THÚC LOGIC CHỌN PHÒNG ---

                if (selectedRoom == null) {
                    // Không có phòng nào đủ SỐ MÁY HOẠT ĐỘNG cho studentsForThisBooking
                    // Đây là trường hợp bạn cần quyết định:
                    // 1. Báo lỗi luôn (khuyến nghị)
                    // 2. (Không khuyến nghị) Nếu user yêu cầu "buộc phải xét": tìm phòng có TỔNG SỐ MÁY >= studentsForThisBooking
                    //    nhưng SỐ MÁY HOẠT ĐỘNG < studentsForThisBooking. Điều này có nghĩa là có SV không có máy.
                    //    Nếu theo hướng này, bạn cần thêm 1 vòng lặp nữa qua availableRoomsForSlot
                    //    để tìm phòng có room.getNumberOfComputers() >= studentsForThisBooking và cảnh báo cực mạnh.
                    //    Hiện tại, logic đang theo hướng báo lỗi nếu không đủ MÁY HOẠT ĐỘNG.
                    throw new CalendarException("Không tìm thấy phòng nào có đủ " + studentsForThisBooking +
                            " máy hoạt động cho lịch: Thứ " + day + ", Tuần " + weekSemester.getWeekStudy() +
                            ", Ca " + practiceCase.getNamePracticeCase() + " (Nhóm " + toHop + ").");
                }

                // Tạo CalendarEntity
                CalendarEntity newCalendar = new CalendarEntity();
                newCalendar.setCreditClass(creditClass);
                newCalendar.setWeekSemester(weekSemester);
                newCalendar.setDay(day);
                newCalendar.setPracticeCase(practiceCase);
                newCalendar.setAllCase(allCase);
                newCalendar.setNoteCalendar(note);
                newCalendar.setGroup(toHop);
                newCalendar.setStatus(status);
                newCalendar.setRoom(selectedRoom); // Gán phòng đã chọn

                // Ghi nhận cảnh báo nếu cần
                if (usedRoomWithBrokenComputers) {
                    String warningKey = String.format("Nhóm %s (Lịch: Thứ %d, Tuần %s, Ca %s)",
                            toHop, day, weekSemester.getWeekStudy(), practiceCase.getNamePracticeCase());
                    String warningMessage = String.format("Phòng %s được chọn có %d máy, trong đó %d máy hỏng. Số máy hoạt động: %d (Đủ cho %d SV).",
                            selectedRoom.getNameRoom(), selectedRoom.getNumberOfComputers(),
                            selectedRoom.getNumberOfComputerError(),
                            (selectedRoom.getNumberOfComputers() - selectedRoom.getNumberOfComputerError()),
                            studentsForThisBooking);
                    messageIfCreateSuccessButRoomHaveComputerError.put(warningKey, warningMessage);
                    System.out.println("warningKey : " + warningKey );
                    System.out.println("warningMessage : " + warningMessage );
                }

                try {
                    this.iCalendarRepository.save(newCalendar);
                    // Hoặc add vào newCalendarsToSave rồi saveAll ở cuối
                    this.iCalendarRepository.flush(); // Ép flush để xem lỗi ngay
                } catch (RuntimeException e) { // Bắt DataIntegrityViolationException nếu có unique constraint
                    // Xử lý lỗi nếu có unique constraint bị vi phạm (ví dụ: đã có lịch khác được tạo đồng thời)
                    // Hoặc bạn có thể đã check existsBy... ở trên, nhưng đây là một safeguard.
                    throw new CalendarException("Lỗi khi lưu lịch cho Nhóm " + toHop +
                            ": Thứ " + day + ", Tuần " + weekSemester.getWeekStudy() +
                            ", Ca " + practiceCase.getNamePracticeCase() + ", Phòng " + selectedRoom.getNameRoom() +
                            ". Lịch có thể đã tồn tại hoặc có lỗi cơ sở dữ liệu. Chi tiết: " + e.getMessage());
                }
            } // Kết thúc vòng lặp for qua listCalendarCreate

            return  new ResponseSuccess<>(HttpStatus.OK.value(), "Tạo lịch thành công"  );
        }catch (CalendarException e){

            e.printStackTrace();
            throw  new CalendarException(e.getMessage());
            //return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
        catch (RuntimeException e) {
            System.out.println("--ER handleCreateCalendar " + e.getMessage());
            e.printStackTrace();
            throw  new RuntimeException(e.getMessage());
           // return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Create calendar fail");
        }




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
                return ResponseEntity.ok().body("Tạo lịch mượn phòng thành công");
            }else {
                throw new CalendarException("Đã tồn tại lịch vào Thứ: " +
                        day + ", Tuần: " + weekSemester.getWeekStudy() + " Thời gian: [" +
                        DateUtils.convertToString(weekSemester.getDateBegin()) +
                        "]-[" +
                        DateUtils.convertToString(weekSemester.getDateEnd()) +
                        "]" +
                        ", Bắt đầu tại ca thực hành: " +
                        practiceCase.getNamePracticeCase()
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
    public ResponseData<?> handleUpdateCalendar(CalendarRequestOneDto calendarRequestOneDto) {
        CalendarEntity calendar = this.iCalendarRepository.findCalendarEntityById(calendarRequestOneDto.getCalendarId());
        WeekSemesterEntity weekSemesterNew = this.iWeekSemesterRepository.findWeekSemesterEntityById(calendarRequestOneDto.getWeekSemesterId());
        Long dayNew = calendarRequestOneDto.getDayId();
        PracticeCaseEntity practiceCaseNew = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarRequestOneDto.getPracticeCaseBeginId());
        RoomEntity room =  calendar.getRoom();
        String noteNew = calendarRequestOneDto.getPurposeUse() != null ? calendarRequestOneDto.getPurposeUse() : "";
        StatusEntity status = this.iStatusRepository.findStatusEntityByNameStatus("ACTIVE");


        // check Number of case must greater than allCase
        Long countPracticeCase = this.iPracticeCaseRepository.count();
        Long allCase = calendar.getAllCase();
        if ( countPracticeCase - practiceCaseNew.getId() + 1 < allCase){
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Số lượng tiết từ tiết bắt đầu phải lớn hơn tổng tiết");
        }

        // check exists

        boolean checkFirst = this.iCalendarRepository.existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus( weekSemesterNew, dayNew , practiceCaseNew ,room , status);


        try {
            // checkFirst
            if (!checkFirst) {

                calendar.setDay(dayNew);
                calendar.setPracticeCase(practiceCaseNew);

                calendar.setWeekSemester(weekSemesterNew);
                calendar.setNoteCalendar(noteNew);
                this.iCalendarRepository.save(calendar);
                return new ResponseSuccess<>(HttpStatus.OK.value(),"Cập nhật lịch thành công" );
            } else {
                throw new CalendarException("Đã tồn tại lịch vào Thứ: " +
                        dayNew + ", Tuần: " + weekSemesterNew.getWeekStudy() + " Thời gian: [" +
                        DateUtils.convertToString(weekSemesterNew.getDateBegin()) +
                        "]-[" +
                        DateUtils.convertToString(weekSemesterNew.getDateEnd()) +
                        "]" +
                        ", Bắt đầu tại ca thực hành: " +
                        practiceCaseNew.getNamePracticeCase()
                );

            }
        } catch (CalendarException e) {
            System.out.println("---> ER handleUpdateCalendar :" + e.getMessage());
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
            throw new CalendarException("Không thể xóa lịch Id : " + calendarId);
        }
    }
}
