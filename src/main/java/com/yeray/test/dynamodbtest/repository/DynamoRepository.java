package com.yeray.test.dynamodbtest.repository;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.yeray.test.dynamodbtest.model.Device;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DynamoRepository {

    private AmazonDynamoDB client;

    private Table table;

    private DynamoDBMapper mapper;

    public DynamoRepository() {
        client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.EU_WEST_1)
                .build();

        DynamoDB dynamoDB  = new DynamoDB(client);

        table = dynamoDB.getTable("device-management");
        mapper = new DynamoDBMapper(client);
    }

    public void storeDevice(Device device) {
        Item item = new Item()
                .withPrimaryKey("deviceId", device.getDeviceId())
                .with("status", device.getStatus().toString());

        table.putItem(item);
    }

    public int countOnlineDevices() {
        Condition onlineCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue().withS("ONLINE"));

        Map<String, Condition> filter = new HashMap<>();
        filter.put("status", onlineCondition);

        ScanRequest request = new ScanRequest()
                .withTableName("device-management")
                .withScanFilter(filter)
                .withSelect(Select.COUNT);

        return client.scan(request).getCount();
    }

    public List<Device> getDevicesStartingWith(String macPrefix) {
        Condition similarDeviceIdCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.CONTAINS)
                .withAttributeValueList(new AttributeValue().withS(macPrefix));

        Map<String, Condition> filter = new HashMap<>();
        filter.put("deviceId", similarDeviceIdCondition);

        ScanRequest request = new ScanRequest()
                .withTableName("device-management")
                .withScanFilter(filter);

        return client.scan(request).getItems().stream()
                .map(e -> {
                    Device device = new Device();
                    device.setDeviceId(e.get("deviceId").getS());
                    device.setStatus(Device.Status.valueOf(e.get("status").getS()));

                    return device;
                }).collect(Collectors.toList());
    }

}
