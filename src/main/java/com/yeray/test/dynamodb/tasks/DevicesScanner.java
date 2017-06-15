package com.yeray.test.dynamodb.tasks;

import com.codahale.metrics.Timer;
import com.yeray.test.dynamodb.repository.DynamoRepository;
import com.yeray.test.dynamodb.tools.MeteringTools;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class DevicesScanner {

    private DynamoRepository repository;

    private MeteringTools meteringTools;

    public DevicesScanner(DynamoRepository repository, MeteringTools meteringTools) {
        this.repository = repository;
        this.meteringTools = meteringTools;
    }

    public void launchScans(int inserts, int threads) {
        try {
            new ForkJoinPool(threads).submit(() ->
                    IntStream.range(0, inserts)
                            .parallel()
                            .forEach(i -> scanRandomDevice())
            ).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        meteringTools.report();
    }

    private void scanRandomDevice() {
        final Timer.Context context = meteringTools.getTimer("scans").time();
        try {
            repository.getDevicesStartingWith("AA-8D");
        } finally {
            context.stop();
        }
    }

}
