package org.mizhou.landd;

import java.util.ArrayList;
import java.util.List;

public class Room {

    private final String name;
    private final List<Device> devices;

    public Room(String name) {
        this.name = name;
        this.devices = new ArrayList<>();
    }

    public boolean addDevice(Device device) {
        return devices.add(device);
    }

    public String getName() {
        return name;
    }

    public void listDevices() {
        System.out.printf("Room (%s), current devices:\n", name);
        for (Device device : devices) {
            System.out.println(device);
        }
    }
}
