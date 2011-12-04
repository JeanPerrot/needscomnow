package org.rhok.pdx;

import com.mongodb.*;
import com.mongodb.util.JSON;

import java.net.UnknownHostException;
import java.util.List;

public class MongoAccess {

    public static final String DB_NAME = "signalstrength";
    public static final String MONGO_URL_PROP = "MONGOHQ_URL";
    public static final String MEASUREMENTS = "measurements";
    public static final int COLLECTION_EXISTS = -5;

    private String dbName;
    private Mongo mongo;
    private DB db;


    public MongoAccess(String dbName) {
        this.dbName = dbName;
        mongo = getMongo();
        db = mongo.getDB(dbName);
        setupDB();
    }

    public MongoAccess() {
        this(DB_NAME);
    }

    //ensure that the collection is indexed for geolocation queries
    public void setupDB() {
        try {
            db.createCollection(MEASUREMENTS, new BasicDBObject());
        } catch (MongoException e) {
            if (e.getCode() != COLLECTION_EXISTS) {
                throw e;
            }
        }
        DBObject index = new BasicDBObject();
        index.put("location", "2d");
        DBCollection collection = getCollection();
        collection.createIndex(index);
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
