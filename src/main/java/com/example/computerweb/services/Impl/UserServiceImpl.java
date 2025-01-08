package com.example.computerweb.services.Impl;

import com.example.computerweb.DTO.UserLoginDTO;
import com.example.computerweb.DTO.UserRegisterDTO;
import com.example.computerweb.components.JwtTokenUtil;
import com.example.computerweb.models.Role;
import com.example.computerweb.models.User;
import com.example.computerweb.repositories.RoleRepository;
import com.example.computerweb.repositories.UserRepository;
import com.example.computerweb.services.IUserService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    @Transactional
    @Override
    public ResponseEntity<String> handleRergister(UserRegisterDTO userRegisterDTO) {
        try {
            System.out.println("111");

            System.out.println("222");

            User user = User.builder()
                    .fullName(userRegisterDTO.getFullName())
                    .email(userRegisterDTO.getEmail())

                    .dateOfBirth(userRegisterDTO.getDateOfBirth())

                    .build();
            String passWord = passwordEncoder.encode(userRegisterDTO.getPassWord());
            user.setPassWord(passWord);
            Optional<Role> roles = this.roleRepository.findById(userRegisterDTO.getRole());
            if (roles.isPresent() ){
                user.setRole(roles.get());
            }
            user.setActive(true);
            // handle save
            this.userRepository.save(user);

            return ResponseEntity.ok().body("Save register success");
        }catch (RuntimeException e){
            System.out.println("--ER : loi ko save register duoc user");
            return ResponseEntity.badRequest().body("save register failed");

        }
    }

    @Override
    public boolean checkEamilExist(String email) {
        Optional<User> user = this.userRepository.findUserByEmail(email);
        return user.isPresent();
    }

    @Override
    public ResponseEntity<String> handleLogin(UserLoginDTO userLoginDTO) {
        Optional<User> existsEmail = this.userRepository.findUserByEmail(userLoginDTO.getEmail());
        if(existsEmail.isPresent() ){
            User userCurrent  = existsEmail.get();
            String passEncode = passwordEncoder.encode(userLoginDTO.getPassWord());
            if ( userCurrent.getPassword().equals(passEncode)){
                try {
                    String token = this.jwtTokenUtil.generateToken(userCurrent);
                    return  ResponseEntity.ok().body(token);
                } catch (Exception e ){
                    e.printStackTrace();
                    System.out.println("--ER : Lỗi không tạo được token " + e.getMessage());
                }

            }else {
                return ResponseEntity.badRequest().body("Mật khẩu không chính xác");
            }
        }

       return ResponseEntity.badRequest().body("Email đã tồn tại hoặc đã được đăng kí");

    }
}
