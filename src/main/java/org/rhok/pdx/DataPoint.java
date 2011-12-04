package org.rhok.pdx;

public class DataPoint {
    private Location location;
    private double intensity;
    private long timestamp;


    public DataPoint(Location location, double intensity, long timestamp) {
        this.location = location;
        this.intensity = intensity;
        this.timestamp = timestamp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
