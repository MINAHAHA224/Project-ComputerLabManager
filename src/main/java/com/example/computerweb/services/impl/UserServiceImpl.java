package com.example.computerweb.services.impl;

import com.example.computerweb.DTO.requestBody.accessRequest.UserLoginDto;
import com.example.computerweb.DTO.requestBody.accessRequest.UserRegisterDto;
import com.example.computerweb.components.JwtTokenUtil;
import com.example.computerweb.models.entity.RoleEntity;
import com.example.computerweb.models.entity.UserEntity;
import com.example.computerweb.repositories.IRoleRepository;
import com.example.computerweb.repositories.IUserRepository;
import com.example.computerweb.services.IUserService;
import com.example.computerweb.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

                    String token = "token" + ":" + this.jwtTokenUtil.generateToken(userCurrent);
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
