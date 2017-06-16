package com.yeray.test.dynamodb.tasks;

import com.codahale.metrics.Timer;
import com.yeray.test.dynamodb.model.Device;
import com.yeray.test.dynamodb.repository.DynamoRepository;
import com.yeray.test.dynamodb.tools.MeteringTools;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class DevicesLoader {

    private DynamoRepository repository;

    private MeteringTools meteringTools;

    public DevicesLoader(DynamoRepository repository, MeteringTools meteringTools) {
        this.repository = repository;
        this.meteringTools = meteringTools;
    }

    public void launch(List<Device> devices, int threads) {
        System.out.println("Executing inserts ...");

        try {
            new ForkJoinPool(threads).submit(() ->
                    devices.parallelStream()
                            .forEach(this::insertRandomDevice)
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        meteringTools.report();
    }

    private void insertRandomDevice(Device device) {
        final Timer.Context context = meteringTools.getTimer("inserts").time();
        try {
            repository.storeDevice(device);
        } finally {
            context.stop();
        }
    }

}
