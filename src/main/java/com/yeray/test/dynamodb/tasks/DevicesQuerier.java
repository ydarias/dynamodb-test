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

public class DevicesQuerier implements Task {

    private DynamoRepository repository;

    private MeteringTools meteringTools;

    public DevicesQuerier(DynamoRepository repository, MeteringTools meteringTools) {
        this.repository = repository;
        this.meteringTools = meteringTools;
    }

    public void launch(int queries, int threads) {
        List<String> macs = prepareRandomScans(queries);

        System.out.println("Executing task ...");

        try {
            new ForkJoinPool(threads).submit(() ->
                    macs.parallelStream()
                            .forEach(this::queryRandomDevice)
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        meteringTools.report();
    }

    private List<String> prepareRandomScans(int queries) {
        System.out.println("Preparing data set ...");

        List<String> macs = new ArrayList<>();
        IntStream.range(0, queries)
                .forEach(i -> macs.add(i, RandomTools.macAddress()));

        return macs;
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
