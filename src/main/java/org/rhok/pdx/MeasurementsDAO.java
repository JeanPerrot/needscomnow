package org.rhok.pdx;

public interface MeasurementsDAO {
    Measurements getMeasurements(Location location, double range, long timestamp, int maxCount);
    void saveMeasurements(Measurements ms);

}
