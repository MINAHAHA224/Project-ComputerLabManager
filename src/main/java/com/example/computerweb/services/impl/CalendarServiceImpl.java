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
import com.example.computerweb.DTO.requestBody.calendarRequest.*;
import com.example.computerweb.exceptions.CalendarException;
import com.example.computerweb.models.entity.*;
import com.example.computerweb.models.enums.Day;
import com.example.computerweb.models.enums.PracticeCase;
import com.example.computerweb.repositories.*;
import com.example.computerweb.services.ICalendarService;
import com.example.computerweb.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public ResponseData<?> handleCreateCalendarAuto(CalendarRequestDto calendarRequestDto) {
        CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(calendarRequestDto.getCreditClassId());
        if (creditClass == null) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy lớp tín chỉ với ID: " + calendarRequestDto.getCreditClassId());
        }

        List<CreditClassEligibleDto> creditClassEligibleDtos = this.iCreditClassRepository.findAllCreditClassEligible();
        for ( CreditClassEligibleDto creditClassEligibleDto :  creditClassEligibleDtos){
            boolean check1 = Long.valueOf(creditClassEligibleDto.getCreditClassId()).equals(creditClass.getId());
            boolean check2 = Long.parseLong(creditClassEligibleDto.getLessonHave()) != 0;
            Long test1= Long.valueOf(creditClassEligibleDto.getCreditClassId());
            Long test2 = Long.parseLong(creditClassEligibleDto.getLessonHave());
            if ( check1  && check2 ){
                return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Tính năng sắp lịch tự động chỉ sử dụng khi lịch chưa được phân tiết học nào");
            }
        }

        FacilityEntity facility = this.iFacilityRepository.findFacilityEntityById(calendarRequestDto.getIdFacility());
        if (facility == null) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy cơ sở với ID: " + calendarRequestDto.getIdFacility());
        }

        List<RoomEntity> allRoomsInFacility = this.iRoomRepository.findRoomEntitiesByFacility(facility);
        if (allRoomsInFacility.isEmpty()) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Cơ sở " + facility.getNameFacility() + " không có phòng thực hành nào.");
        }

        CreditClassEligibleDto creditInfoForLessonCount = null;
        List<CreditClassEligibleDto> listCredit = this.iCreditClassRepository.findAllCreditClassEligible(); // Giữ tên biến gốc
        for (CreditClassEligibleDto credit : listCredit) {
            if (credit.getCreditClassId().equals(calendarRequestDto.getCreditClassId().toString())) {
                creditInfoForLessonCount = credit;
                if (credit.getLessonHave().equals(credit.getLessonSum())) {
                    return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Lớp tín chỉ " + creditClass.getNameCreditClass() + " đã đủ số tiết, vui lòng chọn lớp tín chỉ khác");
                }
                break;
            }
        }
        if (creditInfoForLessonCount == null) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy thông tin số tiết cho lớp tín chỉ ID: " + calendarRequestDto.getCreditClassId());
        }

        long lessonsAlreadyHad = Long.parseLong(creditInfoForLessonCount.getLessonHave());
        long totalLessonsRequired = Long.parseLong(creditInfoForLessonCount.getLessonSum());
        long lessonsToSchedule = totalLessonsRequired - lessonsAlreadyHad;

        if (lessonsToSchedule <= 0) {
            return new ResponseSuccess<>(HttpStatus.OK.value(), "Lớp tín chỉ '" + creditClass.getNameCreditClass() + "' đã đủ tiết hoặc không cần xếp thêm.");
        }

        Long allCasePerSession = calendarRequestDto.getAllCasePerSession();
        if (allCasePerSession == null || allCasePerSession <= 0) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Số tiết mỗi buổi (allCase) phải lớn hơn 0.");
        }

        int numberOfIterations = (int) Math.ceil((double) lessonsToSchedule / allCasePerSession);
        if (numberOfIterations <= 0) {
            return new ResponseSuccess<>(HttpStatus.OK.value(), "Không cần vòng lặp nào để xếp lịch (số tiết cần xếp hoặc số tiết mỗi buổi không hợp lệ).");
        }
        System.out.println(String.format("LTC ID %d (%s): Cần xếp %d tiết, mỗi buổi %d tiết => Cần %d buổi học.",
                creditClass.getId(), creditClass.getNameCreditClass(), lessonsToSchedule, allCasePerSession, numberOfIterations));

        Long totalStudentsInCreditClass = creditClass.getNumberOfStudentsLTC(); // Giữ tên biến gốc
        if (totalStudentsInCreditClass == null || totalStudentsInCreditClass < 0) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Số lượng sinh viên của lớp tín chỉ (" + creditClass.getNameCreditClass() + ") không hợp lệ: " + totalStudentsInCreditClass);
        }

        int numberOfGroupsToSchedule; // Giữ tên biến gốc
        if (totalStudentsInCreditClass == 0) {
            numberOfGroupsToSchedule = 0;
            System.out.println("Lớp tín chỉ ID " + creditClass.getId() + " (" + creditClass.getNameCreditClass() + ") không có sinh viên, sẽ không tạo lịch tự động.");
        } else {
            numberOfGroupsToSchedule = (int) Math.ceil((double) totalStudentsInCreditClass / 35.0);
        }

        System.out.println(String.format("LTC ID %d (%s): Tổng SV: %d, Số nhóm cần tạo mỗi buổi: %d (tối đa 35 SV/nhóm).",
                creditClass.getId(), creditClass.getNameCreditClass(), totalStudentsInCreditClass, numberOfGroupsToSchedule));

        if (numberOfGroupsToSchedule == 0 && totalStudentsInCreditClass > 0) {
            return new ResponseFailure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi logic: Lớp tín chỉ " + creditClass.getNameCreditClass() + " có sinh viên nhưng số nhóm tính ra là 0.");
        }
        if (numberOfGroupsToSchedule == 0 && totalStudentsInCreditClass == 0) {
            return new ResponseSuccess<>(HttpStatus.OK.value(), "Lớp tín chỉ " + creditClass.getNameCreditClass() + " không có sinh viên, không tạo lịch nào.");
        }

        long baseStudentsPerGroup = 0L; // Giữ tên biến gốc
        long remainingStudentsToDistribute = 0L; // Giữ tên biến gốc

        if (numberOfGroupsToSchedule > 0 ) { // totalStudentsInCreditClass > 0 đã được bao hàm nếu numberOfGroupsToSchedule > 0
            baseStudentsPerGroup = totalStudentsInCreditClass / numberOfGroupsToSchedule;
            remainingStudentsToDistribute = totalStudentsInCreditClass % numberOfGroupsToSchedule;
        }

        StatusEntity statusActive = this.iStatusRepository.findStatusEntityById(6L);
        if (statusActive == null) {
            return new ResponseFailure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Không tìm thấy trạng thái 'Active' trong hệ thống (ID: 6).");
        }

        List<CalendarEntity> allSuccessfullyScheduledCalendars = new ArrayList<>();
        Map<String, List<long[]>> tempOccupiedSlotsByRoom = new HashMap<>();
        Map<String, String> messageIfCreateSuccessButRoomHasNonActiveComputers = new HashMap<>(); // Giữ tên biến gốc

        try {
            for (int iter = 0; iter < numberOfIterations; iter++) {
                System.out.println(String.format("--- Bắt đầu xếp lịch cho buổi học thứ %d/%d ---", iter + 1, numberOfIterations));
                List<CalendarEntity> calendarsForThisIteration = new ArrayList<>();
                boolean iterationSuccessful = true;

                for (int groupIdx = 0; groupIdx < numberOfGroupsToSchedule; groupIdx++) {
                    String toHop = "02-" + (iter + 1) ; // Giữ tên biến gốc, thêm logic
                    String groupIdentifierLog = String.format("Buổi %d - Nhóm %s", iter + 1, toHop);

                    long studentsForThisBooking; // Giữ tên biến gốc
                    if (groupIdx < remainingStudentsToDistribute) {
                        studentsForThisBooking = baseStudentsPerGroup + 1;
                    } else {
                        studentsForThisBooking = baseStudentsPerGroup;
                    }

                    if (studentsForThisBooking == 0 && totalStudentsInCreditClass > 0) {
                        // Điều này chỉ xảy ra nếu totalStudents < numberOfGroupsToSchedule
                        // Ví dụ 10 SV, chia làm 20 nhóm (do logic tính numberOfGroupsToSchedule có vấn đề)
                        System.out.println(groupIdentifierLog + ": được phân bổ 0 sinh viên. Bỏ qua nhóm này trong buổi học.");
                        // Nếu tất cả các nhóm phải có sinh viên, đây có thể là lỗi và iterationSuccessful = false;
                        continue;
                    }

                    WeekSemesterEntity initialWeekSemester = this.iWeekSemesterRepository.findWeekSemesterEntityById(calendarRequestDto.getStartWeekSemesterId());
                    if (initialWeekSemester == null) {
                        throw new CalendarException(String.format("%s: Không tìm thấy Tuần-Học kỳ bắt đầu với ID: %d", groupIdentifierLog, calendarRequestDto.getStartWeekSemesterId()));
                    }
                    Long semesterIdForLTC = initialWeekSemester.getSemesterStudy();
                    Long initialWeekNumber = initialWeekSemester.getWeekStudy();
                    if (semesterIdForLTC == null || initialWeekNumber == null) {
                        throw new CalendarException(String.format("%s: Tuần-Học kỳ bắt đầu (ID: %d) thiếu thông tin semesterStudy hoặc weekStudy.", groupIdentifierLog, initialWeekSemester.getId()));
                    }

                    Long targetWeekNumber = initialWeekNumber + iter;
                    WeekSemesterEntity currentWeekSemester = this.iWeekSemesterRepository
                            .findBySemesterStudyAndWeekStudy(semesterIdForLTC, targetWeekNumber)
                            .orElseThrow(() -> new CalendarException(
                                    String.format("%s: Không tìm thấy Tuần-Học kỳ cho Tuần thứ %d của học kỳ ID %d.", groupIdentifierLog, targetWeekNumber, semesterIdForLTC)
                            ));

                    Long day = calendarRequestDto.getDayId(); // Giữ tên biến gốc
                    PracticeCaseEntity practiceCaseBegin = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarRequestDto.getPracticeCaseBeginId()); // Giữ tên
                    if (practiceCaseBegin == null) {
                        throw new CalendarException(String.format("%s: Không tìm thấy Tiết thực hành bắt đầu với ID: %d", groupIdentifierLog, calendarRequestDto.getPracticeCaseBeginId()));
                    }

                    String note = calendarRequestDto.getPurposeUse(); // Giữ tên

                    List<PracticeCaseEntity> practiceCasesForBooking; // Giữ tên
                    List<PracticeCaseEntity> potentialCases = iPracticeCaseRepository.findPracticeCasesStartingFromId(
                            practiceCaseBegin.getId(), PageRequest.of(0, allCasePerSession.intValue()));

                    if (potentialCases.size() < allCasePerSession) {
                        throw new CalendarException(String.format("%s: Không tìm thấy đủ %d tiết TH liên tục từ tiết '%s' (ID: %d). Hệ thống chỉ tìm thấy %d tiết.",
                                groupIdentifierLog, allCasePerSession, practiceCaseBegin.getNamePracticeCase(), practiceCaseBegin.getId(), potentialCases.size()));
                    }
                    for (int k = 0; k < potentialCases.size(); k++) { // Giữ tên
                        PracticeCaseEntity currentFetchedCase = potentialCases.get(k);
                        Long expectedId = practiceCaseBegin.getId() + k;
                        if (!currentFetchedCase.getId().equals(expectedId)) {
                            String previousCaseInfo = (k > 0) ? String.format("Tiết trước đó trong chuỗi là '%s' (ID %d). ", potentialCases.get(k - 1).getNamePracticeCase(), potentialCases.get(k - 1).getId()) : "";
                            throw new CalendarException(String.format("%s: Tính liên tục của ID các tiết TH bị gián đoạn. %sTiết thứ %d (từ '%s' - ID %d) mong đợi ID là %d, nhưng thực tế tìm thấy tiết '%s' với ID %d.",
                                    groupIdentifierLog, previousCaseInfo, k + 1, practiceCaseBegin.getNamePracticeCase(), practiceCaseBegin.getId(), expectedId, currentFetchedCase.getNamePracticeCase(), currentFetchedCase.getId()));
                        }
                    }
                    practiceCasesForBooking = potentialCases;
                    long currentBookingStartPcId = practiceCasesForBooking.get(0).getId();
                    long currentBookingEndPcId = practiceCasesForBooking.get(practiceCasesForBooking.size() - 1).getId();

                    RoomEntity selectedRoom = null; // Giữ tên
                    boolean usedRoomWithNonActiveComputersFlag = false; // Giữ tên, reset cho mỗi nhóm
                    List<RoomEntity> availableRoomsForSlotAndDuration = new ArrayList<>(); // Giữ tên

                    for (RoomEntity roomCandidate : allRoomsInFacility) {
                        boolean isRoomTrulyFree = true;
                        String roomTimeSlotKey = roomCandidate.getId() + "_" + currentWeekSemester.getId() + "_" + day;

                        List<CalendarEntity> existingBookingsInRoom = iCalendarRepository.findAllByWeekSemesterAndDayAndRoomAndStatus(
                                currentWeekSemester, day, roomCandidate, statusActive); // Giữ tên
                        if (!existingBookingsInRoom.isEmpty()) {
                            for (CalendarEntity existingBooking : existingBookingsInRoom) {
                                PracticeCaseEntity existingBookingStartPcEntity = existingBooking.getPracticeCase();
                                Long existingBookingAllCase = existingBooking.getAllCase(); // Giữ tên
                                if (existingBookingStartPcEntity == null || existingBookingAllCase == null || existingBookingAllCase <= 0) {
                                    System.err.println(String.format("Cảnh báo (%s): Bỏ qua lịch DB không hợp lệ (ID: %d) phòng %s.",
                                            groupIdentifierLog, existingBooking.getId(), roomCandidate.getNameRoom()));
                                    continue;
                                }
                                long existingDbBookingStartPcId = existingBookingStartPcEntity.getId();
                                long existingDbBookingEndPcId = existingDbBookingStartPcId + existingBookingAllCase - 1;
                                if (doIntervalsOverlap(currentBookingStartPcId, currentBookingEndPcId, existingDbBookingStartPcId, existingDbBookingEndPcId)) {
                                    isRoomTrulyFree = false; break;
                                }
                            }
                        }
                        if (!isRoomTrulyFree) continue;

                        List<long[]> tempOccupiedIntervalsInSlot = tempOccupiedSlotsByRoom.get(roomTimeSlotKey);
                        if (tempOccupiedIntervalsInSlot != null && !tempOccupiedIntervalsInSlot.isEmpty()) {
                            for (long[] tempInterval : tempOccupiedIntervalsInSlot) {
                                if (doIntervalsOverlap(currentBookingStartPcId, currentBookingEndPcId, tempInterval[0], tempInterval[1])) {
                                    isRoomTrulyFree = false; break;
                                }
                            }
                        }
                        if (isRoomTrulyFree) {
                            availableRoomsForSlotAndDuration.add(roomCandidate);
                        }
                    }

                    String practiceCaseNamesForErrorMessage = practiceCasesForBooking.stream().map(PracticeCaseEntity::getNamePracticeCase).collect(Collectors.joining(", ")); // Giữ tên
                    if (availableRoomsForSlotAndDuration.isEmpty()) {
                        System.err.println(String.format("%s: Không tìm được phòng trống nào cho lịch: Thứ %d, Tuần %d (ID %d, Tuần học số %d), Tiết %s.",
                                groupIdentifierLog, day, currentWeekSemester.getWeekStudy(), currentWeekSemester.getId(), targetWeekNumber, practiceCaseNamesForErrorMessage));
                        iterationSuccessful = false;
                        break;
                    }

                    availableRoomsForSlotAndDuration.sort((r1, r2) -> { // Giữ logic sort
                        long activeMachinesR1 = r1.getNumberOfComputerActive() != null ? r1.getNumberOfComputerActive() : 0;
                        long activeMachinesR2 = r2.getNumberOfComputerActive() != null ? r2.getNumberOfComputerActive() : 0;
                        boolean r1CanFit = activeMachinesR1 >= studentsForThisBooking;
                        boolean r2CanFit = activeMachinesR2 >= studentsForThisBooking;
                        if (r1CanFit && !r2CanFit) return -1; if (!r1CanFit && r2CanFit) return 1;
                        if (!r1CanFit && !r2CanFit) return Long.compare(activeMachinesR2, activeMachinesR1);
                        boolean r1AllComputersActive = r1.getNumberOfComputers().equals(activeMachinesR1);
                        boolean r2AllComputersActive = r2.getNumberOfComputers().equals(activeMachinesR2);
                        if (r1AllComputersActive && !r2AllComputersActive) return -1; if (!r1AllComputersActive && r2AllComputersActive) return 1;
                        if (activeMachinesR1 != activeMachinesR2) return Long.compare(activeMachinesR2, activeMachinesR1);
                        return Long.compare(r2.getNumberOfComputers(), r1.getNumberOfComputers());
                    });

                    for (RoomEntity room : availableRoomsForSlotAndDuration) { // Giữ tên
                        if ((room.getNumberOfComputerActive() != null ? room.getNumberOfComputerActive() : 0) >= studentsForThisBooking) {
                            selectedRoom = room;
                            if (room.getNumberOfComputerActive() < room.getNumberOfComputers()) {
                                usedRoomWithNonActiveComputersFlag = true;
                            }
                            break;
                        }
                    }

                    if (selectedRoom == null) { // Giữ tên
                        System.err.println(String.format("%s: Không tìm được phòng đủ %d máy hoạt động cho lịch: Thứ %d, Tuần %d (ID %d, Tuần học số %d), Tiết %s.",
                                groupIdentifierLog, studentsForThisBooking, day, currentWeekSemester.getWeekStudy(), currentWeekSemester.getId(), targetWeekNumber, practiceCaseNamesForErrorMessage));
                        iterationSuccessful = false;
                        break;
                    }

                    String selectedRoomTimeSlotKey = selectedRoom.getId() + "_" + currentWeekSemester.getId() + "_" + day;
                    tempOccupiedSlotsByRoom.computeIfAbsent(selectedRoomTimeSlotKey, k -> new ArrayList<>())
                            .add(new long[]{currentBookingStartPcId, currentBookingEndPcId});

                    CalendarEntity newCalendar = new CalendarEntity(); // Giữ tên
                    newCalendar.setCreditClass(creditClass);
                    newCalendar.setWeekSemester(currentWeekSemester);
                    newCalendar.setDay(day);
                    newCalendar.setPracticeCase(practiceCaseBegin);
                    newCalendar.setAllCase(allCasePerSession);
                    newCalendar.setNoteCalendar(note);
                    newCalendar.setGroup(toHop);
                    newCalendar.setStatus(statusActive);
                    newCalendar.setRoom(selectedRoom);
                    calendarsForThisIteration.add(newCalendar);

                    if (usedRoomWithNonActiveComputersFlag) { // Giữ tên
                        String warningKey = String.format("Buổi %d - Nhóm %s (Lịch: Thứ %d, Tuần %d (ID %d, Tuần học số %d), Tiết %s)",
                                iter + 1, toHop, day, currentWeekSemester.getWeekStudy(), currentWeekSemester.getId(), targetWeekNumber, practiceCaseNamesForErrorMessage);
                        long nonActiveComputers = selectedRoom.getNumberOfComputers() - (selectedRoom.getNumberOfComputerActive() != null ? selectedRoom.getNumberOfComputerActive() : 0);
                        String warningMessage = String.format("Phòng %s được chọn có tổng %d máy, trong đó %d máy hoạt động (Đủ cho %d SV). %d máy không hoạt động.",
                                selectedRoom.getNameRoom(), selectedRoom.getNumberOfComputers(),
                                selectedRoom.getNumberOfComputerActive(), studentsForThisBooking, nonActiveComputers);
                        messageIfCreateSuccessButRoomHasNonActiveComputers.put(warningKey, warningMessage);
                    }
                } // End group loop (groupIdx)

                if (iterationSuccessful) {
                    if(calendarsForThisIteration.size() < numberOfGroupsToSchedule && totalStudentsInCreditClass > 0){
                        // Trường hợp này xảy ra nếu một số nhóm không có SV (studentsForThisBooking == 0) và đã bị continue
                        // nhưng các nhóm có SV thì lại xếp được.
                        // Hoặc nếu logic phân bổ SV có vấn đề.
                        // Nếu yêu cầu là SỐ LƯỢNG LỊCH ĐƯỢC TẠO TRONG BUỔI PHẢI BẰNG SỐ NHÓM DỰ KIẾN (trừ khi nhóm đó 0 SV)
                        // thì đây có thể là lỗi.
                        // Hiện tại, nếu các nhóm có SV đều được xếp, thì coi là thành công.
                        // Nếu bạn muốn chặt chẽ hơn, cần kiểm tra calendarsForThisIteration.size() so với số nhóm thực sự cần xếp (có SV)
                        System.out.println(String.format("--- Buổi học thứ %d/%d: Xếp lịch thành công cho %d/%d nhóm dự kiến. ---",
                                iter + 1, numberOfIterations, calendarsForThisIteration.size(), numberOfGroupsToSchedule));
                    } else if (totalStudentsInCreditClass == 0 && calendarsForThisIteration.isEmpty()){
                        // Không có SV, không có lịch nào được tạo cho buổi này (đúng)
                        System.out.println(String.format("--- Buổi học thứ %d/%d: Lớp không có SV, không tạo lịch. ---", iter + 1, numberOfIterations));
                    }
                    else {
                        System.out.println(String.format("--- Buổi học thứ %d/%d: Xếp lịch thành công cho %d nhóm. ---",
                                iter + 1, numberOfIterations, calendarsForThisIteration.size()));
                    }
                    allSuccessfullyScheduledCalendars.addAll(calendarsForThisIteration);
                } else {
                    throw new CalendarException("Không thể xếp đủ lịch cho tất cả các nhóm . Vui lòng kiểm tra lại thông tin hoặc tài nguyên phòng máy. Quá trình tạo lịch tự động đã dừng." );
                }
            } // End iteration loop (iter)

            if (!allSuccessfullyScheduledCalendars.isEmpty()) {
                this.iCalendarRepository.saveAll(allSuccessfullyScheduledCalendars);
                // Cân nhắc cập nhật creditClass.setLessonHave(...) và lưu lại creditClass ở đây
            } else if (numberOfIterations > 0 && numberOfGroupsToSchedule > 0) {
                return new ResponseSuccess<>(HttpStatus.OK.value(), "Không có lịch nào được tạo thành công (ví dụ: không tìm thấy phòng phù hợp cho bất kỳ buổi nào hoặc lớp không có sinh viên).");
            }

            String successMessage = String.format("Tạo lịch tự động thành công cho %d buổi học (%d lịch chi tiết đã được tạo).", numberOfIterations, allSuccessfullyScheduledCalendars.size());
            if (!messageIfCreateSuccessButRoomHasNonActiveComputers.isEmpty()) {
                Map<String, Object> responseDataWithWarnings = new HashMap<>();
                responseDataWithWarnings.put("warnings", messageIfCreateSuccessButRoomHasNonActiveComputers);
                return new ResponseSuccess<>(HttpStatus.OK.value(), successMessage + " Có một số cảnh báo về phòng máy.", responseDataWithWarnings);
            }
            return new ResponseSuccess<>(HttpStatus.OK.value(), successMessage);

        } catch (CalendarException e) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            Throwable rootCause = e.getRootCause();
            String message = "Lỗi lưu dữ liệu: Có thể lịch đã tồn tại hoặc có xung đột dữ liệu.";
            if (rootCause != null) { message += " Chi tiết: " + rootCause.getMessage(); }
            return new ResponseFailure(HttpStatus.CONFLICT.value(), message);
        } catch (RuntimeException e) {
            System.err.println("--LỖI CHUNG handleCreateCalendarAuto: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseFailure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi hệ thống không xác định khi tạo lịch tự động. Vui lòng thử lại sau hoặc liên hệ quản trị viên.");
        }
    }



