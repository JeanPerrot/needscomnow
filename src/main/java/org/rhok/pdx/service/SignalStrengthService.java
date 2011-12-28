package org.rhok.pdx.service;

import org.apache.log4j.Logger;
import org.rhok.pdx.dao.MeasurementsDAO;
import org.rhok.pdx.model.Measurements;

public class SignalStrengthService {
    private static Logger logger = Logger.getLogger(SignalStrengthService.class);

    private MeasurementsDAO dao;


    public Measurements getMeasurements(org.rhok.pdx.model.Location location, double range, long timestamp, int maxCount) {
        return dao.getMeasurements(location, range, timestamp, maxCount);
    }

    public void saveMeasurements(Measurements measurements) {
        dao.saveMeasurements(measurements);
    }


    public void setDao(MeasurementsDAO dao) {
        this.dao = dao;
    }

}
