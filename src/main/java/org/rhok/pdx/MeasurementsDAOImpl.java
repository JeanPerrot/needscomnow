package org.rhok.pdx;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.util.JSON;

import java.net.UnknownHostException;
import java.util.Arrays;

public class MeasurementsDAOImpl implements MeasurementsDAO {

    public static final String DB_NAME = "signalstrength";
    public static final String MONGO_URL_PROP = "MONGOHQ_URL";
    public static final String MEASUREMENTS = "measurements";

    private Mongo mongo;
    private DB db;
    private Gson gson = new Gson();

    public MeasurementsDAOImpl() {
        mongo = getMongo();
        db = mongo.getDB(DB_NAME);
        setupDB();
    }

    //ensure that the collection is indexed for geolocation queries
    private void setupDB() {
        db.createCollection(MEASUREMENTS, new BasicDBObject());
        DBObject index = new BasicDBObject();
        index.put("loc", "2d");
        DBCollection collection = getCollection();
        collection.ensureIndex(index);
    }

    @Override
    public Measurements getMeasurements(Location l, double r) {
//        db.places.find( { loc : { $near : [50,50] , $maxDistance : 5 } } )
        BasicDBObject query = new BasicDBObject("$near", Arrays.asList(l.getLat(), l.getLng()));
        query.append("$maxDistance", 5);

        DBCollection coll = getCollection();
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
//        getCollection().insert(measurement.getMeasurements());

    }


    //not the most efficient?
    private DataPoint fromMongo(DBObject obj) {
        String json = JSON.serialize(obj);
        DataPoint datum = gson.fromJson(json, DataPoint.class);
        return datum;

//        DBObject locationObj = (DBObject) obj.get("location");
//        Location location = new Location((Double) obj.get("lat"), (Double) obj.get("lon"));
//        DataPoint retValue = new DataPoint(location, (Integer) obj.get("intensity"), (Integer) obj.get("wifi"), (Long) obj.get("timestamp"));
//        return retValue;
    }

    private DBCollection getCollection() {
        DBCollection measurements = db.getCollection(MEASUREMENTS);
        return measurements;
    }

    private synchronized Mongo getMongo() {
        String url = getMongoUrl();
        try {
            Mongo mongo = new Mongo(url);
            return mongo;
        } catch (UnknownHostException e) {
            //can't find Mongo, die.
            throw new RuntimeException(e);
        }
    }

    //TODO - externalize
    private String getMongoUrl() {
        String url = System.getenv(MONGO_URL_PROP);
        if (url == null) {
            url = "localhost";
        }
        return url;
    }


}
