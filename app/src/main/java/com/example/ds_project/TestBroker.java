package com.example.ds_project;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class TestBroker {
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        Node node = new Node();
        node.loadBrokers();
        node.printBrokers();
        System.out.print("Select broker (1 or 2 or 3): ");
        Scanner scanner = new Scanner(System.in);
        Integer choice = scanner.nextInt();
        if (choice != 1 && choice != 2 && choice != 3) {
            System.out.println("Invalid argument");
            System.exit(1);
        }
        Broker selectedBroker = node.brokers.get(choice - 1);
        Broker broker = new Broker(selectedBroker.name, selectedBroker.ip, selectedBroker.port);
        broker.start();
    }
}