package com.yeray.test.dynamodbtest;

import com.yeray.test.dynamodbtest.model.Device;
import com.yeray.test.dynamodbtest.repository.DynamoRepository;

public class LaunchStoreDevice {

    public static void main(String ... args) {
        DynamoRepository repo = new DynamoRepository();
        Device device = new Device().fromJson("{\n" +
                "  \"deviceId\": \"11-22-33-44-55-66\",\n" +
                "  \"status\": \"ONLINE\"\n" +
                "}");

        repo.storeDevice(device);
    }

}
