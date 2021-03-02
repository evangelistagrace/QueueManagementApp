package com.example.queueManagementSystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Customer implements Serializable {
    int id;
    String username, password;
    ArrayList<Ticket> tickets;

    public Customer(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.tickets = new ArrayList<>();
    }

    public Ticket sendTicketRequest(Customer customer, int serviceId) {
        final ArrayList<Service> services = CustomerActivity.getServices();
        Ticket newTicket = null;

        for (Service service: services) {
            int randomIndex = 0; //hardcoded for now to take the first counter
            Counter counter;
            if (service.getServiceId() == serviceId) {
                counter = service.getCounter(randomIndex);
                newTicket = counter.getQueueManager().handleTicketRequest(customer);
            }
        }

        return newTicket;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword (String newPassword) {
        this.password = newPassword;
    }

    public int getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Ticket> getTickets() {
        return this.tickets;
    }

    public void addTicket (Ticket ticket) {
        this.tickets.add(ticket);
    }

    public void removeTicket (int index) {
        this.tickets.remove(index);
    }
}

