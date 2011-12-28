package org.rhok.pdx.dao;

import com.mongodb.*;
import org.apache.log4j.Logger;

public class MongoAccess {
    private static Logger logger = Logger.getLogger(MongoAccess.class);

    //    public static final String DB_NAME = "signalstrength";
    public static final String MONGO_URL_PROP = "MONGOHQ_URL";
    public static final String MEASUREMENTS = "measurements";
    public static final int COLLECTION_EXISTS = -5;

    private String dbName;
    private Mongo mongo;
    private DB db;


    public MongoAccess() {
        MongoURI uri = new MongoURI(getMongoUrl());
        this.dbName = uri.getDatabase();
        mongo = getMongo();
        logger.info("connecting to mongo database " + dbName);
        db = mongo.getDB(dbName);
        if (uri.getUsername()!=null){
            db.authenticate(uri.getUsername(), uri.getPassword());
        }
        setupDB();
    }

//    public MongoAccess() {
//        this(DB_NAME);
//    }

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
        Mongo mongo = null;
        try {
            logger.info("connecting to mongo db");
            mongo = new Mongo(new MongoURI(url));
            logger.info("successfully connected to MongoDB");
            return mongo;
        } catch (Exception e) {
            logger.error("could not create connection to MongoDB", e);
            throw new RuntimeException(e);
        }
    }

    //TODO - externalize
    private String getMongoUrl() {
        String url = System.getenv(MONGO_URL_PROP);
        if (url == null) {
            url = "mongodb://localhost/signalstrength";
//            url = "mongodb://RhokPDX2011:rhok@staff.mongohq.com:10082/signalstrength";
        }
        logger.info("mongoDB url:" + url);
        return url;
    }


}
