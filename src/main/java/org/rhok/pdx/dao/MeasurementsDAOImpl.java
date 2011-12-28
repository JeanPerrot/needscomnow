package org.rhok.pdx.dao;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.rhok.pdx.model.DataPoint;
import org.rhok.pdx.model.Location;
import org.rhok.pdx.model.Measurements;

public class MeasurementsDAOImpl implements MeasurementsDAO {

    private MongoAccess mongoAccess;
    private Gson gson = new Gson();


    @Override
    public Measurements getMeasurements(Location location, double range, long timestamp, int maxCount) {
//      example query:
//      db.places.find( { loc : { $near : [50,50] , $maxDistance : 5 } } ).limit(20)
        BasicDBObject locObj = new BasicDBObject("lat", location.getLat()).append("lng", location.getLng());
        BasicDBObject near = new BasicDBObject("$near", locObj).append("$maxDistance", range);
        BasicDBObject query = new BasicDBObject("location", near);

        BasicDBObject time = new BasicDBObject("$gt", timestamp);
        query.append("timestamp", time);

        DBCollection coll = mongoAccess.getCollection();
        DBCursor dbCursor = coll.find(query).limit(maxCount);
        Measurements retValue = new Measurements();
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            DataPoint datum = fromMongo(obj);
            retValue.add(datum);
        }
        return retValue;
    }

    @Override
    public void saveMeasurements(Measurements measurement) {
        //not great... TODO: try inserting a list directly
        DBCollection coll = mongoAccess.getCollection();
        for (DataPoint dataPoint : measurement.getMeasurements()) {
            coll.insert((DBObject) JSON.parse(gson.toJson(dataPoint)));
        }
    }

    private DataPoint fromMongo(DBObject obj) {
        String json = JSON.serialize(obj);
        DataPoint datum = gson.fromJson(json, DataPoint.class);
        return datum;
    }

    public void setMongoAccess(MongoAccess mongoAccess) {
        this.mongoAccess = mongoAccess;
    }
}
