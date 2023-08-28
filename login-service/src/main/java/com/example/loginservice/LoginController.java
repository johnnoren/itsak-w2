package com.example.loginservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class LoginController {

@GetMapping("/user")
    public String login(Principal principal) {
    return "Logged in as: " + principal.getName();
}
}
