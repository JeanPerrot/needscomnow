package org.rhok.pdx.model;

public class RequestParams {
    public RequestParams(double latitude, double longitude, double range, long timestamp, int maxCount) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.range = range;
        this.timestamp = timestamp;
        this.maxCount = maxCount;
    }

    public final double latitude;
    public final double longitude;
    public final double range;
    public final long timestamp;
    public final int maxCount;

}
