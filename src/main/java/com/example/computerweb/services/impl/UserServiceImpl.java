package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.dto.ProfileResponseDto;
import com.example.computerweb.DTO.dto.UserManagementDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserLoginDto;
import com.example.computerweb.DTO.requestBody.userRequest.UserMngProfileRequestDto;
import com.example.computerweb.DTO.requestBody.userRequest.UserProfileRequestDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserRegisterDto;
import com.example.computerweb.components.JwtTokenUtil;
import com.example.computerweb.models.entity.RoleEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.models.enums.Gender;
import com.example.computerweb.repositories.IRoleRepository;
import com.example.computerweb.repositories.IUserRepository;
import com.example.computerweb.services.IUserService;
import com.example.computerweb.utils.DateUtils;
import com.example.computerweb.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {

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
       return  this.iuserRepository.existsByPhone(phone);
    }


    @Transactional
    @Override
    public ResponseEntity<String> handleRergister(UserRegisterDto userRegisterDTO) {
        try {
            Gender test = userRegisterDTO.getGender();
            UserEntity user = UserEntity.builder()
                    .firstName(userRegisterDTO.getFirstName())
                    .lastName(userRegisterDTO.getLastName())
                    .phone(userRegisterDTO.getPhone())

                    .gender(userRegisterDTO.getGender())
                    .email(userRegisterDTO.getEmail())
                   // .dateOfBirth(userRegisterDTO.getDateOfBirth())

                    .build();
            String passWord = passwordEncoder.encode(userRegisterDTO.getPassword());
            user.setPassWord(passWord);
            Optional<RoleEntity> roles = this.iRoleRepository.findById(userRegisterDTO.getRoleId());
            if (roles.isPresent() ){
                user.setRole(roles.get());
            }
            user.setDateOfBirth(DateUtils.convertToDate(userRegisterDTO.getDateOfBirth()));
            log.info("Entity User : {}" ,user );
            // handle save
            // Error of SQL VD : UNIQUE KEY ==> 403 Forbiden
            this.iuserRepository.save(user);

            return ResponseEntity.ok().body("Register success");
        }catch (RuntimeException e){
            System.out.println("--ER : Can not save Register");
            return ResponseEntity.badRequest().body("Register Failure");
        }

    }



    @Override
    public ResponseEntity<String> handleLogin(UserLoginDto userLoginDTO) {
       boolean existsEmail = checkEamilExist(userLoginDTO.getEmail());
        if(existsEmail ){
            UserEntity userCurrent  = this.iuserRepository.findUserEntityByEmail(userLoginDTO.getEmail()).get();
            if ( passwordEncoder.matches(userLoginDTO.getPassWord() , userCurrent.getPassword())){
                try {
                    String testEmail = userLoginDTO.getEmail();
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userLoginDTO.getEmail(),
                            userLoginDTO.getPassWord(),
                            userCurrent.getAuthorities()
                    );

                    authenticationManager.authenticate(authenticationToken);

                    String token =  this.jwtTokenUtil.generateToken(userCurrent);
                    return  ResponseEntity.ok().body(token);
                } catch (Exception e ){
                    e.printStackTrace();
                    System.out.println("--ER : Lỗi không tạo được token " + e.getMessage());
                }

            }else {
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
            this.iuserRepository.save(userEntity);

            return ResponseEntity.ok().body("Update profile success");
        }catch (Exception e){
            System.out.println("--ER error save field profile :" + e.getMessage());
            e.printStackTrace();
        }

     return ResponseEntity.badRequest().body("Update profile failed");
    }

    @Override
    public Map<String, String> handleGetDataUserCurrent() {

        Map<String,String> userCurrent = new TreeMap<>();

        String emailUser = SecurityUtils.getPrincipal();
      UserEntity  userEntity = this.iuserRepository.findUserEntityByEmail(emailUser).get();
        userCurrent.put("userName" , userEntity.getFirstName()+ " " + userEntity.getLastName()) ;
        userCurrent.put("role" , userEntity.getRole().getContentRole());
        return userCurrent;
    }

    @Override
    public ProfileResponseDto handleGetDataProfile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        UserEntity userCurrent = this.iuserRepository.findUserEntityByEmail(SecurityUtils.getPrincipal()).get();
        ProfileResponseDto profileResponseDto = new ProfileResponseDto();
        profileResponseDto.setId(userCurrent.getId().toString());
        profileResponseDto.setEmail(userCurrent.getEmail());
        profileResponseDto.setFirstName(userCurrent.getFirstName());
        profileResponseDto.setLastName(userCurrent.getLastName());
        profileResponseDto.setMajor(userCurrent.getMajor());
        profileResponseDto.setGender(userCurrent.getGender().toString());
        profileResponseDto.setDateOfBirth(dateFormat.format(userCurrent.getDateOfBirth()));
        profileResponseDto.setPhone(userCurrent.getPhone());
        profileResponseDto.setInformationCode(userCurrent.getInfomationCode());
        profileResponseDto.setAddress(userCurrent.getAddress());
        profileResponseDto.setEmailPersonal(userCurrent.getEmailPersonal());
        profileResponseDto.setProvince(userCurrent.getProvince());
        profileResponseDto.setDistrict(userCurrent.getDistrict());
        profileResponseDto.setWard(userCurrent.getWard());

        return profileResponseDto;
    }

    @Override
    public List<UserManagementDto> handleGetAllDataUser() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        List<UserManagementDto> userManagementDtos = new ArrayList<>();
        List<UserEntity> userEntities = this.iuserRepository.findAll();
        for ( UserEntity userEntity : userEntities){
            UserManagementDto userManagementDto = new UserManagementDto();
            userManagementDto.setId(userEntity.getId().toString());
            userManagementDto.setFirstName(userEntity.getFirstName());
            userManagementDto.setLastName(userEntity.getLastName());
            userManagementDto.setGender(userEntity.getGender().toString());
            userManagementDto.setDateOfBirth(dateFormat.format(userEntity.getDateOfBirth()));
            userManagementDto.setPhone(userEntity.getPhone());
            userManagementDto.setEmail(userEntity.getEmail());
            userManagementDto.setInformationCode(userEntity.getInfomationCode());
            userManagementDto.setMajor(userEntity.getMajor());
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
            userCurrent.setGender(userMngProfileRequestDto.getGender());
            userCurrent.setDateOfBirth(DateUtils.convertToDate(userMngProfileRequestDto.getDateOfBirth()) );
            userCurrent.setPhone(userMngProfileRequestDto.getPhone());
            userCurrent.setEmail(userMngProfileRequestDto.getEmail());
            userCurrent.setInfomationCode(userMngProfileRequestDto.getInformationCode());
            userCurrent.setMajor(userMngProfileRequestDto.getMajor().toString());
            userCurrent.setAddress(userMngProfileRequestDto.getAddress());
            userCurrent.setEmailPersonal(userMngProfileRequestDto.getEmailPersonal());
            userCurrent.setProvince(userMngProfileRequestDto.getProvince());
            userCurrent.setDistrict(userMngProfileRequestDto.getDistrict());
            userCurrent.setWard(userMngProfileRequestDto.getWard());
            userCurrent.setAddress(userMngProfileRequestDto.getAvatar());
            this.iuserRepository.save(userCurrent);
            return ResponseEntity.ok().body("Save profile management success");
        }catch (Exception e){
            System.out.println("--ER error save profile management : "+ e.getMessage());
            e.printStackTrace();
        }

    return null;
    }

    @Override
    public Map<String, String> handleGetAllUserByRole() {
        RoleEntity roleEntity = this.iRoleRepository.findById(1).get();
        List<UserEntity> users = this.iuserRepository.findAllByRole(roleEntity);
        Map<String, String> teachers = new HashMap<>();
        for ( UserEntity user : users ){
            teachers.put(user.getId().toString() , user.getFirstName() +" "+user.getLastName());
        }
        return teachers;
    }


}
