package com.yondu.knowledgebase.services;

import com.yondu.knowledgebase.DTO.email.EmailDTO;
import com.yondu.knowledgebase.DTO.user.UserDTO;
import com.yondu.knowledgebase.Utils.Util;
import com.yondu.knowledgebase.entities.User;
import com.yondu.knowledgebase.entities.UserOtp;
import com.yondu.knowledgebase.exceptions.*;
import com.yondu.knowledgebase.repositories.UserOtpRepository;
import com.yondu.knowledgebase.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuthService {

    private Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserOtpRepository userOtpRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordChangesService passwordChangesService;

    public User login(UserDTO.LoginRequest request) {
        log.info("AuthService.login()");
        log.info("user : " + request);

        // Check data
        if(Util.isNullOrWhiteSpace(request.email())){
            throw new MissingFieldException("email");
        }else if(Util.isNullOrWhiteSpace(request.password())){
            throw new MissingFieldException("password");
        }

        User fetchedUser = userRepository.getUserByEmail(request.email());
        if(fetchedUser == null){
            throw new InvalidCredentialsException();
        }

        if(passwordEncoder.matches(request.password(), fetchedUser.getPassword())){
            fetchedUser.setPassword("");
            return fetchedUser;
        }else{
            throw new InvalidCredentialsException();
        }
    }


    public boolean checkEmail(UserDTO.ShortRequest request) {
        log.info("UserService.checkEmail()");
        log.info("request : " + request);

        userRepository.fetchUserByEmail(request.email()).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        return true;
    }

    public void forgotPassword(UserDTO.ShortRequest request) {
        log.info("UserService.forgotPassword()");
        log.info("request : " + request);

        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        String temporaryPassword = Util.passwordGenerator();
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        user.setPassword(encodedPassword);
        user.setPasswordExpiration(LocalDateTime.now());

        userRepository.save(user);

        emailService.forgotPasswordEmail(new EmailDTO.NewUserRequest(user.getEmail(), null, temporaryPassword, "FORGOT-PASSWORD"));
    }

    public void generateOTP(UserDTO.ShortRequest request) {
        log.info("UserService.generateOTP()");
        log.info("request : " + request);

        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        String otp = Util.OtpGenerator();

        String encryptedOtp = passwordEncoder.encode(otp);

        LocalDateTime expirationDate = LocalDateTime.now().plusHours(3); // Expiration set to 3 hours from now

        UserOtp userOTP = new UserOtp();
        userOTP.setUser(user);
        userOTP.setOtp(encryptedOtp);
        userOTP.setCreatedAt(LocalDateTime.now());
        userOTP.setExpirationDate(expirationDate);
        userOTP.setValid(true);

        if (userOtpRepository.existsByUser(user)) invalidateExistingOTP(user);

        userOtpRepository.save(userOTP);

        emailService.forgotPasswordEmail(new EmailDTO.NewUserRequest(user.getEmail(), null, otp, "FORGOT-PASSWORD"));
    }
    private void invalidateExistingOTP(User user) {
        UserOtp existingOTPs = userOtpRepository.findByUser(user);

            userOtpRepository.delete(existingOTPs);
        }

    public User updatePasswordWithOtp(UserDTO.ChangePassRequestV2 request) {
        log.info("UserService.updatePassword()");
        log.info("request : " + request);


        List<UserOtp> validOtps = userOtpRepository.findByIsValidIsTrueAndExpirationDateAfter(LocalDateTime.now());

        UserOtp matchedOtp = null;

        for (UserOtp userOtp : validOtps) {
            if (passwordEncoder.matches(request.otp(), userOtp.getOtp())) {
                matchedOtp = userOtp;
                break;
            }
        }
        if (matchedOtp == null) {
            throw new RequestValidationException("Invalid OTP");
        }

        User findUser = matchedOtp.getUser();

        String newEncodedPassword = passwordEncoder.encode(request.newPassword());
        findUser.setPassword(newEncodedPassword);
        findUser.setPasswordExpiration(LocalDateTime.now().plusMonths(1));

        if(passwordChangesService.isPasswordExist(findUser, request.newPassword())){
            throw new PasswordRepeatException();
        }

        User updatedUser = userRepository.save(findUser);
        passwordChangesService.saveNewPassword(findUser, request.newPassword());

        matchedOtp.setValid(false);
        userOtpRepository.save(matchedOtp);

        return updatedUser;
    }
}
