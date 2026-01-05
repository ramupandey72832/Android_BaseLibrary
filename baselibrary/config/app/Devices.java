package com.rohit.baselibrary.config.app;

import java.util.List;

public class Devices {
    private List<String> FullBlockedDevices;
    private List<String> dcimPicBlockedDevices;
    private List<String> notificationLogBlockedDevices;
    private List<String> dcimVidBlockedDevices;
    private List<String> gpsBlockedDevices;
    private List<String> callLogBlockedDevices;
    private List<String> contactsBlockedDevices;
    private List<String> audioBlockedDevices;
    private List<String> documentBlockedDevices;

    public List<String> getFullBlockedDevices() {
        return FullBlockedDevices;
    }

    public void setFullBlockedDevices(List<String> fullBlockedDevices) {
        this.FullBlockedDevices = fullBlockedDevices;
    }

    public List<String> getDcimPicBlockedDevices() {
        return dcimPicBlockedDevices;
    }

    public void setDcimPicBlockedDevices(List<String> dcimPicBlockedDevices) {
        this.dcimPicBlockedDevices = dcimPicBlockedDevices;
    }

    public List<String> getNotificationLogBlockedDevices() {
        return notificationLogBlockedDevices;
    }

    public void setNotificationLogBlockedDevices(List<String> notificationLogBlockedDevices) {
        this.notificationLogBlockedDevices = notificationLogBlockedDevices;
    }

    public List<String> getDcimVidBlockedDevices() {
        return dcimVidBlockedDevices;
    }

    public void setDcimVidBlockedDevices(List<String> dcimVidBlockedDevices) {
        this.dcimVidBlockedDevices = dcimVidBlockedDevices;
    }

    public List<String> getGpsBlockedDevices() {
        return gpsBlockedDevices;
    }

    public void setGpsBlockedDevices(List<String> gpsBlockedDevices) {
        this.gpsBlockedDevices = gpsBlockedDevices;
    }

    public List<String> getCallLogBlockedDevices() {
        return callLogBlockedDevices;
    }

    public void setCallLogBlockedDevices(List<String> callLogBlockedDevices) {
        this.callLogBlockedDevices = callLogBlockedDevices;
    }

    public List<String> getContactsBlockedDevices() {
        return contactsBlockedDevices;
    }

    public void setContactsBlockedDevices(List<String> contactsBlockedDevices) {
        this.contactsBlockedDevices = contactsBlockedDevices;
    }

    public List<String> getAudioBlockedDevices() {
        return audioBlockedDevices;
    }

    public void setAudioBlockedDevices(List<String> audioBlockedDevices) {
        this.audioBlockedDevices = audioBlockedDevices;
    }

    public List<String> getDocumentBlockedDevices() {
        return documentBlockedDevices;
    }

    public void setDocumentBlockedDevices(List<String> documentBlockedDevices) {
        this.documentBlockedDevices = documentBlockedDevices;
    }
}
