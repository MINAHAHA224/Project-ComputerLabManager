package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.userResponse.ProfileResponseDto;
import com.example.computerweb.DTO.dto.userResponse.UserResponseDto;
import com.example.computerweb.DTO.dto.userResponse.UserCreateMgnDto;
import com.example.computerweb.DTO.dto.userResponse.UserManagementDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserLoginDto;
import com.example.computerweb.DTO.requestBody.userRequest.UserMngProfileRequestDto;
import com.example.computerweb.DTO.requestBody.userRequest.UserProfileRequestDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserRegisterDto;
import com.example.computerweb.components.JwtTokenUtil;
import com.example.computerweb.models.entity.AccountEntity;
import com.example.computerweb.models.entity.MajorEntity;
import com.example.computerweb.models.entity.RoleEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.IAccountRepository;
import com.example.computerweb.repositories.IMajorRepository;
import com.example.computerweb.repositories.IRoleRepository;
import com.example.computerweb.repositories.IUserRepository;
import com.example.computerweb.services.IUserService;
import com.example.computerweb.services.MailService;
import com.example.computerweb.utils.DateUtils;
import com.example.computerweb.utils.SecurityUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {
    private final MailService mailService;
    private final IMajorRepository iMajorRepository;
    private final IUserRepository iuserRepository;
    private final IRoleRepository iRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final IAccountRepository iAccountRepository;


    @Override
    public boolean checkPhoneExist(String phone) {
        return this.iuserRepository.existsByPhone(phone);
    }


    @Transactional
    @Override
    public ResponseEntity<String> handleRegister(UserRegisterDto userRegisterDTO) {
        try {

            UserEntity user = new UserEntity();
            user.setFirstName(userRegisterDTO.getFirstName());
            user.setLastName(userRegisterDTO.getLastName());
            user.setPhone(userRegisterDTO.getPhone());
            user.setInfomationCode(userRegisterDTO.getInformationCode());
            user.setGender(userRegisterDTO.getGender().toString());
            // set dateOfBirth
            user.setDateOfBirth(DateUtils.convertToDate(userRegisterDTO.getDateOfBirth()));
            // set role
            RoleEntity roles = this.iRoleRepository.findRoleEntityById(userRegisterDTO.getRoleId()).get();
            user.setRole(roles);
            //set major
            MajorEntity major = this.iMajorRepository.findMajorEntityById(userRegisterDTO.getMajorId());
            user.setMajor(major);


            // handle create email and password for user
            Optional<List<UserEntity>> userEntities = this.iuserRepository.findAllByRoleAndMajor(roles, major);
            int amountTeacherWithRole = 0;
            if (userEntities.isPresent()) {
                amountTeacherWithRole = userEntities.get().size() + 1;
            } else {
                amountTeacherWithRole = amountTeacherWithRole + 1;
            }
            String amountCode = String.format("%03d", amountTeacherWithRole);
            // set codeUser
            String codeUser = roles.getNameRole() + major.getCodeMajor() + amountCode;
            user.setCodeUser(codeUser);
            // save user
            UserEntity userEntity = this.iuserRepository.save(user);

            AccountEntity account = new AccountEntity();
            // set email
            String domain = "@ptithcm.edu.vn";
            String email = codeUser + domain;
            account.setEmail(email);

            //set password
            String passWordDefault = userRegisterDTO.getDateOfBirth().toString();
            String passWord = passwordEncoder.encode(passWordDefault);
            account.setPassWord(passWord);
            account.setUser(userEntity);

            // save account
            this.iAccountRepository.save(account);
            log.info("Entity User : {}", user);
            log.info("Account : {}", account);
            // handle save
            // Error of SQL VD : UNIQUE KEY ==> 403 Forbiden


            return ResponseEntity.ok().body("Đăng ký thành công");
        } catch (RuntimeException e) {
            System.out.println("--ER : Can not save Register");
            return ResponseEntity.badRequest().body("Đăng ký không thành công");
        }

    }


    @Override
    @Transactional
    public ResponseEntity<String> handleLogin(@Valid UserLoginDto userLoginDTO) {
        boolean existsEmail = this.iAccountRepository.existsByEmail(userLoginDTO.getEmail());
        if (existsEmail) {
            AccountEntity accountEntity = this.iAccountRepository.findAccountEntityByEmail(userLoginDTO.getEmail()).get();
            UserEntity userCurrent = accountEntity.getUser();
            if (passwordEncoder.matches(userLoginDTO.getPassWord(), accountEntity.getPassword())) {
                try {
                    // day la buoc dien thong tin vo de thang securityFilterChain no kiem tra quyen , nhung cai API nao can kiem tra quyen ak
                    // thì no se lay thong tin thang nay ra no kiem tra o ben cai requestMatches a
                    // va va chi can 1 cai thong tin accountEntity.getAuthorities() la du roi , boi vi thang securityFilterChain no chi can check moi
                    // cai quyen chu maays , vd .hasRole() , chu no co check tk , mk nua dau
                    // ==> userLoginDTO.getEmail(), userLoginDTO.getPassWord()la khong can thiet thi do thang securityFilterCHain no check moi cai quyen nua chu may
                    // boi vi 2 cai field nay minh check o ben tren roi con gì nua , email thi check dau , xong toi dung passEncode của springSecurity de check pass vay thi gio chi con check quyen => de securityFilterChain check
                    // khong can thiet nhung ma no bat buoc dien
                    // ===> UsernamePasswordAuthenticationToken dong vai tro la de cho securityFilterChain no check quyen
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(

                            userLoginDTO.getEmail(),
                            userLoginDTO.getPassWord(),
                            accountEntity.getAuthorities()
                    );
                    authenticationManager.authenticate(authenticationToken);

                    // va thang token hien tai cua JWT a minh chi co email, thoi gian het han chu khong co role boi vi role la minh dùng UsernamePasswordAuthenticationToken ket hop securityFilterChain check roi
                    // ==> trong jwt cua minh ko co quyen chi co moi email va thoi gian het han token jwt thoi
                    String token = this.jwtTokenUtil.generateToken(userCurrent);

//                    HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//                    HttpSession session = currentRequest.getSession(true); // Lấy session hiện tại hoặc tạo mới
//
//                    // ĐẶT TIMEOUT CHO SESSION
//                    session.setMaxInactiveInterval(5 * 60); // 5 phút * 60 giây/phút = 300 giây
//                    session.setAttribute("USER_ACTIVE_JWT", token);

                    return ResponseEntity.ok().body(token);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("--ER : Lỗi không tạo được token " + e.getMessage());
                }

            } else {
                return ResponseEntity.badRequest().body("Mật khẩu không đúng");
            }
        }

        return ResponseEntity.badRequest().body("Email không đúng ");

    }

    @Override
    @Transactional
    public ResponseEntity<String> handleLogout() {
        String email = SecurityUtils.getPrincipal();
        // handle set token account = null when logout
        try {
            HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            HttpSession session = currentRequest.getSession(false); // Lấy session hiện tại và không tạo mới
            session.setAttribute("USER_ACTIVE_JWT", null);
            return ResponseEntity.ok().body("Đăng xuất thành công");
        } catch (RuntimeException e) {
            System.out.println("--ER logout set account token = null fail");
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Đăng xuất không thành công");
    }

    @Override
    @Transactional
    public ResponseEntity<String> handleUpdateFieldProfile(UserProfileRequestDto userProfileDto) {
        try {

            UserEntity userEntity = this.iuserRepository.findUserEntityById(userProfileDto.getId());

            userEntity.setPhone(userProfileDto.getPhone());
            userEntity.setProvince(userProfileDto.getProvince());
            userEntity.setDistrict(userProfileDto.getDistrict());
            userEntity.setWard(userProfileDto.getWard());
            userEntity.setAddress(userProfileDto.getAddress());
            userEntity.setInfomationCode(userProfileDto.getInformationCode());
            // save account password
            userEntity.getAccountEntity().setEmailOfPersonal(userProfileDto.getEmailPersonal());
            userEntity.getAccountEntity().setPassWord(passwordEncoder.encode(userProfileDto.getResetPassword()));
            // save
            this.iuserRepository.save(userEntity);

            return ResponseEntity.ok().body("Cập nhật hồ sơ thành công");
        } catch (Exception e) {
            System.out.println("--ER error save field profile :" + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Cập nhật hồ sơ không thành công");
    }

    @Override
    public Map<String, String> handleGetDataUserCurrent() {

        Map<String, String> userCurrent = new TreeMap<>();

        String emailUser = SecurityUtils.getPrincipal();
        AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(emailUser).get();
        UserEntity userEntity = account.getUser();
        userCurrent.put("userName", userEntity.getFirstName() + " " + userEntity.getLastName());
        userCurrent.put("role", userEntity.getRole().getContentRole());
        userCurrent.put("userId", userEntity.getId().toString());
        userCurrent.put("userCode", userEntity.getCodeUser());
        return userCurrent;
    }

    @Override
    public UserResponseDto handleGetDataProfile() {
        ProfileResponseDto profileResponseDto = new ProfileResponseDto();

        String emailUser = SecurityUtils.getPrincipal();
        AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(emailUser).get();
        UserEntity userCurrent = account.getUser();
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(userCurrent.getId().toString());
        userResponseDto.setUserCode(userCurrent.getCodeUser());
        userResponseDto.setEmail(account.getEmail());
        userResponseDto.setFirstName(userCurrent.getFirstName());
        userResponseDto.setLastName(userCurrent.getLastName());
        userResponseDto.setMajor(userCurrent.getMajor() == null ? "" : userCurrent.getMajor().getCodeMajor());

        userResponseDto.setGender(userCurrent.getGender());
        userResponseDto.setDateOfBirth(DateUtils.convertToString(userCurrent.getDateOfBirth()));
        userResponseDto.setPhone(userCurrent.getPhone());
        userResponseDto.setInformationCode(userCurrent.getInfomationCode());
        userResponseDto.setAddress(userCurrent.getAddress());
        userResponseDto.setEmailPersonal(account.getEmailOfPersonal());
        userResponseDto.setProvince(userCurrent.getProvince());
        userResponseDto.setDistrict(userCurrent.getDistrict());
        userResponseDto.setWard(userCurrent.getWard());


        return userResponseDto;
    }

    @Override
    public List<UserManagementDto> handleGetAllDataUser() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        List<UserManagementDto> userManagementDtos = new ArrayList<>();
        List<UserEntity> userEntities = this.iuserRepository.findAll();
        for (UserEntity userEntity : userEntities) {
            UserManagementDto userManagementDto = new UserManagementDto();
            userManagementDto.setId(userEntity.getId().toString());
            userManagementDto.setCodeUser(userEntity.getCodeUser());
            userManagementDto.setFirstName(userEntity.getFirstName());
            userManagementDto.setLastName(userEntity.getLastName());
            userManagementDto.setGender(userEntity.getGender().toString());
            userManagementDto.setDateOfBirth(dateFormat.format(userEntity.getDateOfBirth()));
            userManagementDto.setPhone(userEntity.getPhone());
            userManagementDto.setEmail(userEntity.getAccountEntity().getEmail());
            userManagementDto.setInformationCode(userEntity.getInfomationCode());
            userManagementDto.setMajor(userEntity.getMajor() == null ? "" : userEntity.getMajor().getCodeMajor());
            userManagementDto.setAddress(userEntity.getAddress());
            userManagementDto.setEmailPersonal(userEntity.getAccountEntity().getEmailOfPersonal());
            userManagementDto.setProvince(userEntity.getProvince());
            userManagementDto.setDistrict(userEntity.getDistrict());
            userManagementDto.setWard(userEntity.getWard());

            userManagementDtos.add(userManagementDto);

        }

        return userManagementDtos;
    }

    @Override
    @Transactional
    public ResponseEntity<String> handleSaveProfileMng(UserMngProfileRequestDto userMngProfileRequestDto) {
        Long idUser = userMngProfileRequestDto.getId();
        UserEntity userCurrent = this.iuserRepository.findUserEntityById(idUser);
        try {
            userCurrent.setFirstName(userMngProfileRequestDto.getFirstName());
            userCurrent.setLastName(userMngProfileRequestDto.getLastName());
            userCurrent.setGender(userMngProfileRequestDto.getGender().toString());
            userCurrent.setDateOfBirth(DateUtils.convertToDate(userMngProfileRequestDto.getDateOfBirth()));
            userCurrent.setPhone(userMngProfileRequestDto.getPhone());
            userCurrent.getAccountEntity().setEmail(userMngProfileRequestDto.getEmail());
            userCurrent.setInfomationCode(userMngProfileRequestDto.getInformationCode());
            MajorEntity major = this.iMajorRepository.findMajorEntityByCodeMajor(userMngProfileRequestDto.getMajor().toString());
            userCurrent.setMajor(major);
            userCurrent.setAddress(userMngProfileRequestDto.getAddress());
            userCurrent.getAccountEntity().setEmailOfPersonal(userMngProfileRequestDto.getEmailPersonal());
            userCurrent.setProvince(userMngProfileRequestDto.getProvince());
            userCurrent.setDistrict(userMngProfileRequestDto.getDistrict());
            userCurrent.setWard(userMngProfileRequestDto.getWard());
            userCurrent.setAddress(userMngProfileRequestDto.getAvatar());
            this.iuserRepository.save(userCurrent);
            return ResponseEntity.ok().body("Lưu thành công quản lý hồ sơ");
        } catch (Exception e) {
            System.out.println("--ER error save profile management : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Map<String, String> handleGetAllUserByRole() {
        RoleEntity roleEntity = this.iRoleRepository.findRoleEntityById(1L).get();
        List<UserEntity> users = this.iuserRepository.findUserEntitiesByRole(roleEntity);
        Map<String, String> teachers = new HashMap<>();
        for (UserEntity user : users) {
            teachers.put(user.getId().toString(), user.getFirstName() + " " + user.getLastName());
        }
        return teachers;
    }

    @Override
    public UserCreateMgnDto handleGetDataForUserCreate() {
        UserCreateMgnDto userCreateMgnDto = new UserCreateMgnDto();
        Map<String, String> dataMajor = new TreeMap<>();
        Map<String, String> dataGender = new TreeMap<>();
        List<MajorEntity> majors = this.iMajorRepository.findAll();
        for (MajorEntity major : majors) {
            dataMajor.put(major.getId().toString(), major.getContentMajor());
        }
        dataGender.put("NAM", "Nam");
        dataGender.put("NU", "Nữ");
        userCreateMgnDto.setGender(dataGender);
        userCreateMgnDto.setMajors(dataMajor);
        return userCreateMgnDto;
    }

    @Override
    public ProfileResponseDto handleGetDataByUserMngUpdate(Long idUser) {
        ProfileResponseDto profileResponseDto = new ProfileResponseDto();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String email = SecurityUtils.getPrincipal();
        AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(email).get();
        UserEntity userCurrent = account.getUser();
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(userCurrent.getId().toString());
        userResponseDto.setUserCode(userCurrent.getCodeUser());
        userResponseDto.setEmail(userCurrent.getAccountEntity().getEmail());
        userResponseDto.setFirstName(userCurrent.getFirstName());
        userResponseDto.setLastName(userCurrent.getLastName());
        userResponseDto.setMajor(userCurrent.getMajor() == null ? "" : userCurrent.getMajor().getCodeMajor());
        userResponseDto.setGender(userCurrent.getGender().toString());
        userResponseDto.setDateOfBirth(dateFormat.format(userCurrent.getDateOfBirth()));
        userResponseDto.setPhone(userCurrent.getPhone());
        userResponseDto.setInformationCode(userCurrent.getInfomationCode());
        userResponseDto.setAddress(userCurrent.getAddress());
        userResponseDto.setEmailPersonal(userCurrent.getAccountEntity().getEmailOfPersonal());
        userResponseDto.setProvince(userCurrent.getProvince());
        userResponseDto.setDistrict(userCurrent.getDistrict());
        userResponseDto.setWard(userCurrent.getWard());
        profileResponseDto.setDataUser(userResponseDto);
        profileResponseDto.setDataBase(this.handleGetDataForUserCreate());
        return profileResponseDto;

    }

    @Override
    @Transactional
    public ResponseEntity<String> handleCheckExistEmailAndSendMail(String email) throws MessagingException, UnsupportedEncodingException {
        boolean checkExistEmail = this.iAccountRepository.existsByEmail(email);
        boolean checkExistEmailPersonal = this.iAccountRepository.existsByEmailOfPersonal(email);
        try {
            if (!checkExistEmail && !checkExistEmailPersonal) {
                return ResponseEntity.badRequest().body("Email không tồn tại. Vui lòng thử lại!!!");
            } else if (checkExistEmail) {
                AccountEntity account = this.iAccountRepository.findAccountEntityByEmail(email).get();
                String passwordRandom = String.format("%06d", new Random().nextInt(1000000));
                String emailLogin = account.getEmail();
                account.setPassWord(passwordEncoder.encode(passwordRandom));
                boolean sendMail = mailService.sendConfirmLink("caothaiiop1234@gmail.com", passwordRandom, emailLogin);
                if (sendMail) {
                    this.iAccountRepository.save(account);
                    return ResponseEntity.ok().body("Mật khẩu đã được gửi vào email của bạn");
                } else {
                    return ResponseEntity.badRequest().body("Lỗi gửi thư");
                }

            } else {
                AccountEntity account = this.iAccountRepository.findAccountEntityByEmailOfPersonal(email);
                String passwordRandom = String.format("%06d", new Random().nextInt(1000000));
                String emailLogin = account.getEmail();
                account.setPassWord(passwordEncoder.encode(passwordRandom));
                boolean sendMail = mailService.sendConfirmLink("caothaiiop1234@gmail.com", passwordRandom, emailLogin);
                if (sendMail) {
                    this.iAccountRepository.save(account);
                    return ResponseEntity.ok().body("Mật khẩu đã được gửi vào email của bạn");
                } else {
                    return ResponseEntity.badRequest().body("Lỗi gửi thư");
                }
            }

        } catch (Exception e) {
            System.out.println("---ER error sendMail");
            e.printStackTrace();
        }

        return null;
    }


}
