package com.example.ds_project;

import android.os.Environment;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/*
import models.ProfileName;
import models.Topic;
import models.Value;
 */

//subscriber service
public class Consumer extends Node implements Runnable {
    ProfileName subscriber;
    static String username;
    private DataInputStream input;
    private DataOutputStream output;
    private Socket client;
    private static String chatServer = "192.168.1.190"; //"127.0.0.1"

    public Consumer(String username) {
        //System.out.print("Please enter your name : ");
        //Scanner scanner = new Scanner(System.in);
        //username = scanner.nextLine();
        this.username = username;
        //loadTopics();
    }

    public Consumer(ProfileName subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void run() {
    }

    void disconnect(String topic) {
        // unsubscribe
    }

    void register(String topic) {
        // subscribe
    }

    void showConversationData(String topic, Value val) {

    }

    // TODO CONNECT TO FIRST RANDOM BROKER AND BROKER SEND BROKERS LIST WITH TOPICS
    // AND SELECT THE RIGHT BROKER TO CONNECT
    public void start(String subject) throws UnknownHostException, IOException, InterruptedException {
/*
        System.out.println("--Topics--");
        for (Topic topic : topics) {
            System.out.println(topic.getChannelName());
        }
        System.out.print("Select a topic : ");
        Scanner myTopic = new Scanner(System.in);
        String subject = myTopic.nextLine();
        for (Topic topic : topics) {
            if (topic.getChannelName().equals(subject)) {
                client = new Socket(InetAddress.getByName(chatServer), 1234);
                input = new DataInputStream(client.getInputStream());
                output = new DataOutputStream(client.getOutputStream());
                pull(subject);
                break;
            }
        }*/
        System.out.println("In start");
        client = new Socket(InetAddress.getByName(chatServer), 1234); //start()
        input = new DataInputStream(client.getInputStream());
        output = new DataOutputStream(client.getOutputStream());
        System.out.println("Finish start");


    }

    public synchronized String pull(String subject) throws IOException, InterruptedException { //void
        // readMessage thread

        final String[] msg = new String[1];
        msg[0] = "\n";
        output.writeUTF("Subscriber");
        output.writeUTF(subject);
        output.writeUTF("subscriber");

        final CountDownLatch latch = new CountDownLatch(1);

        System.out.println("Pull");

        //TODO: PASS ALL STRINGS TO TOPICACTIVITY
        Thread readMessage = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("In thread");

                try {
                    output.writeUTF("BrokersList");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while (true) { //runs when refresh button is pressed
                    try {
                        int type = input.readInt();
                        if (type == 1){
                            //TODO:NOT WORKING CORRECTLY
                            int fileNameLength = input.readInt();

                            if (fileNameLength > 0) {
                                byte[] fileNameBytes = new byte[fileNameLength];
                                input.readFully(fileNameBytes, 0, fileNameBytes.length);
                                String fileName = new String(fileNameBytes);

                                int fileContentLength = input.readInt();

                                if (fileContentLength > 0) {
                                    byte[] fileContentBytes = new byte[fileContentLength];
                                    input.readFully(fileContentBytes, 0, fileContentLength);
                                    String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                            + File.separator + fileName;
                                    //---------------//
//                                    String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                                            + File.separator + "Awesome_Wp_Video";
//                                    File rootFile = new File(rootDir);
//                                    rootFile.mkdir();
                                    //-----------------//
                                    File directory = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
                                    if (!directory.exists())
                                        directory.mkdir();

                                    File fileToDownload = new File(rootDir);
                                    try {
                                        FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                                        fileOutputStream.write(fileContentBytes);
                                        fileOutputStream.close();
                                    } catch (IOException error) {

                                        error.printStackTrace();
                                        return;
                                    }
                                }
                            }
                        } else if (type==2) {
                            // read the message sent to this client
                            //String message = input.readUTF();
                            msg[0] = input.readUTF();
                            //msg[0] = msg[0] + "\n" + message;
                            latch.countDown();
                            System.out.println("----->" + msg[0]);
                            //return msg;
                        }
                    } catch (IOException e) {
                        System.out.println("----->Exception");
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
        readMessage.start();
        latch.await();
        return msg[0];
    }

}
