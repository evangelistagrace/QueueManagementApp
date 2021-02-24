package com.example.queueManagementSystem;

public class Counter {
    private int id;
    private String counterName;
    private QueueManager queueManager;
    boolean isOpen;
    Service service;

    public Counter(int id, String counterName, boolean isOpen, Service service) {
        this.id = id;
        this.counterName = counterName;
        this.service = service;
        this.queueManager = new QueueManager(this, service);
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
