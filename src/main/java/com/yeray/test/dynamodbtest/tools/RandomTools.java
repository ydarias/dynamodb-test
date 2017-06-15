package com.yeray.test.dynamodbtest.tools;

import com.yeray.test.dynamodbtest.model.Device;

import java.util.Random;

public class RandomTools {

    public static String macAddress() {
        Random rand = new Random();
        byte[] macAddr = new byte[6];
        rand.nextBytes(macAddr);

        macAddr[0] = (byte) (macAddr[0] & (byte) 254);  //zeroing last 2 bytes to make it unicast and locally adminstrated

        StringBuilder sb = new StringBuilder(18);
        for (byte b : macAddr) {

            if (sb.length() > 0)
                sb.append("-");

            sb.append(String.format("%02x", b));
        }

        return sb.toString().toUpperCase();
    }

    public static Device.Status deviceStatus() {
        Random rand = new Random();

        return (rand.nextInt() % 2 == 0) ? Device.Status.OFFLINE :  Device.Status.ONLINE;
    }

}
