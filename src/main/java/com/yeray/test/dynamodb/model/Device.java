package com.yeray.test.dynamodb.model;

import com.google.gson.Gson;

public class Device {

    private String deviceId;

    private Status status;

    public String asJson() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public Device fromJson(String json) {
        Gson gson = new Gson();

        return gson.fromJson(json, Device.class);
    }

    public enum Status {
        ONLINE, OFFLINE
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceId='" + deviceId + '\'' +
                ", status=" + status +
                '}';
    }
}
