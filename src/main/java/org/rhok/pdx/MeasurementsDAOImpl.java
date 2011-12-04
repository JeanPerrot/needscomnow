package org.rhok.pdx;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import java.util.Arrays;

public class MeasurementsDAOImpl implements MeasurementsDAO {

    private MongoAccess mongoAccess;
    private Gson gson = new Gson();

    public MeasurementsDAOImpl() {
    }


    @Override
    public Measurements getMeasurements(Location l, double r, int maxCount) {
//        db.places.find( { loc : { $near : [50,50] , $maxDistance : 5 } } )
        BasicDBObject query = new BasicDBObject("$near", Arrays.asList(l.getLat(), l.getLng()));
        query.append("$maxDistance", 5);

        DBCollection coll = mongoAccess.getCollection();
        DBCursor dbCursor = coll.find(query);
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