//    @Override
//    @Transactional
//    public ResponseData<?> handleCreateCalendarNoAuto(CalendarRequestNoAutoDto calendarRequestNoAutoDto) {
//        //LTC
//        CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(calendarRequestNoAutoDto.getCreditClassId());
//        //SLSV
//        Long numberOfStudent = creditClass.getNumberOfStudentsLTC();
//        // CoSo
//        FacilityEntity facility = this.iFacilityRepository.findFacilityEntityById(calendarRequestNoAutoDto.getIdFacility());
//        // Danh sach phong cua co so
//        List<RoomEntity> listRoom = this.iRoomRepository.findRoomEntitiesByFacility(facility);
//
//        // check credit class if lessonCurrent = lessonDb can't create calendar
//        List<CreditClassEligibleDto> listCredit = this.iCreditClassRepository.findAllCreditClassEligible();
//        for ( CreditClassEligibleDto credit : listCredit ){
//            if ( credit.getCreditClassId().equals(calendarRequestNoAutoDto.getCreditClassId().toString())    && credit.getLessonHave().equals(credit.getLessonSum()) ){
//                return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Credit created full , please choose another credit");
//            }
//        }
//
//        Map<String , String> messageIfCreateSuccessButRoomHaveComputerError = new HashMap<>();
//
//
//        List<CalendarRequestDetailDto> listCalendarCreate = calendarRequestNoAutoDto.getCalendarDetail();
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
//    }

    @Transactional
    public ResponseData<?> handleCreateCalendarNoAuto(CalendarRequestNoAutoDto calendarRequestNoAutoDto) {
        CreditClassEntity creditClass = this.iCreditClassRepository.findCreditClassEntityById(calendarRequestNoAutoDto.getCreditClassId());
        if (creditClass == null) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy lớp tín chỉ với ID: " + calendarRequestNoAutoDto.getCreditClassId());
        }

        FacilityEntity facilityContext = this.iFacilityRepository.findFacilityEntityById(calendarRequestNoAutoDto.getIdFacility());
        if (facilityContext == null) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Không tìm thấy cơ sở với ID: " + calendarRequestNoAutoDto.getIdFacility());
        }

        // Kiểm tra lớp tín chỉ đã đủ tiết chưa
        CreditClassEligibleDto creditInfo = this.iCreditClassRepository.findAllCreditClassEligible().stream()
                .filter(c -> c.getCreditClassId().equals(calendarRequestNoAutoDto.getCreditClassId().toString()))
                .findFirst().orElse(null);
        if (creditInfo != null && creditInfo.getLessonHave().equals(creditInfo.getLessonSum())) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Lớp tín chỉ " + creditClass.getNameCreditClass() + " đã đủ số tiết, vui lòng chọn lớp tín chỉ khác");
        }

        Map<String, String> messageIfCreateSuccessButRoomHasNonActiveComputers = new HashMap<>();
        List<CalendarEntity> newCalendarsToSave = new ArrayList<>();

        // Logic phân bổ sinh viên cho các nhóm (nếu cần cho kiểm tra sức chứa)
        Long totalStudentsInCreditClass = creditClass.getNumberOfStudentsLTC();
        Integer numberOfGroupsInRequest = calendarRequestNoAutoDto.getCalendarDetail().size();
        long baseStudentsPerGroup = 0L;
        long remainingStudentsToDistribute = 0L;

        // Giả sử mỗi CalendarRequestDetailDto tương ứng với một nhóm riêng biệt mà người dùng muốn tạo
        // và sĩ số mỗi nhóm này có thể được tính nếu ta biết tổng số nhóm của lớp tín chỉ
        // Hoặc, đơn giản hơn, coi mỗi detail là một booking và kiểm tra với tổng sĩ số LTC.
        // Để đơn giản cho ví dụ này, ta sẽ kiểm tra sức chứa phòng với tổng sĩ số LTC.
        // Nếu mỗi detail là 1 nhóm nhỏ hơn, bạn cần logic chia SV phức tạp hơn hoặc
        // yêu cầu người dùng nhập số SV cho từng detail.

        if (numberOfGroupsInRequest > 0 && totalStudentsInCreditClass > 0) {
            // Nếu mỗi CalendarRequestDetailDto là MỘT NHÓM của lớp tín chỉ,
            // và người dùng có thể tạo nhiều nhóm trong một request.
            // Bạn cần quyết định cách chia sinh viên.
            // Ví dụ đơn giản nhất: mỗi nhóm chịu trách nhiệm cho 1 phần của totalStudentsInCreditClass
            baseStudentsPerGroup = totalStudentsInCreditClass / numberOfGroupsInRequest;
            remainingStudentsToDistribute = totalStudentsInCreditClass % numberOfGroupsInRequest;
        }


        StatusEntity statusActive = this.iStatusRepository.findStatusEntityById(6L);
        if (statusActive == null) {
            return new ResponseFailure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Không tìm thấy trạng thái 'Active' trong hệ thống (ID: 6).");
        }

        try {
            int groupIndexCounter = 0; // Để lấy studentsForThisBooking nếu chia nhóm
            for (CalendarRequestDetailDto calendarCreate : calendarRequestNoAutoDto.getCalendarDetail()) {

                long studentsForThisBooking;
                if (numberOfGroupsInRequest > 0 && totalStudentsInCreditClass > 0) {
                    studentsForThisBooking = baseStudentsPerGroup + (groupIndexCounter < remainingStudentsToDistribute ? 1 : 0);
                } else {
                    studentsForThisBooking = totalStudentsInCreditClass; // Nếu không chia nhóm, hoặc lớp 0 SV
                }
                groupIndexCounter++;


                WeekSemesterEntity weekSemester = this.iWeekSemesterRepository.findWeekSemesterEntityById(calendarCreate.getWeekSemesterId());
                if (weekSemester == null) {
                    throw new CalendarException(String.format("Nhóm %d: Không tìm thấy Tuần-Học kỳ với ID: %d", calendarCreate.getGroupId(), calendarCreate.getWeekSemesterId()));
                }

                Long day = calendarCreate.getDayId();
                PracticeCaseEntity practiceCaseBegin = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarCreate.getPracticeCaseBeginId());
                if (practiceCaseBegin == null) {
                    throw new CalendarException(String.format("Nhóm %d: Không tìm thấy Tiết thực hành bắt đầu với ID: %d", calendarCreate.getGroupId(), calendarCreate.getPracticeCaseBeginId()));
                }

                Long allCase = calendarCreate.getAllCase();
                if (allCase == null || allCase <= 0) {
                    throw new CalendarException(String.format("Nhóm %d: Số tiết (allCase) phải lớn hơn 0.", calendarCreate.getGroupId()));
                }

                // Lấy phòng do người dùng chỉ định
                RoomEntity selectedRoomByUser = this.iRoomRepository.findById(calendarCreate.getRoomId())
                        .orElseThrow(() -> new CalendarException(String.format("Nhóm %d: Không tìm thấy phòng với ID: %d", calendarCreate.getGroupId(), calendarCreate.getRoomId())));

                // Kiểm tra phòng có thuộc cơ sở đã chọn không
                if (!selectedRoomByUser.getFacility().getId().equals(facilityContext.getId())) {
                    throw new CalendarException(String.format("Nhóm %d: Phòng %s (ID: %d) không thuộc cơ sở %s đã chọn.",
                            calendarCreate.getGroupId(), selectedRoomByUser.getNameRoom(), selectedRoomByUser.getId(), facilityContext.getNameFacility()));
                }

                // Kiểm tra số tiết liên tục có hợp lệ không
                List<PracticeCaseEntity> practiceCasesForBooking;
                List<PracticeCaseEntity> potentialCases = iPracticeCaseRepository.findPracticeCasesStartingFromId(
                        practiceCaseBegin.getId(), PageRequest.of(0, allCase.intValue()));
                if (potentialCases.size() < allCase) {
                    throw new CalendarException(String.format("Nhóm %d: Không tìm thấy đủ %d tiết TH liên tục trên hệ thống bắt đầu từ tiết '%s' (ID: %d).",
                            calendarCreate.getGroupId(), allCase, practiceCaseBegin.getNamePracticeCase(), practiceCaseBegin.getId()));
                }
                for (int k = 0; k < potentialCases.size(); k++) { /* Kiểm tra tính liên tục ID */
                    PracticeCaseEntity currentFetchedCase = potentialCases.get(k);
                    Long expectedId = practiceCaseBegin.getId() + k;
                    if (!currentFetchedCase.getId().equals(expectedId)) {
                        throw new CalendarException(String.format("Nhóm %d: Tính liên tục ID tiết TH bị gián đoạn ở tiết thứ %d.", calendarCreate.getGroupId(), k + 1));
                    }
                }
                practiceCasesForBooking = potentialCases;
                long currentBookingStartPcId = practiceCasesForBooking.get(0).getId();
                long currentBookingEndPcId = practiceCasesForBooking.get(practiceCasesForBooking.size() - 1).getId();


                // KIỂM TRA PHÒNG TRỐNG CHO TOÀN BỘ allCase (bao gồm cả DB và temp slots)
                // (Sử dụng lại logic từ handleCreateCalendarAuto nếu có tempOccupiedSlotsByRoom,
                //  hoặc chỉ kiểm tra DB nếu mỗi request này là độc lập hoàn toàn)
                // Giả sử không có tempOccupiedSlotsByRoom cho phiên bản "NoAuto" này để đơn giản hóa,
                // người dùng chịu trách nhiệm không tự tạo trùng trong cùng 1 request.
                // Nếu muốn an toàn hơn, bạn NÊN thêm logic tempOccupiedSlotsByRoom.

                List<CalendarEntity> existingBookingsInRoom = iCalendarRepository.findAllByWeekSemesterAndDayAndRoomAndStatus(
                        weekSemester, day, selectedRoomByUser, statusActive);
                if (!existingBookingsInRoom.isEmpty()) {
                    for (CalendarEntity existingBooking : existingBookingsInRoom) {
                        PracticeCaseEntity existingBookingStartPcEntity = existingBooking.getPracticeCase();
                        Long existingBookingAllCase = existingBooking.getAllCase();
                        if (existingBookingStartPcEntity == null || existingBookingAllCase == null || existingBookingAllCase <= 0) continue;
                        long existingDbBookingStartPcId = existingBookingStartPcEntity.getId();
                        long existingDbBookingEndPcId = existingDbBookingStartPcId + existingBookingAllCase - 1;
                        if (doIntervalsOverlap(currentBookingStartPcId, currentBookingEndPcId, existingDbBookingStartPcId, existingDbBookingEndPcId)) {
                            String practiceCaseNamesForErrorMessage = practiceCasesForBooking.stream().map(PracticeCaseEntity::getNamePracticeCase).collect(Collectors.joining(", "));
                            throw new CalendarException(String.format("Nhóm %d: Phòng %s đã bị đặt trong khoảng thời gian Thứ %d, Tuần %s, Tiết %s.",
                                    calendarCreate.getGroupId(), selectedRoomByUser.getNameRoom(), day, weekSemester.getWeekStudy(), practiceCaseNamesForErrorMessage));
                        }
                    }
                }

                // Kiểm tra sức chứa phòng (TÙY CHỌN)
                if (selectedRoomByUser.getNumberOfComputerActive() < studentsForThisBooking) {
                    String warningKey = String.format("Nhóm %d (Phòng %s)", calendarCreate.getGroupId(), selectedRoomByUser.getNameRoom());
                    String warningMessage = String.format("Phòng %s chỉ có %d máy hoạt động, nhưng nhóm cần %d máy cho %d SV.",
                            selectedRoomByUser.getNameRoom(), selectedRoomByUser.getNumberOfComputerActive(), studentsForThisBooking, studentsForThisBooking);
                    // Quyết định: Ném lỗi hay chỉ cảnh báo?
                    // throw new CalendarException(warningMessage); // Nếu muốn báo lỗi
                    messageIfCreateSuccessButRoomHasNonActiveComputers.put(warningKey, warningMessage); // Nếu chỉ cảnh báo
                    System.out.println("CẢNH BÁO: " + warningMessage);
                }


                String note = calendarCreate.getPurposeUse();
                String toHop = "0" + calendarCreate.getGroupId(); // Hoặc một logic tạo mã nhóm/tổ hợp khác

                CalendarEntity newCalendar = new CalendarEntity();
                newCalendar.setCreditClass(creditClass);
                newCalendar.setWeekSemester(weekSemester);
                newCalendar.setDay(day);
                newCalendar.setPracticeCase(practiceCaseBegin);
                newCalendar.setAllCase(allCase);
                newCalendar.setNoteCalendar(note);
                newCalendar.setGroup(toHop);
                newCalendar.setStatus(statusActive);
                newCalendar.setRoom(selectedRoomByUser); // Gán phòng người dùng đã chọn
                newCalendarsToSave.add(newCalendar);

            } // Kết thúc vòng lặp qua calendarDetail

            if (!newCalendarsToSave.isEmpty()) {
                this.iCalendarRepository.saveAll(newCalendarsToSave);
                // this.iCalendarRepository.flush(); // Không cần thiết nếu @Transactional hoạt động đúng
            } else {
                // Trường hợp này không nên xảy ra nếu DTO có @NotEmpty cho calendarDetail
                return new ResponseSuccess<>(HttpStatus.OK.value(), "Không có chi tiết lịch nào để tạo.");
            }

            String successMsg = "Tạo lịch thủ công thành công cho " + newCalendarsToSave.size() + " mục.";
            if (!messageIfCreateSuccessButRoomHasNonActiveComputers.isEmpty()) {
                Map<String, Object> responseDataWithWarnings = new HashMap<>();
                responseDataWithWarnings.put("warnings", messageIfCreateSuccessButRoomHasNonActiveComputers);
                return new ResponseSuccess<>(HttpStatus.OK.value(), successMsg + " Có một số cảnh báo.", responseDataWithWarnings);
            }
            return new ResponseSuccess<>(HttpStatus.OK.value(), successMsg);

        } catch (CalendarException e) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            Throwable rootCause = e.getRootCause();
            String message = "Lỗi lưu dữ liệu khi tạo lịch thủ công: Có thể lịch đã tồn tại hoặc có xung đột dữ liệu.";
            if (rootCause != null) { message += " Chi tiết: " + rootCause.getMessage(); }
            return new ResponseFailure(HttpStatus.CONFLICT.value(), message);
        }
        catch (RuntimeException e) {
            System.err.println("--LỖI CHUNG handleCreateCalendarNoAuto: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseFailure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi hệ thống khi tạo lịch thủ công.");
        }
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
//                return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Lớp tín chỉ đã đủ tín , vui lòng chọn lớp tín chỉ khác");
//            }
//        }
//
//        Map<String , String> messageIfCreateSuccessButRoomHaveComputerError = new HashMap<>();
//
//        //begin check test
//
//        Long totalStudentsInCreditClass = creditClass.getNumberOfStudentsLTC();
//        Integer numberOfGroupsToSchedule = calendarRequestDto.getCalendarDetail().size();
//
//        long baseStudentsPerGroup = 0L;
//        long remainingStudentsToDistribute = 0L;
//
//        if (numberOfGroupsToSchedule != 0) {
//            baseStudentsPerGroup = totalStudentsInCreditClass / numberOfGroupsToSchedule;
//            remainingStudentsToDistribute = totalStudentsInCreditClass % numberOfGroupsToSchedule;
//        } else {
//            // Xử lý fallback, có thể throw exception hoặc gán giá trị mặc định
//
//            throw new CalendarException("Số lượng nhóm phải lớn hơn 0 để thực hiện lịch.");
//        }
//
//        // end check test
//
//
//        List<CalendarRequestDetailDto> listCalendarCreate = calendarRequestDto.getCalendarDetail();
//        try {
//            for (int i = 0; i < numberOfGroupsToSchedule; i++) {
//                CalendarRequestDetailDto calendarCreate = listCalendarCreate.get(i);
//
//                long studentsForThisBooking = baseStudentsPerGroup + (i < remainingStudentsToDistribute ? 1 : 0);
//
//                if (studentsForThisBooking == 0) { // Nếu lớp tín chỉ không có SV, hoặc đã phân bổ hết
//                    continue;
//                }
//
//                // Lấy thông tin cần thiết từ calendarCreate
//                WeekSemesterEntity weekSemester = this.iWeekSemesterRepository.findWeekSemesterEntityById(calendarCreate.getWeekSemesterId());
//                Long day = calendarCreate.getDayId();
//                PracticeCaseEntity practiceCase = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarCreate.getPracticeCaseBeginId());
//                Long allCase = calendarCreate.getAllCase(); // Cần xem xét logic này nếu allCase > 1, phòng phải trống cho tất cả các ca đó
//                String note = calendarCreate.getPurposeUse();
//                String toHop = "0" + calendarCreate.getGroupId();
//                StatusEntity status = this.iStatusRepository.findStatusEntityById(6L); // Active
//                // check Number of case must greater than allCase
//                Long countPracticeCase = this.iPracticeCaseRepository.count();
//                if ( countPracticeCase - practiceCase.getId() + 1 < allCase){
//                    return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Number of case must greater than allCase");
//                }
//                // --- LOGIC CHỌN PHÒNG BẮT ĐẦU TỪ ĐÂY ---
//                RoomEntity selectedRoom = null;
//                boolean usedRoomWithBrokenComputers = false;
//
//                // Lấy danh sách phòng có sẵn tại thời điểm đó (chưa bị đặt)
//                List<RoomEntity> availableRoomsForSlot = new ArrayList<>();
//                for (RoomEntity roomCandidate : listRoom) { // listRoom là phòng của cơ sở đã fetch ở đầu
//                    // TODO: Nếu allCase > 1, bạn cần kiểm tra phòng trống cho TẤT CẢ các ca liên tiếp.
//                    // Ví dụ: nếu practiceCase là Ca1, allCase là 2, thì phải check Ca1 và Ca2.
//                    // Đoạn code dưới đây giả sử chỉ check cho practiceCaseBeginId.
//                    boolean checkCalendarExistRoom = this.iCalendarRepository.existsByWeekSemesterAndDayAndPracticeCaseAndRoomAndStatus(
//                            weekSemester, day, practiceCase, roomCandidate, status
//                    );
//                    if (!checkCalendarExistRoom) {
//                        availableRoomsForSlot.add(roomCandidate);
//                    }
//                }
//
//                if (availableRoomsForSlot.isEmpty()) {
//                    throw new CalendarException("Không còn phòng trống tại cơ sở " + facility.getNameFacility() +
//                            " cho lịch: Thứ " + day + ", Tuần " + weekSemester.getWeekStudy() +
//                            ", Ca " + practiceCase.getNamePracticeCase());
//                }
//
//                // Ưu tiên phòng:
//                // 1. Đủ máy hoạt động, không có máy hỏng
//                // 2. Đủ máy hoạt động, có máy hỏng
//                // Sắp xếp danh sách phòng trống để dễ chọn
//                availableRoomsForSlot.sort((r1, r2) -> {
//                    long workingMachinesR1 = r1.getNumberOfComputers() - r1.getNumberOfComputerError();
//                    long workingMachinesR2 = r2.getNumberOfComputers() - r2.getNumberOfComputerError();
//
//                    // Ưu tiên phòng đủ máy cho số sinh viên hiện tại
//                    boolean r1CanFit = workingMachinesR1 >= studentsForThisBooking;
//                    boolean r2CanFit = workingMachinesR2 >= studentsForThisBooking;
//
//                    if (r1CanFit && !r2CanFit) return -1;
//                    if (!r1CanFit && r2CanFit) return 1;
//                    if (!r1CanFit && !r2CanFit) { // Cả 2 đều không đủ máy hoạt động, ưu tiên phòng có nhiều máy hoạt động hơn
//                        return Long.compare(workingMachinesR2, workingMachinesR1); // Nhiều hơn thì tốt hơn (ưu tiên)
//                    }
//
//                    // Cả 2 đều đủ máy hoạt động
//                    // Ưu tiên phòng không có máy hỏng
//                    if (r1.getNumberOfComputerError() == 0 && r2.getNumberOfComputerError() != 0) return -1;
//                    if (r1.getNumberOfComputerError() != 0 && r2.getNumberOfComputerError() == 0) return 1;
//
//                    // Cả 2 đều có máy hỏng (hoặc đều không có), ưu tiên phòng có nhiều máy hoạt động hơn (chỗ ngồi thoải mái hơn)
//                    // hoặc ít máy hỏng hơn nếu số máy hoạt động bằng nhau
//                    if (workingMachinesR1 != workingMachinesR2) {
//                        return Long.compare(workingMachinesR2, workingMachinesR1); // Nhiều máy hoạt động hơn thì tốt hơn
//                    }
//                    return Long.compare(r1.getNumberOfComputerError(), r2.getNumberOfComputerError()); // Ít máy hỏng hơn thì tốt hơn
//                });
//
//                // Chọn phòng đầu tiên trong danh sách đã sắp xếp mà đáp ứng được
//                for (RoomEntity room : availableRoomsForSlot) {
//                    long workingComputers = room.getNumberOfComputers() - room.getNumberOfComputerError();
//                    if (workingComputers >= studentsForThisBooking) {
//                        selectedRoom = room;
//                        if (room.getNumberOfComputerError() > 0) {
//                            usedRoomWithBrokenComputers = true;
//                        }
//                        break; // Đã tìm được phòng phù hợp
//                    }
//                }
//
//                // --- KẾT THÚC LOGIC CHỌN PHÒNG ---
//
//                if (selectedRoom == null) {
//                    // Không có phòng nào đủ SỐ MÁY HOẠT ĐỘNG cho studentsForThisBooking
//                    // Đây là trường hợp bạn cần quyết định:
//                    // 1. Báo lỗi luôn (khuyến nghị)
//                    // 2. (Không khuyến nghị) Nếu user yêu cầu "buộc phải xét": tìm phòng có TỔNG SỐ MÁY >= studentsForThisBooking
//                    //    nhưng SỐ MÁY HOẠT ĐỘNG < studentsForThisBooking. Điều này có nghĩa là có SV không có máy.
//                    //    Nếu theo hướng này, bạn cần thêm 1 vòng lặp nữa qua availableRoomsForSlot
//                    //    để tìm phòng có room.getNumberOfComputers() >= studentsForThisBooking và cảnh báo cực mạnh.
//                    //    Hiện tại, logic đang theo hướng báo lỗi nếu không đủ MÁY HOẠT ĐỘNG.
//                    throw new CalendarException("Không tìm thấy phòng nào có đủ " + studentsForThisBooking +
//                            " máy hoạt động cho lịch: Thứ " + day + ", Tuần " + weekSemester.getWeekStudy() +
//                            ", Ca " + practiceCase.getNamePracticeCase() + " (Nhóm " + toHop + ").");
//                }
//
//                // Tạo CalendarEntity
//                CalendarEntity newCalendar = new CalendarEntity();
//                newCalendar.setCreditClass(creditClass);
//                newCalendar.setWeekSemester(weekSemester);
//                newCalendar.setDay(day);
//                newCalendar.setPracticeCase(practiceCase);
//                newCalendar.setAllCase(allCase);
//                newCalendar.setNoteCalendar(note);
//                newCalendar.setGroup(toHop);
//                newCalendar.setStatus(status);
//                newCalendar.setRoom(selectedRoom); // Gán phòng đã chọn
//
//                // Ghi nhận cảnh báo nếu cần
//                if (usedRoomWithBrokenComputers) {
//                    String warningKey = String.format("Nhóm %s (Lịch: Thứ %d, Tuần %s, Ca %s)",
//                            toHop, day, weekSemester.getWeekStudy(), practiceCase.getNamePracticeCase());
//                    String warningMessage = String.format("Phòng %s được chọn có %d máy, trong đó %d máy hỏng. Số máy hoạt động: %d (Đủ cho %d SV).",
//                            selectedRoom.getNameRoom(), selectedRoom.getNumberOfComputers(),
//                            selectedRoom.getNumberOfComputerError(),
//                            (selectedRoom.getNumberOfComputers() - selectedRoom.getNumberOfComputerError()),
//                            studentsForThisBooking);
//                    messageIfCreateSuccessButRoomHaveComputerError.put(warningKey, warningMessage);
//                    System.out.println("warningKey : " + warningKey );
//                    System.out.println("warningMessage : " + warningMessage );
//                }
//
//                try {
//                    this.iCalendarRepository.save(newCalendar);
//                    // Hoặc add vào newCalendarsToSave rồi saveAll ở cuối
//                    this.iCalendarRepository.flush(); // Ép flush để xem lỗi ngay
//                } catch (RuntimeException e) { // Bắt DataIntegrityViolationException nếu có unique constraint
//                    // Xử lý lỗi nếu có unique constraint bị vi phạm (ví dụ: đã có lịch khác được tạo đồng thời)
//                    // Hoặc bạn có thể đã check existsBy... ở trên, nhưng đây là một safeguard.
//                    throw new CalendarException("Lỗi khi lưu lịch cho Nhóm " + toHop +
//                            ": Thứ " + day + ", Tuần " + weekSemester.getWeekStudy() +
//                            ", Ca " + practiceCase.getNamePracticeCase() + ", Phòng " + selectedRoom.getNameRoom() +
//                            ". Lịch có thể đã tồn tại hoặc có lỗi cơ sở dữ liệu. Chi tiết: " + e.getMessage());
//                }
//            } // Kết thúc vòng lặp for qua listCalendarCreate
//
//            return  new ResponseSuccess<>(HttpStatus.OK.value(), "Tạo lịch thành công"  );
//        }catch (CalendarException e){
//
//            e.printStackTrace();
//            throw  new CalendarException(e.getMessage());
//            //return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), e.getMessage());
//        }
//        catch (RuntimeException e) {
//            System.out.println("--ER handleCreateCalendar " + e.getMessage());
//            e.printStackTrace();
//            throw  new RuntimeException(e.getMessage());
//           // return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "Create calendar fail");
//        }
//
//
//
//
//    }

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
        if (calendarRequestOneDto.getCalendarId() == null) {
            return new ResponseFailure(HttpStatus.BAD_REQUEST.value(), "ID của lịch cần cập nhật không được để trống.");
        }

        CalendarEntity calendarToUpdate = this.iCalendarRepository.findById(calendarRequestOneDto.getCalendarId())
                .orElseThrow(() -> new CalendarException("Không tìm thấy lịch với ID: " + calendarRequestOneDto.getCalendarId()));

        WeekSemesterEntity weekSemesterNew = this.iWeekSemesterRepository.findWeekSemesterEntityById(calendarRequestOneDto.getWeekSemesterId());
        if (weekSemesterNew == null) {
            throw new CalendarException("Không tìm thấy Tuần-Học kỳ mới với ID: " + calendarRequestOneDto.getWeekSemesterId());
        }

        Long dayNew = calendarRequestOneDto.getDayId();
        PracticeCaseEntity practiceCaseNew = this.iPracticeCaseRepository.findPracticeCaseEntityById(calendarRequestOneDto.getPracticeCaseBeginId());
        if (practiceCaseNew == null) {
            throw new CalendarException("Không tìm thấy Tiết thực hành mới với ID: " + calendarRequestOneDto.getPracticeCaseBeginId());
        }

        RoomEntity roomOfCalendar = calendarToUpdate.getRoom(); // Phòng không thay đổi
        String noteNew = calendarRequestOneDto.getPurposeUse() != null ? calendarRequestOneDto.getPurposeUse().trim() : calendarToUpdate.getNoteCalendar(); // Giữ note cũ nếu note mới là null
        StatusEntity statusActive = this.iStatusRepository.findStatusEntityByNameStatus("ACTIVE"); // Hoặc lấy từ calendarToUpdate.getStatus() nếu trạng thái có thể khác
        if (statusActive == null) {
            return new ResponseFailure(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Không tìm thấy trạng thái 'ACTIVE'.");
        }


        Long allCase = calendarToUpdate.getAllCase(); // allCase không thay đổi

        // Kiểm tra xem khoảng tiết mới có hợp lệ với allCase không
        // (Giả định ID của PracticeCaseEntity là liên tục)
        // Lấy danh sách các tiết thực hành liên tiếp cho khoảng thời gian mới
        List<PracticeCaseEntity> practiceCasesForNewSlot;
        List<PracticeCaseEntity> potentialNewCases = iPracticeCaseRepository.findPracticeCasesStartingFromId(
                practiceCaseNew.getId(), PageRequest.of(0, allCase.intValue()));

        if (potentialNewCases.size() < allCase) {
            throw new CalendarException(String.format("Không tìm thấy đủ %d tiết TH liên tục trên hệ thống bắt đầu từ tiết '%s' (ID: %d) cho lịch ID %d.",
                    allCase, practiceCaseNew.getNamePracticeCase(), practiceCaseNew.getId(), calendarToUpdate.getId()));
        }
        for (int k = 0; k < potentialNewCases.size(); k++) {
            PracticeCaseEntity currentFetchedCase = potentialNewCases.get(k);
            Long expectedId = practiceCaseNew.getId() + k;
            if (!currentFetchedCase.getId().equals(expectedId)) {
                throw new CalendarException(String.format("Tính liên tục ID tiết TH bị gián đoạn cho vị trí mới của lịch ID %d, ở tiết thứ %d (mong đợi ID %d, thực tế ID %d).",
                        calendarToUpdate.getId(), k + 1, expectedId, currentFetchedCase.getId()));
            }
        }
        practiceCasesForNewSlot = potentialNewCases;
        long newPracticeCaseBeginIdActual = practiceCasesForNewSlot.get(0).getId(); // = practiceCaseNew.getId()
        long newPracticeCaseEndIdActual = practiceCasesForNewSlot.get(practiceCasesForNewSlot.size() - 1).getId();


        // Kiểm tra xem thời gian mới có trùng với lịch nào khác không (TRỪ CHÍNH NÓ)
        boolean isNewSlotOccupiedByOther = this.iCalendarRepository.existsOverlappingCalendar(
                calendarToUpdate.getId(),
                roomOfCalendar,
                weekSemesterNew,
                dayNew,
                statusActive, // Chỉ kiểm tra với các lịch active khác
                newPracticeCaseBeginIdActual,
                newPracticeCaseEndIdActual
        );

        try {
            if (!isNewSlotOccupiedByOther) {
                calendarToUpdate.setDay(dayNew);
                calendarToUpdate.setPracticeCase(practiceCaseNew); // Tiết bắt đầu mới
                calendarToUpdate.setWeekSemester(weekSemesterNew);
                calendarToUpdate.setNoteCalendar(noteNew);
                // Không thay đổi: creditClass, allCase, room, status (trừ khi có logic khác)

                this.iCalendarRepository.save(calendarToUpdate);
                return new ResponseSuccess<>(HttpStatus.OK.value(), "Cập nhật lịch (ID: " + calendarToUpdate.getId() + ") thành công");
            } else {
                String practiceCaseNamesForErrorMessage = practiceCasesForNewSlot.stream().map(PracticeCaseEntity::getNamePracticeCase).collect(Collectors.joining(", "));
                throw new CalendarException(String.format("Không thể cập nhật lịch (ID: %d). Phòng %s đã có lịch khác chiếm giữ vào Thứ: %d, Tuần: %s (ID: %d), Tiết %s.",
                        calendarToUpdate.getId(), roomOfCalendar.getNameRoom(),
                        dayNew, weekSemesterNew.getWeekStudy(), weekSemesterNew.getId(), practiceCaseNamesForErrorMessage
                ));
            }
        } catch (CalendarException e) {
            // Ghi log chi tiết hơn nếu cần thiết
            System.err.println("Lỗi khi cập nhật lịch ID " + calendarToUpdate.getId() + ": " + e.getMessage());
            throw e; // Ném lại để @Transactional rollback và controller advice xử lý
        } catch (DataIntegrityViolationException dive) {
            // Mặc dù đã kiểm tra, vẫn có thể có race condition nếu có request đồng thời
            System.err.println("Lỗi DataIntegrityViolationException khi cập nhật lịch ID " + calendarToUpdate.getId() + ": " + dive.getMessage());
            throw new CalendarException("Lỗi dữ liệu: Không thể cập nhật lịch do xung đột. Vui lòng thử lại. " + dive  );
        }
        // Không nên có catch (RuntimeException e) chung chung ở đây nếu bạn muốn @Transactional hoạt động đúng
        // và ControllerAdvice xử lý Exception.
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


    private boolean doIntervalsOverlap(long startIdA, long endIdA, long startIdB, long endIdB) {
        // Đảm bảo các khoảng hợp lệ (start <= end)
        // Nếu không, chúng không thể chồng chéo một cách có ý nghĩa
        if (startIdA > endIdA || startIdB > endIdB) {
            return false;
        }
        // Chồng chéo xảy ra nếu điểm bắt đầu của khoảng này nằm trước hoặc bằng điểm kết thúc của khoảng kia,
        // VÀ điểm kết thúc của khoảng này nằm sau hoặc bằng điểm bắt đầu của khoảng kia.
        // Nói cách khác: max(startA, startB) <= min(endA, endB)
        return Math.max(startIdA, startIdB) <= Math.min(endIdA, endIdB);
    }
}
