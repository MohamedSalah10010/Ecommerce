package com.learn.ecommerce.api.controller.auth;

import com.learn.ecommerce.api.model.LoginBody;
import com.learn.ecommerce.api.model.LoginResponse;
import com.learn.ecommerce.api.model.RegistrationBody;
import com.learn.ecommerce.exception.EmailFailureException;
import com.learn.ecommerce.exception.UserAlreadyExistsException;
import com.learn.ecommerce.exception.UserIsNotVerifiedException;
import com.learn.ecommerce.model.LocalUser;
import com.learn.ecommerce.repository.LocalUserRepo;
import com.learn.ecommerce.services.JwtService;
import com.learn.ecommerce.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private UserService userService;
    private JwtService jwtService;
    private LocalUserRepo localUserRepo;

    public AuthenticationController(JwtService jwtService, LocalUserRepo localUserRepo, UserService userService) {
        this.jwtService = jwtService;
        this.localUserRepo = localUserRepo;
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity registerUser(@Valid @RequestBody RegistrationBody registrationBody)
    {

        try {
            userService.registerUser(registrationBody);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (UserAlreadyExistsException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }catch (EmailFailureException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginBody loginBody)
    {
        String jwt = null;
        try {
             jwt = userService.loginUser(loginBody);
        }catch (UserIsNotVerifiedException ex)
        {
            LoginResponse loginResponse = new LoginResponse();
            String reason = "Email is not verified. Please verify your email to login.";
            if(ex.isEmailSent())
            {
                reason += " A new verification email has been sent to your email address.";
            }
            else{
                reason += " Failed to send verification email. Please try resending verification email.";
            }
            loginResponse.setFailureMessage(reason);
            loginResponse.setSuccess(false);
            loginResponse.setJwt(null);

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(loginResponse);

        }
        catch (EmailFailureException ex)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        if (jwt == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        else{
        LoginResponse response = new LoginResponse();
        response.setJwt(jwt);
        response.setSuccess(true);
        return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/me")
    public LocalUser getLoggedInUserProfile(@AuthenticationPrincipal LocalUser user)
    {
        return user;
    }

    @GetMapping("/verify")
    public ResponseEntity verifyUserEmail(@RequestParam("token") String token)
    {
        boolean verified = userService.verifyUserEmail(token);
        if(verified) {
            return ResponseEntity.ok().body("Account is verified successfully!!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
