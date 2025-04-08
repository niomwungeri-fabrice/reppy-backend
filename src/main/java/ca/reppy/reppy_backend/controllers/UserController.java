package ca.reppy.reppy_backend.controllers;

import ca.reppy.reppy_backend.entities.User;
import ca.reppy.reppy_backend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/users")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        User user = this.authService.getUserByEmail(principal.getName());
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
