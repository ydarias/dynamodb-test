package com.yeray.test.dynamodb.model;

import com.google.gson.Gson;

public class Device {

    private String deviceId;

    private Status status;

    private Type type;

    private Manufacturer manufacturer;

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

    public enum Type {
        STATION, ACCESS_POINT
    }

    public enum Manufacturer {
        MEDIATEK, QUALCOMM
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }
}
