package com.example.loginservice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        if (passwordEncoder.matches(password, correctPassword)) {
            return new ResponseEntity<>("Login successful for " + username, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Validation failed for " + username, HttpStatus.FORBIDDEN);
        }
    }
}
