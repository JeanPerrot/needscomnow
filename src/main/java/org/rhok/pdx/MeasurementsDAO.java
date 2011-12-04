package org.rhok.pdx;

public interface MeasurementsDAO {
    Measurements getMeasurements(Location l, double r, int maxCount);
    void saveMeasurements(Measurements ms);

}
