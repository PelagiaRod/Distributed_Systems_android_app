package com.example.ds_project;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

//import models.Topic;

public class OldBroker {
    // push(topic,value) -> [broker]
    // pull(topic,[broker]) -> [topic,value]

    static List<Consumer> enrolledConsumers = new ArrayList<Consumer>();
    static List<Publisher> enrolledPublishers = new ArrayList<Publisher>();
    String topic;

    private String name, ip;
    private int port;
    ServerSocket serverSocket;
    Socket client;
    List<Topic> linkedTopics = new ArrayList<Topic>(); // hashmap == queue
    public HashMap<Topic, Queue<String>> topicsQueue = new HashMap<>();

    public OldBroker() {
    }

    public OldBroker(String name, String ip, int port) {

        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public synchronized void init() {
        // n.readRouteCodes();
        // setTopics(n);
        // setPubOwnTopics(this.name);
        // settopicsQueue(n);
        connectToBroker();
    }

    public OldBroker(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public OldBroker(String topic) {
        this.topic = topic;
    }

    public OldBroker(DatagramSocket datagramSocket) {
    }

    public OldBroker(List<Consumer> enrolledConsumers, List<Publisher> enrolledPublishers) {
        this.enrolledConsumers = enrolledConsumers;
        this.enrolledPublishers = enrolledPublishers;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    Consumer acceptConnection(Consumer name) {
        for (Consumer registeredUser : enrolledConsumers) {
            if (registeredUser == name) {
                try {
                    client = serverSocket.accept();
                    System.out.println("Publisher is connected!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        enrolledConsumers.add(name);
        return name;
    }

    Publisher acceptConnection(Publisher publisher) {
        for (Publisher registeredPublisher : enrolledPublishers) {
            if (registeredPublisher == publisher) {
                try {
                    client = serverSocket.accept();
                    System.out.println("Publisher is connected!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        enrolledPublishers.add(publisher);
        return publisher;
    }

    void filterConsumers(String consumers) {
    }

    void notifyBrokersOnChanges() {
    }

    void notifyPublishers(String publisher) {
        for (Publisher registeredPublishers : enrolledPublishers) {
        }
    }

    synchronized void connectToBroker() {

        try {
            OldBroker br = new OldBroker(new ServerSocket(4321));
            // ip = br.getIp();
            // port = br.getPort();
            Socket client1 = new Socket("127.0.0.1", 4321);
            // ServerSocket serverSocket = new ServerSocket(port);
            // BrokerHandler handler = new BrokerHandler(client1);
            // Thread t = new Thread(handler);
            // t.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Thread t = new Thread(br);
        // t.start();
        // }
    }

    public void closeBroker() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Topic> getlinkedTopics() {
        return this.linkedTopics;
    }

    public int getPort() {
        return this.port;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrokerName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String ipPlusPort = this.getIp() + this.getPort();
            byte[] messageDigest = md.digest(ipPlusPort.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            return no.intValue();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
