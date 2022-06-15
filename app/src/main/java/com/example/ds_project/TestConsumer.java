package com.example.ds_project;

import java.io.IOException;
import java.net.UnknownHostException;

public class TestConsumer {
    public static void main(String[] args) throws UnknownHostException, IOException {
        Consumer consumer = new Consumer();
        consumer.start();
    }
}