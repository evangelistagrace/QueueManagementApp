package com.example.queueManagementSystem;

import java.io.Serializable;

public class Ticket implements Serializable {
    private int id;
//    private Counter counter;
    private long ticketNumber;
    private int randomTimeInterval;
    private boolean isExpired;
    Service service;
    Customer customer;
    Counter counter;

    public Ticket(long ticketNumber, Service service, Counter counter) {
        this.ticketNumber = ticketNumber;
        this.randomTimeInterval = this.getRandomTimeInterval();
        this.isExpired = false;
        this.service = service;
        this.counter = counter;
        this.customer = null;
    }

    public Ticket(long ticketNumber, Service service, Counter counter,  Customer customer) {
        this.ticketNumber = ticketNumber;
        this.randomTimeInterval = this.getRandomTimeInterval();
        this.isExpired = false;
        this.service = service;
        this.counter = counter;
        this.customer = customer;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public Service getService() {
        return service;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(long ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public int getRandomTimeInterval() {
        int min = 1;
        int max = 5;
        int randomTime = (int)(Math.random() * (max - min + 1) + min);
        return randomTime;
    }

    // get the random serving period assigned to the ticket
    public int getTicketTimeInterval() {
        return this.randomTimeInterval;
    }

    public void setExpired(boolean bool) {
        this.isExpired = bool;
    }

    public boolean isExpired() {
        return this.isExpired;
    }

    public Counter getCounter() {
        return counter;
    }
}

