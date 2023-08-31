package org.example;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Main {

    public Main() {

        /*PwCracker pwCracker = new LocalPwCracker((byte) 33, (byte) 126);
        String password = "ab!c6!";

        LocalTime start = LocalTime.now();

        System.out.println(pwCracker.crack(6, password));

        LocalTime end = LocalTime.now();
        System.out.println(start.until(end, ChronoUnit.SECONDS) + " seconds");*/

        PwCracker pwCracker = new BlockingQueueHttpPwCracker((byte) 33, (byte) 126);
        System.out.println(pwCracker.crack(4, "http://localhost:8080/nocrypt"));

    }

    public static void main(String[] args) {

        new Main();
    }
}