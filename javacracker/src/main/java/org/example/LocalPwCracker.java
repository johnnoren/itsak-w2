package org.example;

import java.util.Arrays;

public class LocalPwCracker implements PwCracker {

    private final byte fromChar;
    private final byte toChar;

    public LocalPwCracker(byte fromChar, byte toChar) {
        this.fromChar = fromChar;
        this.toChar = toChar;
    }

    private byte[] recursiveCracker(byte[] correctByteArray, int maxLength, int currentLength, byte[] currentByteArray) {

        if (currentLength == maxLength) {
            if (Arrays.equals(currentByteArray, correctByteArray)) {
                return currentByteArray;
            }
            return null;
        }

        for (byte i = this.fromChar; i <= this.toChar; i++) {
            currentByteArray[currentLength] = i;
            byte[] result = recursiveCracker(correctByteArray, maxLength, currentLength + 1, currentByteArray);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    public String crack(int maxLength, String correctString) {

        System.out.println("Cracking...");

        byte[] correctByteArray = correctString.getBytes();

        for (int length = 1; length <= maxLength; length++) {
            byte[] currentByteArray = new byte[length];
            byte[] result = recursiveCracker(correctByteArray, length, 0, currentByteArray);
            if (result != null) {
                return new String(result);
            }
        }
        return "Not found";
    }
}
