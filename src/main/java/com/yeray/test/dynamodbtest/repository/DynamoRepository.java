package com.yeray.test.dynamodbtest.repository;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.yeray.test.dynamodbtest.model.Device;
import org.springframework.stereotype.Component;

@Component
public class DynamoRepository {

    private Table table;

    public DynamoRepository() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.EU_WEST_1)
                .build();

        DynamoDB dynamoDB  = new DynamoDB(client);

        table = dynamoDB.getTable("device-management");
    }

    public void storeDevice(Device device) {
        Item item = new Item()
                .withPrimaryKey("deviceId", device.getDeviceId())
                .with("status", device.getStatus().toString());

        table.putItem(item);
    }

}
