package com.example.ds_project;

import java.util.ArrayList;
import java.util.HashMap;

public class ProfileName {
    String profileName;
    HashMap<String, ArrayList<Value>> userVideoFilesMap;
    HashMap<String, Integer> subscribedConversations;

    public ProfileName() {
    }

    public ProfileName(String profileName) {
        this.profileName = profileName;
        userVideoFilesMap = new HashMap<>();
        subscribedConversations = new HashMap<>();
    }

    public ProfileName(String profileName, HashMap<String, ArrayList<Value>> userVideoFilesMap,
            HashMap<String, Integer> subscribedConversations) {
        this.profileName = profileName;
        this.userVideoFilesMap = userVideoFilesMap;
        this.subscribedConversations = subscribedConversations;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public HashMap<String, ArrayList<Value>> getUserVideoFilesMap() {

        return userVideoFilesMap;
    }

    public void setUserVideoFilesMap(HashMap<String, ArrayList<Value>> userVideoFilesMap) {
        this.userVideoFilesMap = userVideoFilesMap;
    }

    public HashMap<String, Integer> getSubscribedConversations() {
        return subscribedConversations;
    }

    public void setSubscribedConversations(HashMap<String, Integer> subscribedConversations) {
        this.subscribedConversations = subscribedConversations;
    }
}
