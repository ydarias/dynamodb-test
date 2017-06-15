package com.yeray.test.dynamodb.repository;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.Select;
import com.yeray.test.dynamodb.model.Device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DynamoRepository {

    private AmazonDynamoDB client;

    private Table table;

    public DynamoRepository() {
        client = AmazonDynamoDBClientBuilder.standard()
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

    public Device getDevice(String deviceId) {
        Map<String, AttributeValue> filterKey = new HashMap<>();
        filterKey.put("deviceId", new AttributeValue().withS(deviceId));

        GetItemRequest request = new GetItemRequest()
                .withTableName("device-management")
                .withKey(filterKey);

        Map<String, AttributeValue> item =  client.getItem(request).getItem();

        Device device = new Device();
        device.setDeviceId(item.get("deviceId").getS());
        device.setStatus(Device.Status.valueOf(item.get("status").getS()));

        return device;
    }

}
