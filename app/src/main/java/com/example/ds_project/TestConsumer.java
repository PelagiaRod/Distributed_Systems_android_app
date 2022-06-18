package com.example.ds_project;

import android.widget.TextView;

import java.io.IOException;
import java.net.UnknownHostException;

public class TestConsumer {
    public static void main(String[] args) throws UnknownHostException, IOException {
        Consumer consumer = new Consumer("Consumer");
        consumer.start();

    }
}