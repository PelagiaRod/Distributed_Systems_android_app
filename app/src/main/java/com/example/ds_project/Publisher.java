package com.example.ds_project;
// import javafx.util.Pair;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/*
import models.MultimediaFile;
import models.ProfileName;
import models.Topic;
import models.Value;
 */

//User profile
public class Publisher extends Node implements Runnable {
    // Socket client;
    String username;
    ProfileName profileName;
    private List<Broker> connectedBrokers;
    private static Boolean flag = true;
    private HashMap<ProfileName, AbstractMap.SimpleEntry<String, Value>> queueOfTopics;
    private DataInputStream input;
    private DataOutputStream output;
    private Socket client;
    private static String chatServer = "127.0.0.1";
    private static File mediaDirectory = new File(new File("").getAbsolutePath() + "/data/media/");

    public Publisher(String username) {

        loadTopics();
        this.username = username;
    }

    // TODO CONNECT TO RIGHT BROKER
    // AND PUSH DATA TO BROKER`S QUEUE
    public void start() throws UnknownHostException, IOException {

                client = new Socket(InetAddress.getByName(chatServer), 1234);
                input = new DataInputStream(client.getInputStream());
                output = new DataOutputStream(client.getOutputStream());

                if (!flag)
                    disconnect();
    }

    private void disconnect() {
        System.out.println("\nClosing connection");
        try {
            output.close();
            input.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
    }

    ArrayList<Value> generateChunks(MultimediaFile file) {
        Value value = new Value(file);
        ArrayList<Value> chunks = new ArrayList<>();
        return chunks;
    }

    public List<Broker> getBrokerList() {
        return this.connectedBrokers;
    }

    // TODO
    // void hashTopic(String topic) {
    // Broker nd = new Broker();
    // // Broker broker = new Broker();
    // for (NodeNew brok : nd.brokers) {
    // if (this.profileName.getUserVideoFilesMap().containsValue(topic))

    // {

    // }
    // }
    // }

    void notifyBrokersNewMessage(String message) {

    }

    void notifyFailure(Broker fail) {

    }

    // synchronized method in order to avoid a race condition and
    // ALLOW only one thread to execute this block at any given time
    public synchronized void push(String subject) {
        try {
            output.writeUTF(username);
            output.writeUTF(subject);
            output.writeUTF("publisher");

            // sendMessage thread
            Thread sendMessage = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (flag) {
                        System.out.println("1. Upload file. \n2. Write text.");
                        Scanner scanner = new Scanner(System.in);
                        String type = scanner.nextLine();
                        switch (type) {
                            case "1":
                                try {
                                    output.writeUTF("1");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                upload();
                                break;
                            case "2":
                                String msg = scanner.nextLine();

                                try {
                                    // write on the output stream
                                    output.writeUTF("2");
                                    output.writeUTF(username + "#" + msg);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case "exit":
                                flag = false;
                                break;
                            default:
                                System.out.println("You must select either 1 or 2");
                                System.out.println("Or say 'exit' to close connection");
                                break;
                        }
                    }
                }

            });

            sendMessage.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void upload() {
        try {
            String contents[] = mediaDirectory.list();
            for (String name : contents) {
                System.out.println(name);
            }
            System.out.println("Write the name of the file you want to upload.");
            Scanner in = new Scanner(System.in);
            String fileName = in.nextLine();
            while (true) {
                if (!Arrays.asList(contents).contains(fileName)) {
                    System.out.println("File not found. Please try again.");
                    fileName = in.nextLine();
                } else {
                    break;
                }
            }
            // Value value = new Value(new MultimediaFile(fileName));

            FileInputStream fileInputStream = new FileInputStream(mediaDirectory + "\\" + fileName);
            File file = new File(mediaDirectory + "\\" + fileName);

            byte[] fileNameBytes = fileName.getBytes(); // StandardCharsets.UTF_8

            int count;
            output.writeInt(fileNameBytes.length);
            output.write(fileNameBytes);

            byte[] fileContentBytes = new byte[(int) file.length()];
            output.writeInt(fileContentBytes.length);

            // break in chunks and send file
            while ((count = fileInputStream.read(fileContentBytes)) > 0) {
                output.write(fileContentBytes, 0, count);
            }
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}