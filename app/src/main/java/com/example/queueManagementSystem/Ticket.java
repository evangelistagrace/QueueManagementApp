package com.example.queueManagementSystem;

public class Ticket {
    private int id;
    private int counterId;
    private long ticketNumber;
    private int randomTimeInterval;
    private boolean isExpired;

    public Ticket(int id, int counterId, long ticketNumber) {
        this.id = id;
        this.counterId = counterId;
        this.ticketNumber = ticketNumber;
        this.randomTimeInterval = this.getRandomTimeInterval();
        this.isExpired = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCounterId() {
        return counterId;
    }

    public void setCounterId(int counterId) {
        this.counterId = counterId;
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
}

