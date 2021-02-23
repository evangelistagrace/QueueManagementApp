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


    public void startCounters() {
        // query db for counetrs associated with the service id and add to counetrs list
        // but for now we're just setting some dummy counters based on the service name
        if (this.serviceName.equals("Service 1")) {
            this.counters.add(new Counter(11, "Counter 1", true, this));
        }

        if (this.serviceName.equals("Service 2")) {
            this.counters.add(new Counter(12, "Counter 2", true, this));
        }

        for (Counter counter: this.counters) {
            if (counter.open()) {
                counter.getQueueManager().run();
            }
        }
    }

    public int getServiceId() {
        return serviceId;
    }
}
