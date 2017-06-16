package com.yeray.test.dynamodb.model;

import com.yeray.test.dynamodb.tools.RandomTools;

public class DeviceBuilder {

    public static Device randomDevice() {
        Device device = new Device();
        device.setDeviceId(RandomTools.macAddress());
        device.setStatus(RandomTools.deviceStatus());
        device.setType(RandomTools.deviceType());
        device.setManufacturer(RandomTools.deviceManufacturer());

        return device;
    }

}
