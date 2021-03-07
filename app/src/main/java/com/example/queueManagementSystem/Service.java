package com.example.queueManagementSystem;

import java.io.Serializable;
import java.util.ArrayList;

public class Service implements Serializable {
    int serviceId, isRunning;
    String serviceName;
    ArrayList<Counter> counters;
    boolean countersStarted;

    public Service(int serviceId, String serviceName, int isRunning) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.isRunning = isRunning;
        this.counters = new ArrayList<>();
        this.countersStarted = false;
    }

    public int isRunning() {
        return isRunning;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void addCounter(Counter counter) {
        this.counters.add(counter);
    }

    public Counter getCounter(int index) {
        return this.counters.get(index);
    }

    public ArrayList<Counter> getCounters() {
        return this.counters;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setCountersStarted(boolean bool) {
        this.countersStarted = bool;
    }

    public boolean getCountersStarted() {
        return this.countersStarted;
    }
}
