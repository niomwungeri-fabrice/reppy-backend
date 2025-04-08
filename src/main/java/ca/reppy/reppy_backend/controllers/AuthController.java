package ca.reppy.reppy_backend.controllers;

import ca.reppy.reppy_backend.dtos.TokenResponse;
import ca.reppy.reppy_backend.dtos.UserSignInRequest;
import ca.reppy.reppy_backend.dtos.UserSignUpRequest;
import ca.reppy.reppy_backend.dtos.UserSignUpResponse;
import ca.reppy.reppy_backend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserSignUpRequest request) {
        UserSignUpResponse userSignupResponse = UserSignUpResponse.builder().message(
                authService.registerUser(request.getEmail())
        ).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(userSignupResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserSignInRequest request) {
        TokenResponse token = TokenResponse.builder().token(
                authService.verifyOTP(request.getEmail(), request.getOtp())
        ).build();
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }
}
