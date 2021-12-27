package com.example.instaclone.example;

public class Singleton {
    final static Singleton obj = new Singleton();
    private Singleton(){

    }

    public static Singleton getInstance(){

        return obj;
    }
}
