package com.example.ds_project;

import java.io.File;
import java.util.*;
/*
import helpers.FileHelper;
import models.Topic;
 */


public class Node {
    public ArrayList<Broker> brokers = new ArrayList<>();
    public ArrayList<Topic> topics = new ArrayList<>();
    private static File currDirectory = new File(new File("").getAbsolutePath());
    private static String topicsPath = currDirectory + "\\data\\Topics.txt";
    private static String brokersPath = currDirectory + "\\data\\Brokers.txt";

    public void loadTopics() {
        ArrayList<String> topicsLines = FileHelper.readFile(topicsPath);
        for (String line : topicsLines) {
            topics.add(new Topic(line));
        }
    }
    public ArrayList<Topic>  getTopics(){
        return topics;
    }
    public void loadBrokers() {
        ArrayList<String> brokersLines = FileHelper.readFile(brokersPath);
        for (String line : brokersLines) {
            String[] data = line.split(" , ");
            brokers.add(new Broker(data[0], data[1], Integer.parseInt(data[2])));
        }
    }

    public void printBrokers() {
        for (Broker line : brokers) {
            System.out.println("name: " + line.name + ", ip: " + line.ip + ", port: " + line.port);
        }
    }

    public Node() {

    }

}