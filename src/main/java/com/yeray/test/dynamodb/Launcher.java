package com.yeray.test.dynamodb;

import com.yeray.test.dynamodb.model.Device;
import com.yeray.test.dynamodb.model.DeviceBuilder;
import com.yeray.test.dynamodb.repository.DynamoRepository;
import com.yeray.test.dynamodb.tasks.DevicesLoader;
import com.yeray.test.dynamodb.tasks.DevicesQuerier;
import com.yeray.test.dynamodb.tasks.DevicesScanner;
import com.yeray.test.dynamodb.tools.MeteringTools;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Launcher {

    private static DynamoRepository repository = new DynamoRepository();

    private static MeteringTools meteringTools = new MeteringTools();

    public static void main(String ... args) {
        int writeOperations;
        int readOperations;
        int threads;

        try {
            writeOperations = Integer.parseInt(args[0]);
            readOperations = Integer.parseInt(args[1]);
            threads = Integer.parseInt(args[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Error invoking the program");
            System.out.println("$ launcher <number of writes> <number of reads> <threads>");

            return;
        }

        System.out.println("Preparing data set ...");

        List<Device> devices = IntStream.range(0, writeOperations)
                .parallel()
                .mapToObj(i -> DeviceBuilder.randomDevice())
                .collect(Collectors.toList());

        new DevicesLoader(repository, meteringTools).launch(devices, threads);
        new DevicesScanner(repository, meteringTools).launch(devices, readOperations, threads);
        new DevicesQuerier(repository, meteringTools).launch(devices, readOperations, threads);
    }

}
