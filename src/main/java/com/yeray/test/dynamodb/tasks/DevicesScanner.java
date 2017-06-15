package com.yeray.test.dynamodb.tasks;

import com.codahale.metrics.Timer;
import com.yeray.test.dynamodb.repository.DynamoRepository;
import com.yeray.test.dynamodb.tools.MeteringTools;
import com.yeray.test.dynamodb.tools.RandomTools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class DevicesScanner implements Task {

    private DynamoRepository repository;

    private MeteringTools meteringTools;

    public DevicesScanner(DynamoRepository repository, MeteringTools meteringTools) {
        this.repository = repository;
        this.meteringTools = meteringTools;
    }

    public void launch(int scans, int threads) {
        List<String> macs = prepareRandomScans(scans);

        System.out.println("Executing task ...");

        try {
            new ForkJoinPool(threads).submit(() ->
                    macs.parallelStream()
                            .forEach(this::scanRandomDevice)
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        meteringTools.report();
    }

    private List<String> prepareRandomScans(int scans) {
        System.out.println("Preparing data set ...");

        List<String> macs = new ArrayList<>();
        IntStream.range(0, scans)
                .forEach(i -> macs.add(i, RandomTools.partialMacAddress()));

        return macs;
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
