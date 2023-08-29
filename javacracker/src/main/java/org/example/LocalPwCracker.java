package org.example;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

public class LocalPwCracker implements PwCracker {



    private String recursiveCracker(String correctString, int maxLength, int currentLength, byte[] currentPassword) {
        if (currentLength == maxLength) {
            if (Arrays.equals(currentPassword, correctString.getBytes())) {
                return new String(currentPassword);
            }
            return null;
        }

        for (int i = 33; i < 126; i++) {
            currentPassword[currentLength] = (byte) i;
            String result = recursiveCracker(correctString, maxLength, currentLength + 1, currentPassword);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public String crack(int maxLength, String correctString) {

        LocalTime start = LocalTime.now();

        for (int length = 1; length <= maxLength; length++) {
            byte[] passwordBytes = new byte[length];
            String result = recursiveCracker(correctString, length, 0, passwordBytes);
            if (result != null) {
                LocalTime end = LocalTime.now();
                System.out.println(start.until(end, ChronoUnit.SECONDS) + " seconds");
                return result;
            }
        }
        return "Not found";
    }
}
