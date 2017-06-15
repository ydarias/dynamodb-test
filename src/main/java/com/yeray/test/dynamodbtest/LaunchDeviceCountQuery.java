package com.yeray.test.dynamodbtest;

import com.codahale.metrics.Timer;
import com.yeray.test.dynamodbtest.repository.DynamoRepository;
import com.yeray.test.dynamodbtest.tools.MeteringTools;

public class LaunchDeviceCountQuery {

    public static void main(String ... args) {
        int result;

        DynamoRepository repo = new DynamoRepository();
        MeteringTools metering = new MeteringTools();

        Timer.Context context = metering.getTimer("query").time();
        try {
            result = repo.countOnlineDevices();
        } finally {
            context.stop();
        }

        metering.report();

        System.out.println("Result > " + result);
    }

}
