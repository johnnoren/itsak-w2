package com.example.passwordcracker;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;


@RestController
public class CrackerController {

    private final RestTemplate restTemplate;

    public CrackerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/crack")
    public String crack(@RequestParam String url, @RequestParam String username) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, "abc");

        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();
    }

}
