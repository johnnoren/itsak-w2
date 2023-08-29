package org.example;

public class Main {

    public Main() {
        PwCracker pwCracker = new BlockingQueueHttpPwCracker();
        //String password = "ab!";

        System.out.println(pwCracker.crack(3, "http://localhost:8080/nocrypt"));


    }

    public static void main(String[] args) {

        new Main();
    }
}