package com.kenkode.altruistic;

public class Model {
    private String arrival_time, departure_time, waiting_time;
    private int vacant_seats;

    public Model() {
    }

    public Model(String arrival_time, String departure_time, String waiting_time, int vacant_seats) {
        this.arrival_time = arrival_time;
        this.departure_time = departure_time;
        this.waiting_time = waiting_time;
        this.vacant_seats = vacant_seats;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public void setArrival_time(String arrival_time) {
        this.arrival_time = arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public void setDeparture_time(String departure_time) {
        this.departure_time = departure_time;
    }

    public String getWaiting_time() {
        return waiting_time;
    }

    public void setWaiting_time(String waiting_time) {
        this.waiting_time = waiting_time;
    }

    public int getVacant_seats() {
        return vacant_seats;
    }

    public void setVacant_seats(int vacant_seats) {
        this.vacant_seats = vacant_seats;
    }
}
