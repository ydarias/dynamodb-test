package com.yeray.test.dynamodb.tasks;

import com.codahale.metrics.Timer;
import com.yeray.test.dynamodb.model.Device;
import com.yeray.test.dynamodb.repository.DynamoRepository;
import com.yeray.test.dynamodb.tools.MeteringTools;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class DevicesQuerier {

    private DynamoRepository repository;

    private MeteringTools meteringTools;

    public DevicesQuerier(DynamoRepository repository, MeteringTools meteringTools) {
        this.repository = repository;
        this.meteringTools = meteringTools;
    }

    public void launch(List<Device> devices, int queries, int threads) {
        System.out.println("Executing queries ...");

        try {
            new ForkJoinPool(threads).submit(() ->
                    IntStream.range(0, queries)
                        .parallel()
                        .forEach(i -> {
                            String mac = devices.get(i).getDeviceId();
                            queryRandomDevice(mac);
                        })
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        meteringTools.report();
    }

    private void queryRandomDevice(String mac) {
        final Timer.Context context = meteringTools.getTimer("queries").time();
        try {
            repository.getDevice(mac);
        } finally {
            context.stop();
        }
    }

}
