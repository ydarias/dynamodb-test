package com.yeray.test.dynamodbtest;

import com.yeray.test.dynamodbtest.model.Device;
import com.yeray.test.dynamodbtest.repository.DynamoRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Component
public class DevicesLoader implements InitializingBean {

    @Value("${loader.inserts}")
    private int inserts;

    @Autowired
    private DynamoRepository repository;

    @Override
    public void afterPropertiesSet() throws Exception {
        launchInserts();
    }

    private void launchInserts() {
        IntStream.range(0, inserts)
                .forEach(i -> insertRandomDevice());
    }

    private void insertRandomDevice() {
        Device device = new Device();
        device.setDeviceId(RandomTools.macAddress());
        device.setStatus(RandomTools.deviceStatus());

        repository.storeDevice(device);
    }

}
