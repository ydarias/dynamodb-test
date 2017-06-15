package com.yeray.test.dynamodbtest;

import com.codahale.metrics.Timer;
import com.yeray.test.dynamodbtest.model.Device;
import com.yeray.test.dynamodbtest.repository.DynamoRepository;
import com.yeray.test.dynamodbtest.tools.MeteringTools;

import java.util.List;

public class LaunchDeviceQuery {

    public static void main(String ... args) {
        List<Device> devices;

        DynamoRepository repo = new DynamoRepository();
        MeteringTools metering = new MeteringTools();

        Timer.Context context = metering.getTimer("query").time();
        try {
            devices = repo.getDevicesStartingWith("AA-8B");
        } finally {
            context.stop();
        }

        devices.forEach(System.out::println);

        metering.report();
    }

}
