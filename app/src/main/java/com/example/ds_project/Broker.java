package com.example.ds_project;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
/*
import models.Topic;
 */

public class Broker extends Node {

    ServerSocket serverSocket;
    String name, ip;
    int port;
    Socket client;
    int num;
    String brokerName;
    int brokerIndex;
    int hashValue;
    public HashMap<Topic, Queue<String>> topicsQueue = new HashMap<>();
    List<Topic> linkedTopics = new ArrayList<Topic>(); // hashmap == queue
    private static File currDirectory = new File(new File("").getAbsolutePath());
    boolean isChanged = false;
    private static List<Topic> allTopics;
    static ArrayList<Integer> allBrokHash;

    public Broker(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    // TODO NEED REFACTOR TO ADD LINKEDTOPICS TO THIS BROKER
    public void calculateKeys() throws NoSuchAlgorithmException {
        loadTopics();
        loadBrokers();
        // calculate each of the Brokers hash value
        BrokerMD5HashCode();
        // calculate TopicsHash
        HashMap<String, Integer> topicHashes = calculateTopicHash();
        List<Topic> copyTopics = new ArrayList<>();
        for (Topic t : topics) {
            copyTopics.add(t);
        }
        // compare topic hashes and broker hash value
        if (!topicHashes.isEmpty()) {
            ArrayList<Integer> allBrokHash = allBrokerHash();
            if (allBrokHash != null) {
                // iterate the list of the values of the brokers' hash
                for (int i = 0; i < allBrokHash.size(); i++) {
                    for (Topic t : topics) {     //check that specific element exists
                        if (copyTopics.indexOf(t) > -1) {
                            int h = TopicMD5HashCode(t);

                            if (i == 0) {
                                if ((h < allBrokHash.get(i)) || (h >= allBrokHash.get(allBrokHash.size()-1)) ){
                                    for (Broker b : brokers) {
                                        if (b.hashValue == allBrokHash.get(i)) {
                                            b.linkedTopics.add(t);
                                            int index = copyTopics.indexOf(t);
                                            copyTopics.remove(index);
                                        }
                                    }
                                }
                            } else if (h < allBrokHash.get(i) && h >= allBrokHash.get(i - 1)) {
                                for (Broker b : brokers) {
                                    if (b.hashValue == allBrokHash.get(i)) {
                                        b.linkedTopics.add(t);
                                        int index = copyTopics.indexOf(t);
                                        copyTopics.remove(index);
                                    }
                                }
                            } else{
                                continue;
                            }

                        }
                    }
                }
            }
        }
        // Check which broker of brokersList is this object and add to this linkedTopics
        int i = 0;
        for (Broker br : brokers) {
            if (br.equals(this)) {
                this.brokerIndex = i;
                this.linkedTopics = br.linkedTopics;
            }
            i++;
        }

    }

    public void start() throws NoSuchAlgorithmException, IOException {
        calculateKeys();
        // server is listening on port 1234
        for (Broker br : brokers) {
            System.out.println(br.name);
            for (Topic c : br.linkedTopics) {
                System.out.println("#" + c.getChannelName());
            }
            System.out.println("---------------------");
        }
        serverSocket = new ServerSocket(port);
        System.out.println(name + " start and listening on port " + port);
        // running infinite loop for getting
        // client request
        while (!serverSocket.isClosed())

        {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New client request received : " + socket);
                // obtain input and output streams
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                // Listen for input from connection
                // Check for client disconnection

                String username = dis.readUTF();

                String subject = dis.readUTF();
                System.out.println("connected to subject: " + subject);
                String client = dis.readUTF();
                if (client.equals("publisher")) {
                    // Create a new handler object for handling this request.
                    PublisherHandler mtch = new PublisherHandler(socket, username, subject, dis, dos, this);
                    // Create a new Thread with this object.
                    Thread t = new Thread(mtch);

                    // start the thread.
                    t.start();
                } else if (client.equals("subscriber")) {
                    // Create a new handler object for handling this request.
                    ConsumerHandler mtch = new ConsumerHandler(socket, username, subject, dis, dos, this);
                    // Create a new Thread with this object.
                    Thread t = new Thread(mtch);

                    // start the thread.
                    t.start();
                }

            } catch (IOException e) {
                System.err.println("Error processing client connection");
                throw e;
            }
        }
    }

