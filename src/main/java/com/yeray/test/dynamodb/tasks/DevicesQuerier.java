package com.yeray.test.dynamodb.tasks;

import com.codahale.metrics.Timer;
import com.yeray.test.dynamodb.repository.DynamoRepository;
import com.yeray.test.dynamodb.tools.MeteringTools;

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
        try {
            new ForkJoinPool(threads).submit(() ->
                    IntStream.range(0, queries)
                            .parallel()
                            .forEach(i -> queryRandomDevice())
            ).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        meteringTools.report();
    }

    private void queryRandomDevice() {
        final Timer.Context context = meteringTools.getTimer("queries").time();
        try {
            repository.getDevice("44-FD-68-08-5A-EF");
        } finally {
            context.stop();
        }
    }

}
