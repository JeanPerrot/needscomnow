package org.rhok.pdx.dao;

import org.rhok.pdx.model.Location;
import org.rhok.pdx.model.Measurements;

public interface MeasurementsDAO {
    Measurements getMeasurements(Location location, double range, long timestamp, int maxCount);
    void saveMeasurements(Measurements ms);

}
