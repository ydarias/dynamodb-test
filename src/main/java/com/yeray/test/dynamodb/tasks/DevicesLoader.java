package com.yeray.test.dynamodb.tasks;

import com.codahale.metrics.Timer;
import com.yeray.test.dynamodb.model.Device;
import com.yeray.test.dynamodb.repository.DynamoRepository;
import com.yeray.test.dynamodb.tools.MeteringTools;
import com.yeray.test.dynamodb.tools.RandomTools;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class DevicesLoader {

    private DynamoRepository repository;

    private MeteringTools meteringTools;

    public DevicesLoader(DynamoRepository repository, MeteringTools meteringTools) {
        this.repository = repository;
        this.meteringTools = meteringTools;
    }

    public void launchInserts(int inserts, int threads) {
        try {
            new ForkJoinPool(threads).submit(() ->
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
