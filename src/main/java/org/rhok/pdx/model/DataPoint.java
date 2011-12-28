package org.rhok.pdx.model;

public class DataPoint {
    private Location location;
    private int signal;
    private int wifi;
    private long timestamp;

    public DataPoint(){}

    public DataPoint(Location location, int signal, int wifi, long timestamp) {
        this.location = location;
        this.signal = signal;
        this.timestamp = timestamp;
        this.wifi = wifi;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getWifi() {
        return wifi;
    }

    public void setWifi(int wifi) {
        this.wifi = wifi;
    }
}
