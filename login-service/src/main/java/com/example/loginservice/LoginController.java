package com.example.loginservice;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;


@RestController
public class LoginController {

    private final String correctPassword;
    private final PasswordEncoder passwordEncoder;
    private final String unencryptedPassword = "aa";

    private final Bucket bucket;

    public int counter = 0;

    public LoginController() {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.correctPassword = passwordEncoder.encode("T");
        //Bandwidth limit = Bandwidth.classic(50, Refill.greedy(50, Duration.ofSeconds(10)));
        Bandwidth limit = Bandwidth.classic(20, Refill.intervally(20, Duration.ofSeconds(10)));
        this.bucket = Bucket4j.builder()
                .addLimit(limit)
                .build();

    }

    @GetMapping("/test")
    public String test() {
        return "Hello world";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        System.out.println("Request No: "+ counter++);
       if (bucket.tryConsume(1)) {
            if (passwordEncoder.matches(user.getPassword(), correctPassword)) {
                return new ResponseEntity<>("Login successful for " + user.getUsername(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Validation failed for " + user.getUsername(), HttpStatus.FORBIDDEN);
            }
        }

     return new ResponseEntity<>(HttpStatus.TOO_MANY_REQUESTS);

    }

    @PostMapping("/nocrypt")
    public ResponseEntity<String> noEncryption(@RequestBody User user) {
        System.out.println(user);

        if (unencryptedPassword.equals(user.getPassword())) {
            return new ResponseEntity<>("Login successful for " + user.getUsername(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Validation failed for " + user.getUsername(), HttpStatus.FORBIDDEN);
        }
    }
}
