package com.example.ds_project;

import java.io.IOException;
import java.net.UnknownHostException;
import java.io.*;
import java.net.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class TestPublisher {
    public static void main(String[] args) throws UnknownHostException, IOException {
        Publisher publisher = new Publisher("Publisher");
        publisher.start();
    }
}