package com.yeray.test.dynamodbtest;

import com.codahale.metrics.Timer;
import com.yeray.test.dynamodbtest.model.Device;
import com.yeray.test.dynamodbtest.repository.DynamoRepository;
import com.yeray.test.dynamodbtest.tools.MeteringTools;
import com.yeray.test.dynamodbtest.tools.RandomTools;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

@Component
public class DevicesLoader implements InitializingBean {

    @Value("${loader.inserts}")
    private int inserts;

    @Autowired
    private DynamoRepository repository;

    @Autowired
    private MeteringTools meteringTools;

    @Override
    public void afterPropertiesSet() throws Exception {
        launchInserts();
    }

    private void launchInserts() {
        try {
            new ForkJoinPool(10).submit(() ->
                    IntStream.range(0, inserts)
                            .parallel()
                            .forEach(i -> insertRandomDevice())
            ).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        meteringTools.report();
    }

    private void insertRandomDevice() {
        Device device = new Device();
        device.setDeviceId(RandomTools.macAddress());
        device.setStatus(RandomTools.deviceStatus());

        final Timer.Context context = meteringTools.getTimer("inserts").time();
        try {
            repository.storeDevice(device);
        } finally {
            context.stop();
        }
    }

}
