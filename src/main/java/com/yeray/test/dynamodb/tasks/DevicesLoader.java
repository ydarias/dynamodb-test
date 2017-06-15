package com.yeray.test.dynamodb.tasks;

import com.codahale.metrics.Timer;
import com.yeray.test.dynamodb.model.Device;
import com.yeray.test.dynamodb.repository.DynamoRepository;
import com.yeray.test.dynamodb.tools.MeteringTools;
import com.yeray.test.dynamodb.tools.RandomTools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class DevicesLoader implements Task {

    private DynamoRepository repository;

    private MeteringTools meteringTools;

    public DevicesLoader(DynamoRepository repository, MeteringTools meteringTools) {
        this.repository = repository;
        this.meteringTools = meteringTools;
    }

    public void launch(int inserts, int threads) {
        List<Device> devices = prepareRandomDevicesLoad(inserts);

        System.out.println("Executing task ...");

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

    private List<Device> prepareRandomDevicesLoad(int inserts) {
        System.out.println("Preparing data set ...");

        List<Device> devices = new ArrayList<>();

        IntStream.range(0, inserts).forEach(i -> devices.add(i, createRandomDevice()));

        return devices;
    }

    private Device createRandomDevice() {
        Device device = new Device();
        device.setDeviceId(RandomTools.macAddress());
        device.setStatus(RandomTools.deviceStatus());

        return device;
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
