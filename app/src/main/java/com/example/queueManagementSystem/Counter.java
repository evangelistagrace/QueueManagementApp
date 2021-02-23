package com.example.queueManagementSystem;

public class Counter {
    private int id;
    private String counterName;
    private int serviceId;
    private QueueManager queueManager;
    boolean isOpen;

    public Counter(int id, String counterName, boolean isOpen, Service service) {
        this.id = id;
        this.counterName = counterName;
        this.serviceId = service.serviceId;
        this.queueManager = new QueueManager(this);
        this.isOpen = isOpen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public QueueManager getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    public boolean open() {
        return this.isOpen;
    }
}
