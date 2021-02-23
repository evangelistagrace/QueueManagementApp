package com.example.queueManagementSystem;

import java.util.ArrayList;

public class App {
    protected static ArrayList<Service> services = new ArrayList<>();
    public static void main(String[] args) {
        //here need to init customer and start the services from the db
        //the services will spawn objects for its own counters and so forth

        //e.g:
        // get list of services from db
        services.add(new Service(1, "Service 1"));
        services.add(new Service(2, "Service 2"));
        for (Service service: services) {
            service.startCounters();
        }

        Customer customer = new Customer("grace");
        Customer customer2 = new Customer("anna");
        customer.sendTicketRequest(customer, 1); //assume customer had clicked on a service with service id 1
        customer2.sendTicketRequest(customer2, 1);
    }

    public static ArrayList<Service> getServices() {
        return services;
    }
}
