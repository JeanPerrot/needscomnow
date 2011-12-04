package org.rhok.pdx;

public interface MeasurementsDAO {
    Measurements getMeasurements(Location l, double r);
    void saveMeasurements(Measurements ms);

}
