package com.yeray.test.dynamodb;

import com.yeray.test.dynamodb.repository.DynamoRepository;
import com.yeray.test.dynamodb.tasks.DevicesLoader;
import com.yeray.test.dynamodb.tasks.DevicesQuerier;
import com.yeray.test.dynamodb.tasks.DevicesScanner;
import com.yeray.test.dynamodb.tasks.Task;
import com.yeray.test.dynamodb.tools.MeteringTools;

import java.util.HashMap;
import java.util.Map;

public class Launcher {

    private static DynamoRepository repository = new DynamoRepository();

    private static MeteringTools meteringTools = new MeteringTools();

    private static final Map<String, Task> tasks = new HashMap<>();

    static {
        tasks.put("insert", new DevicesLoader(repository, meteringTools));
        tasks.put("scan", new DevicesScanner(repository, meteringTools));
        tasks.put("query", new DevicesQuerier(repository, meteringTools));
    }

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

        tasks.get(operation).launch(operationsNumber, threads);
    }

}
