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
    private static String topicsPath = currDirectory + "\\com.example.ds_project\\data\\topics.txt";
    private static String brokersPath = currDirectory + "\\data\\brokers.txt";


    //TODO: READ LIST FROM TXT
    public void loadTopics() {
 /*
        ArrayList<String> topicsLines = FileHelper.readFile(topicsPath);

        for (String line : topicsLines) {
            topics.add(new Topic(line));
        }
   */
        topics.add(new Topic("DISTRIBUTED_SYSTEMS"));
        topics.add(new Topic("COMPUTER_NETWORKS"));
        topics.add(new Topic("DATABASES"));
        topics.add(new Topic("HUMAN_COMPUTER_CONTACT"));
        topics.add(new Topic("GAME_THEORY"));


    }
    public ArrayList<Topic>  getTopics(){
        return topics;
    }

    public void loadBrokers() {
        brokers.add(new Broker("Broker1", "127.0.0.1", 1234));
        brokers.add(new Broker("Broker2", "127.0.0.1", 1235));
        brokers.add(new Broker("Broker3", "127.0.0.1", 1236));

/*
        ArrayList<String> brokersLines = FileHelper.readFile(brokersPath);

        for (String line : brokersLines) {
            String[] data = line.split(" , ");
            brokers.add(new Broker(data[0], data[1], Integer.parseInt(data[2])));
        }*/
    }

    public void printBrokers() {
        for (Broker line : brokers) {
            System.out.println("name: " + line.name + ", ip: " + line.ip + ", port: " + line.port);
        }
    }

    public Node() {

    }

}