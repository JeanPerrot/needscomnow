package org.rhok.pdx;

import com.mongodb.*;

import java.net.UnknownHostException;

public class MongoAccess {

    public static final String DB_NAME = "signalstrength";
    public static final String MONGO_URL_PROP = "MONGOHQ_URL";
    public static final String MEASUREMENTS = "measurements";

    private String dbName;
    private Mongo mongo;
    private DB db;


    public MongoAccess(String dbName) {
        mongo = getMongo();
        db = mongo.getDB(dbName);
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

    public DBCollection getCollection() {
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
