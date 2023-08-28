package com.example.loginservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class LoginController {

@PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return "Logged in as: " + username + " with password: " + password;
}
}
