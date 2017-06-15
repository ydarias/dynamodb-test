package com.yeray.test.dynamodb;

import com.yeray.test.dynamodb.repository.DynamoRepository;
import com.yeray.test.dynamodb.tasks.DevicesLoader;
import com.yeray.test.dynamodb.tasks.DevicesQuerier;
import com.yeray.test.dynamodb.tasks.DevicesScanner;
import com.yeray.test.dynamodb.tools.MeteringTools;

public class Launcher {

    public static void main(String ... args) {
        String operation;
        int operationsNumber;
        int threads;

        try {
            operation = args[0];
            operationsNumber = Integer.parseInt(args[1]);
            threads = Integer.parseInt(args[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Error invoking the program");
            System.out.println("$ launcher <operation> <number of operations> <threads>");

            return;
        }

        DynamoRepository repository = new DynamoRepository();
        MeteringTools meteringTools = new MeteringTools();

        if ("insert".equals(operation)) {
            DevicesLoader loader = new DevicesLoader(repository, meteringTools);
            loader.launchInserts(operationsNumber, threads);
        }
        if ("scan".equals(operation)) {
            DevicesScanner scanner = new DevicesScanner(repository, meteringTools);
            scanner.launchScans(operationsNumber, threads);
        }
        if ("query".equals(operation)) {
            DevicesQuerier querier = new DevicesQuerier(repository, meteringTools);
            querier.launchQueries(operationsNumber, threads);
        }
    }

}
