package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.ProfileResponseDto;
import com.example.computerweb.DTO.dto.UserResponseDto;
import com.example.computerweb.DTO.dto.UserCreateMgnDto;
import com.example.computerweb.DTO.dto.UserManagementDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserLoginDto;
import com.example.computerweb.DTO.requestBody.userRequest.UserMngProfileRequestDto;
import com.example.computerweb.DTO.requestBody.userRequest.UserProfileRequestDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserRegisterDto;
import com.example.computerweb.components.JwtTokenUtil;
import com.example.computerweb.models.entity.MajorEntity;
import com.example.computerweb.models.entity.RoleEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.IMajorRepository;
import com.example.computerweb.repositories.IRoleRepository;
import com.example.computerweb.repositories.IUserRepository;
import com.example.computerweb.services.IUserService;
import com.example.computerweb.services.MailService;
import com.example.computerweb.utils.DateUtils;
import com.example.computerweb.utils.SecurityUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public boolean checkEamilExist(String email) {
        return this.iuserRepository.existsByEmail(email);
    }

    @Override
    public boolean checkPhoneExist(String phone) {
        return this.iuserRepository.existsByPhone(phone);
    }


    @Transactional
    @Override
    public ResponseEntity<String> handleRergister(UserRegisterDto userRegisterDTO) {
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
            RoleEntity roles = this.iRoleRepository.findById(userRegisterDTO.getRoleId()).get();
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

            // set email
            String domain = "@ptithcm.edu.vn";
            String email = codeUser + domain;
            user.setEmail(email);

            //set password
            String passWordDefault = userRegisterDTO.getDateOfBirth().toString();
            String passWord = passwordEncoder.encode(passWordDefault);
            user.setPassWord(passWord);

            log.info("Entity User : {}", user);
            // handle save
            // Error of SQL VD : UNIQUE KEY ==> 403 Forbiden
            this.iuserRepository.save(user);

            return ResponseEntity.ok().body("Register success");
        } catch (RuntimeException e) {
            System.out.println("--ER : Can not save Register");
            return ResponseEntity.badRequest().body("Register Failure");
        }

    }


    @Override
    public ResponseEntity<String> handleLogin(UserLoginDto userLoginDTO) {
        boolean existsEmail = checkEamilExist(userLoginDTO.getEmail());
        if (existsEmail) {
            UserEntity userCurrent = this.iuserRepository.findUserEntityByEmail(userLoginDTO.getEmail()).get();
            if (passwordEncoder.matches(userLoginDTO.getPassWord(), userCurrent.getPassword())) {
                try {
                    String testEmail = userLoginDTO.getEmail();
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userLoginDTO.getEmail(),
                            userLoginDTO.getPassWord(),
                            userCurrent.getAuthorities()
                    );

                    authenticationManager.authenticate(authenticationToken);

                    String token = this.jwtTokenUtil.generateToken(userCurrent);
                    return ResponseEntity.ok().body(token);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("--ER : Lỗi không tạo được token " + e.getMessage());
                }

            } else {
                return ResponseEntity.badRequest().body("Email or Password incorrect");
            }
        }

        return ResponseEntity.badRequest().body("Email or Password incorrect");

    }

    @Override
    @Transactional
    public ResponseEntity<String> handleUpdateFieldProfile(UserProfileRequestDto userProfileDto) {
        try {
            UserEntity userEntity = this.iuserRepository.findUserEntityById(userProfileDto.getId());
            userEntity.setPhone(userProfileDto.getPhone());
            userEntity.setEmailPersonal(userProfileDto.getEmailPersonal());
            userEntity.setProvince(userProfileDto.getProvince());
            userEntity.setDistrict(userProfileDto.getDistrict());
            userEntity.setWard(userProfileDto.getWard());
            userEntity.setAddress(userProfileDto.getAddress());
            userEntity.setInfomationCode(userProfileDto.getInformationCode());
            userEntity.setPassWord(passwordEncoder.encode(userProfileDto.getResetPassword()));
            this.iuserRepository.save(userEntity);

            return ResponseEntity.ok().body("Update profile success");
        } catch (Exception e) {
            System.out.println("--ER error save field profile :" + e.getMessage());
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().body("Update profile failed");
    }

    @Override
    public Map<String, String> handleGetDataUserCurrent() {

        Map<String, String> userCurrent = new TreeMap<>();

        String emailUser = SecurityUtils.getPrincipal();
        UserEntity userEntity = this.iuserRepository.findUserEntityByEmail(emailUser).get();
        userCurrent.put("userName", userEntity.getFirstName() + " " + userEntity.getLastName());
        userCurrent.put("role", userEntity.getRole().getContentRole());
        userCurrent.put("userId" , userEntity.getId().toString());
        userCurrent.put("userCode" , userEntity.getCodeUser());
        return userCurrent;
    }

    @Override
    public ProfileResponseDto handleGetDataProfile() {
        ProfileResponseDto profileResponseDto = new ProfileResponseDto();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        UserEntity userCurrent = this.iuserRepository.findUserEntityByEmail(SecurityUtils.getPrincipal()).get();
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(userCurrent.getId().toString());
        userResponseDto.setUserCode(userCurrent.getCodeUser());
        userResponseDto.setEmail(userCurrent.getEmail());
        userResponseDto.setFirstName(userCurrent.getFirstName());
        userResponseDto.setLastName(userCurrent.getLastName());
        userResponseDto.setMajor(userCurrent.getMajor()== null ? "" : userCurrent.getMajor().getCodeMajor() );

        userResponseDto.setGender(userCurrent.getGender());
        userResponseDto.setDateOfBirth(dateFormat.format(userCurrent.getDateOfBirth()));
        userResponseDto.setPhone(userCurrent.getPhone());
        userResponseDto.setInformationCode(userCurrent.getInfomationCode());
        userResponseDto.setAddress(userCurrent.getAddress());
        userResponseDto.setEmailPersonal(userCurrent.getEmailPersonal());
        userResponseDto.setProvince(userCurrent.getProvince());
        userResponseDto.setDistrict(userCurrent.getDistrict());
        userResponseDto.setWard(userCurrent.getWard());
        profileResponseDto.setDataUser(userResponseDto);
        profileResponseDto.setDataBase(this.handleGetDataForUserCreate());
        return profileResponseDto;
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
            userManagementDto.setEmail(userEntity.getEmail());
            userManagementDto.setInformationCode(userEntity.getInfomationCode());
            userManagementDto.setMajor(userEntity.getMajor()== null ? "" : userEntity.getMajor().getCodeMajor() );
            userManagementDto.setAddress(userEntity.getAddress());
            userManagementDto.setEmailPersonal(userEntity.getEmailPersonal());
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
            userCurrent.setEmail(userMngProfileRequestDto.getEmail());
            userCurrent.setInfomationCode(userMngProfileRequestDto.getInformationCode());
            MajorEntity major = this.iMajorRepository.findMajorEntityByCodeMajor(userMngProfileRequestDto.getMajor().toString());
            userCurrent.setMajor(major);
            userCurrent.setAddress(userMngProfileRequestDto.getAddress());
            userCurrent.setEmailPersonal(userMngProfileRequestDto.getEmailPersonal());
            userCurrent.setProvince(userMngProfileRequestDto.getProvince());
            userCurrent.setDistrict(userMngProfileRequestDto.getDistrict());
            userCurrent.setWard(userMngProfileRequestDto.getWard());
            userCurrent.setAddress(userMngProfileRequestDto.getAvatar());
            this.iuserRepository.save(userCurrent);
            return ResponseEntity.ok().body("Save profile management success");
        } catch (Exception e) {
            System.out.println("--ER error save profile management : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Map<String, String> handleGetAllUserByRole() {
        RoleEntity roleEntity = this.iRoleRepository.findById(1).get();
        List<UserEntity> users = this.iuserRepository.findAllByRole(roleEntity);
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
        UserEntity userCurrent = this.iuserRepository.findUserEntityByEmail(SecurityUtils.getPrincipal()).get();
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(userCurrent.getId().toString());
        userResponseDto.setUserCode(userCurrent.getCodeUser());
        userResponseDto.setEmail(userCurrent.getEmail());
        userResponseDto.setFirstName(userCurrent.getFirstName());
        userResponseDto.setLastName(userCurrent.getLastName());
        userResponseDto.setMajor(userCurrent.getMajor()== null ? "" : userCurrent.getMajor().getCodeMajor() );
        userResponseDto.setGender(userCurrent.getGender().toString());
        userResponseDto.setDateOfBirth(dateFormat.format(userCurrent.getDateOfBirth()));
        userResponseDto.setPhone(userCurrent.getPhone());
        userResponseDto.setInformationCode(userCurrent.getInfomationCode());
        userResponseDto.setAddress(userCurrent.getAddress());
        userResponseDto.setEmailPersonal(userCurrent.getEmailPersonal());
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
        boolean checkExistEmail = this.iuserRepository.existsByEmail(email);
        boolean checkExistEmailPersonal = this.iuserRepository.existsByEmailPersonal(email);
        try {
            if ( !checkExistEmail && !checkExistEmailPersonal){
                return ResponseEntity.badRequest().body("Email not exist. Please try again!!!");
            }else if (checkExistEmail){
                UserEntity user = this.iuserRepository.findUserEntityByEmail(email).get();
                String passwordRandom = String.format("%06d", new Random().nextInt(1000000));
                String emailLogin = user.getEmail();
                user.setPassWord(passwordEncoder.encode(passwordRandom));
              boolean sendMail=  mailService.sendConfirmLink("caothaiiop1234@gmail.com",passwordRandom,emailLogin);
               if ( sendMail){
                   this.iuserRepository.save(user);
                   return ResponseEntity.ok().body("The password has send in your email");
               }else {
                   return ResponseEntity.badRequest().body("Error send mail");
               }

            }else {
                UserEntity user = this.iuserRepository.findUserEntityByEmailPersonal(email);
                String passwordRandom = String.format("%06d", new Random().nextInt(1000000));
                String emailLogin = user.getEmail();
                user.setPassWord(passwordEncoder.encode(passwordRandom));
                boolean sendMail=  mailService.sendConfirmLink("caothaiiop1234@gmail.com",passwordRandom,emailLogin);
                if ( sendMail){
                    this.iuserRepository.save(user);
                    return ResponseEntity.ok().body("The password has send in your email");
                }else {
                    return ResponseEntity.badRequest().body("Error send mail");
                }
            }

        }catch (Exception e){
            System.out.println("---ER error sendMail");
            e.printStackTrace();
        }

        return null;
    }


}
