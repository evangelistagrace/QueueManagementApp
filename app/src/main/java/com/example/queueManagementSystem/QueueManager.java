package com.example.queueManagementSystem;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class QueueManager {
    private int servingTicketId;
    private Queue<Ticket> tickets = new LinkedList<>();
    private long lastTicketNum = 0;
    Timer newTimer;
    Counter counter;
    Queue<Customer> queuedCustomers = new LinkedList<>();

    public QueueManager(Counter counter) {
        this.counter = counter;
    }

    public void generateTickets() {
        int randomQNum = this.getRandomQueueNumber();
        for (int i=0; i<randomQNum; i++) {
            Ticket ticket = generateTicket();
            this.tickets.add(ticket);
        }
    }

    public Ticket generateTicket() {
        int counterNum = this.counter.getId();
        long lastTicketNumber = getLastTicketNumber();
        Long ticketNum = Long.parseLong(String.valueOf(counterNum*10) + String.valueOf(lastTicketNumber));
        return new Ticket(111, counterNum, ticketNum); //ticket created with random time interval assigned
    }

    public int getRandomQueueNumber() {
        int min = 1;
        int max = 10;
        int randomQueueNumber = (int)(Math.random() * (max - min + 1) + min);
        System.out.println(randomQueueNumber);
        return randomQueueNumber;
    }

    public long getLastTicketNumber() {
        return ++lastTicketNum;
    }

    public Queue<Ticket> getTickets() {
        return this.tickets;
    }

    public void getNextTicket() {
        QueueManager queueManager = this;
        Ticket nextTicket = this.tickets.peek();
        long period = (long)(nextTicket.getTicketTimeInterval() * 1000);

        newTimer = new Timer();
        newTimer.schedule(new TicketHelper(queueManager), 0, period); //start ticket serving scheduler
    }

    class TicketHelper extends TimerTask {
        public QueueManager queueManager;
        public Queue<Ticket> tickets;
        Customer customer = null;

        public TicketHelper(QueueManager queueManager) {
            this.queueManager = queueManager;
            this.tickets = queueManager.getTickets();
        }

        public void run() {
            Ticket ticket = this.tickets.peek();
            assert ticket != null;
            long period = (long)(ticket.getTicketTimeInterval() * 1000);

            Timer oldTimer;
            if (!ticket.isExpired()) {

                //serve immediately
                for (Customer queuedCustomer: this.queueManager.queuedCustomers) {
                    if (queuedCustomer.getTicket().getTicketNumber() == ticket.getTicketNumber()) {
                        customer = queuedCustomer;
                    }
                }

                if (customer != null) {
                    System.out.println("Currently serving customer " + customer.getUsername() + " with " + ticket.getTicketNumber());
                } else {
                    System.out.println("Currently serving ticket" + ticket.getTicketNumber());
                }

                // DELAY THREAD TO SIMULATE RANDOM SERVING PERIOD //
                try {
                    Thread.sleep(period);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

                // CONTINUE THREAD EXECUTION //

                if (customer != null) {
                    System.out.println("Finished serving customer " + customer.getUsername() + " with " + ticket.getTicketNumber());
                    //remove customer from queue
                    customer.setInQueue(false);
                    customer.setTicket(null);
                    queuedCustomers.poll();
                } else {
                    System.out.println("Finished serving " + ticket.getTicketNumber());
                }
                ticket.setExpired(true);

            } else {
                oldTimer = newTimer; //assign previous timer as oldtimer so it can be cancelled and purge after new timer starts

                this.tickets.poll(); //remove served ticket

                if (this.tickets.peek() != null) { //check for remaining tickets
                    newTimer = new Timer(); //instantiate a new timer
                    newTimer.schedule(new TicketHelper(this.queueManager), 0, period);
                } else { //no more tickets left
                    System.out.println("~~ Finished serving all tickets for counter " + this.queueManager.counter.getId() + "~~");
                    this.queueManager.run(); //gen new batch of tickets
                }

                oldTimer.cancel();
                oldTimer.purge();
            }
        }
    }

    // process that keeps on running
    public void run() {
        this.generateTickets();
        System.out.println("~~Generated new batch of tickets for counter " + this.counter.getId() + " ~~");
        for (Ticket ticket : tickets) {
            System.out.println("Ticket number: " + ticket.getTicketNumber() + " with time interval: " + ticket.getRandomTimeInterval());
        }
        this.getNextTicket();
    }

    // handle ticket request from customer
    public void handleTicketRequest(Customer customer) {
        Ticket newTicket = this.generateTicket();
        System.out.println("QUEUED CUSTOMER " + customer.getUsername() + " WITH TICKET NUMBER " + newTicket.getTicketNumber());
        customer.setTicket(newTicket);
        customer.setInQueue(true);
        this.tickets.add(newTicket);
        queuedCustomers.add(customer);
    }

    public int getServingTicketId() {
        return servingTicketId;
    }

    public void setServingTicketId(int servingTicketId) {
        this.servingTicketId = servingTicketId;
    }

}
