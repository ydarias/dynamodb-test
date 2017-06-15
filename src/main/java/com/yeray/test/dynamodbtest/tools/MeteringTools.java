package com.yeray.test.dynamodbtest.tools;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class MeteringTools {

    private final MetricRegistry metrics = new MetricRegistry();

    private Map<String, Timer> timers = new HashMap<>();

    private ConsoleReporter reporter;

    public MeteringTools() {
        reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.MILLISECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        reporter.start(1, TimeUnit.SECONDS);
    }

    public Timer createTimer(String name) {
        Timer timer = metrics.timer(name);
        timers.put(name, timer);

        return timer;
    }

    public Timer getTimer(String name) {
        if (timers.containsKey(name))
            return timers.get(name);

        return createTimer(name);
    }

}
