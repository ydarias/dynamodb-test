package com.yeray.test.dynamodb.tasks;

import com.codahale.metrics.Timer;
import com.yeray.test.dynamodb.model.Device;
import com.yeray.test.dynamodb.repository.DynamoRepository;
import com.yeray.test.dynamodb.tools.MeteringTools;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class DevicesScanner  {

    private DynamoRepository repository;

    private MeteringTools meteringTools;

    public DevicesScanner(DynamoRepository repository, MeteringTools meteringTools) {
        this.repository = repository;
        this.meteringTools = meteringTools;
    }

    public void launch(List<Device> devices, int scans, int threads) {
        System.out.println("Executing scans ...");

        try {
            new ForkJoinPool(threads).submit(() ->
                    IntStream.range(0, scans)
                        .parallel()
                        .forEach(i -> {
                            String mac = devices.get(i).getDeviceId().substring(0, 5);
                            scanRandomDevice(mac);
                        })
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        meteringTools.report();
    }

    private void scanRandomDevice(String mac) {
        final Timer.Context context = meteringTools.getTimer("scans").time();
        try {
            repository.getDevicesStartingWith(mac);
        } finally {
            context.stop();
        }
    }

}
