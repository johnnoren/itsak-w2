package com.example.loginservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
public class LoginController {

    private final String correctPassword;
    private final PasswordEncoder passwordEncoder;

    public LoginController() {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.correctPassword = passwordEncoder.encode("abc");
    }

    @GetMapping("/test")
    public String test() {
        return "Hello world";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        if (passwordEncoder.matches(user.getPassword(), correctPassword)) {
            return new ResponseEntity<>("Login successful for " + user.getUsername(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Validation failed for " + user.getUsername(), HttpStatus.FORBIDDEN);
        }
    }
}
