package com.example.queueManagementSystem;

import java.util.ArrayList;

public class Customer {
    String username;
    boolean isInQueue;
    Ticket ticket;

    public Customer(String username) {
        this.username = username;
        this.isInQueue = false;
        this.ticket = null;
    }

    public void sendTicketRequest(Customer customer, int serviceId) {
        final ArrayList<Service> services = CustomerActivity.getServices();
        // instantiate service object
        // query if service exist in db
        // if service exists, get number of counters
        // assign customer to one counter randomly
        // get the assigned counter's id, and other details necessary for instantiating a counter object
        // call the counter's queuemanager's handleTicketRequest method
        // the handleTicketRequest method will accept parameters for
        for (Service service: services) {
            int randomIndex = 0; //hardcoded for now to take the first counter
            Counter counter;
            if (service.getServiceId() == serviceId) {
                counter = service.getCounter(randomIndex);
                counter.getQueueManager().handleTicketRequest(customer); // todo: work on handleTicketRequest method
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public boolean isInQueue() {
        return isInQueue;
    }

    public void setInQueue(boolean isInQueue) {
        this.isInQueue = isInQueue;
    }
}

