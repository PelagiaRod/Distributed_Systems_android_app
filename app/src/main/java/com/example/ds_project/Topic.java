package com.example.ds_project;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Topic implements Serializable {
    private String channelName;

    public Topic(String name) {
        this.channelName = name;
    }

    public Topic() {
    }

    public void setchannelName(String n) {
        this.channelName = n;
    }

    public String getChannelName() {
        return this.channelName;
    }

    @Override
    public String toString() {
        String name = "The name of this channel is " + this.channelName;
        return name;
    }

    @Override
    public int hashCode() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(this.channelName.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            return no.intValue();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