    // PublisherHandler class
    class PublisherHandler implements Runnable {
        Scanner scn = new Scanner(System.in);
        private String name;
        final DataInputStream dis;
        final DataOutputStream dos;
        Socket s;
        boolean isloggedin;
        boolean hasBrokerThisTopic;
        Topic topic;

        // constructor
        public PublisherHandler(Socket s, String name, String subject,
                                DataInputStream dis, DataOutputStream dos, Broker thisBroker) {
            this.dis = dis;
            this.dos = dos;
            this.name = name;
            this.s = s;
            this.isloggedin = true;
            this.hasBrokerThisTopic = false;
            for (Topic c : thisBroker.linkedTopics) {
                if (c.getChannelName().equals(subject)) {
                    this.topic = c;
                    hasBrokerThisTopic = true;
                    break;
                }
            }

        }

        @Override
        public void run() {

            String received;
            while (true) {
                try {
                    String type = dis.readUTF();

                    if (!hasBrokerThisTopic) {
                        System.out.println("This topic is not available for this Broker");
                        this.s.close();
                        break;
                    }
                    Broker broker = brokers.get(brokerIndex);
                    if (type.equals("1")) {

                        int fileNameLength = dis.readInt();

                        if (fileNameLength > 0) {
                            byte[] fileNameBytes = new byte[fileNameLength];
                            dis.readFully(fileNameBytes, 0, fileNameBytes.length);
                            String fileName = new String(fileNameBytes);

                            int fileContentLength = dis.readInt();

                            if (fileContentLength > 0) {
                                byte[] fileContentBytes = new byte[fileContentLength];
                                dis.readFully(fileContentBytes, 0, fileContentLength);
                                File directory = new File(currDirectory + "\\data\\downloads");
                                if (!directory.exists())
                                    directory.mkdir();

                                File fileToDownload = new File(currDirectory + "\\data\\downloads\\" + fileName);
                                try {
                                    FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                                    fileOutputStream.write(fileContentBytes);
                                    fileOutputStream.close();
                                } catch (IOException error) {

                                    error.printStackTrace();
                                    return;
                                }
                            }
                            if (broker.topicsQueue.get(this.topic) == null) {
                                broker.topicsQueue.put(this.topic, new LinkedList<String>());
                            }
                            broker.topicsQueue.get(this.topic)
                                    .add(this.name + " : File Upload Successful");
                            isChanged = true;
                        }

                    } else if (type.equals("2")) {

                        // receive the string
                        received = dis.readUTF();

                        System.out.println(received);

                        if (received.equals("logout")) {
                            this.isloggedin = false;
                            this.s.close();
                            break;
                        }

                        // break the string into message and recipient part
                        StringTokenizer st = new StringTokenizer(received, "#");
                        String recipient = st.nextToken();
                        String MsgToSend = st.nextToken();

                        if (broker.topicsQueue.get(this.topic) == null) {
                            broker.topicsQueue.put(this.topic, new LinkedList<String>());
                        }
                        broker.topicsQueue.get(this.topic).add(this.name + " : " + MsgToSend);
                        isChanged = true;
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                    return;
                }

            }
            // try {
            // // closing resources
            // this.dis.close();
            // this.dos.close();

            // } catch (IOException e) {
            // e.printStackTrace();
            // }

        }
    }

