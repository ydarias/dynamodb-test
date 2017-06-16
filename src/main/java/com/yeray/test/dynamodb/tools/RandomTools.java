package com.yeray.test.dynamodb.tools;

import com.yeray.test.dynamodb.model.Device;

import java.util.Random;

public class RandomTools {

    private static Random rand = new Random();

    public static String macAddress() {
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
        return (rand.nextInt() % 2 == 0) ? Device.Status.OFFLINE : Device.Status.ONLINE;
    }

    public static Device.Type deviceType() {
        return (rand.nextInt() % 2 == 0) ? Device.Type.ACCESS_POINT : Device.Type.STATION;
    }

    public static Device.Manufacturer deviceManufacturer() {
        return (rand.nextInt() % 2 == 0) ? Device.Manufacturer.MEDIATEK : Device.Manufacturer.QUALCOMM;
    }

}
