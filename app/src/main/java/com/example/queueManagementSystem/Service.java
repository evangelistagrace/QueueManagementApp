package com.example.queueManagementSystem;

import java.util.ArrayList;

public class Service {
    int serviceId;
    String serviceName;
    ArrayList<Counter> counters;

    public Service(int serviceId, String serviceName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.counters = new ArrayList<>();
    }

    public Counter getCounter(int index) {
        return this.counters.get(index);
    }

    public void addCounter(Counter counter) {
        this.counters.add(counter);
    }

    public ArrayList<Counter> getCounters() {
        return this.counters;
    }

    public int getServiceId() {
        return serviceId;
    }
}