    // PublisherHandler class
    class ConsumerHandler implements Runnable {
        Scanner scn = new Scanner(System.in);
        private String name;
        final DataInputStream dis;
        final DataOutputStream dos;
        Socket s;
        boolean isloggedin;
        boolean hasBrokerThisTopic;
        Topic topic;
        Queue<String> messagesQueue;

        // constructor
        public ConsumerHandler(Socket s, String name, String subject,
                               DataInputStream dis, DataOutputStream dos, Broker thisBroker) {
            this.dis = dis;
            this.dos = dos;
            this.name = name;
            this.s = s;
            this.isloggedin = true;
            this.hasBrokerThisTopic = false;
            for (Topic c : thisBroker.linkedTopics) {
                if (c.getChannelName().equals(subject)) {
                    this.topic = c;
                    hasBrokerThisTopic = true;
                    break;
                }
            }

        }

        @Override
        public void run() {
            try {
                String action = dis.readUTF();
                if (action.equals("BrokersList")) {
                    for (Broker br : brokers) {
                        dos.writeUTF(br.name);
                        for (Topic c : br.linkedTopics) {
                            dos.writeUTF("#" + c.getChannelName());
                        }
                        dos.writeUTF("---------------------");
                    }
                }
                if (!hasBrokerThisTopic) {
                    System.out.println("This topic is not available for this Broker");
                    this.s.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            int index = 0;
            Queue<String> topicsMessages = brokers.get(brokerIndex).topicsQueue.get(this.topic);
            int initCount = 0;
            if (topicsMessages != null) {
                initCount = topicsMessages.size();
                for (String tM : topicsMessages) {

                    try {
                        index++;
                        this.dos.writeUTF(tM);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
            while (true) {
                // RUN CONTINIOUSLY UNTIL IDENTIFY CHANGE IN TOPICS QUEUE
                if (isChanged) {
                    topicsMessages = brokers.get(brokerIndex).topicsQueue.get(this.topic);
                    try {
                        if (topicsMessages.size() == initCount) {
                        } else {
                            String message = topicsMessages.toArray()[index].toString();
                            this.dos.writeUTF(message);
                            index += 1;
                            initCount = topicsMessages.size();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }

                }
            }

            // }

        }
    }

    // Overriding equals() to compare two Complex objects
    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /*
         * Check if o is an instance of Complex or not
         * "null instanceof [type]" also returns false
         */
        if (!(o instanceof Broker)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Broker b = (Broker) o;

        return port == b.port && ip.equals(b.ip) && name.equals(b.name);
    }

    public int BrokerMD5HashCode() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String ipPlusPort = this.ip + this.port;
            md.update(ipPlusPort.getBytes(), 0, ipPlusPort.length());
            BigInteger no = new BigInteger(1, md.digest());
            int newno = no.intValue();
            newno = newno%300 * (-1);
            this.hashValue = newno;
            return newno;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int TopicMD5HashCode(Topic c){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String chName = c.getChannelName();
            md.update(chName.getBytes(), 0, chName.length());
            BigInteger no = new BigInteger(1, md.digest());
            int newno = no.intValue();
            newno = newno%300 * (-1);
            return newno;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private ArrayList<Integer> allBrokerHash() {
        ArrayList<Integer> allBrokHash = new ArrayList<>();
        for (Broker b : brokers) {
            int hashV = b.BrokerMD5HashCode();
            allBrokHash.add(hashV);
        } // sort the hash to be in order
        Collections.sort(allBrokHash);
        return allBrokHash;
    }

    // get all the topics and calculate a hash value for each topic and put them
    // inside topicHashes list and return
    private HashMap<String, Integer> calculateTopicHash() throws NoSuchAlgorithmException {

        //List allTopics = loadTopics();
        HashMap<String, Integer> topicHashes = new HashMap<>();
        for (Topic t : topics) {
            int h = TopicMD5HashCode(t);
            // put inside topicHashes queue the name and corresponding hash value of each
            // topic
            topicHashes.put(t.getChannelName(), h);
        }
        return topicHashes;
    }




}