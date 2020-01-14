package dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;


public class MongoConnection {
	
	MongoClient mongoClient;	

	public MongoConnection() {
		mongoClient = MongoClients.create();
	}
	
	public MongoClient getMongoClient() {
		return mongoClient;
	}
	
	public MongoDatabase getDB(String database)
	{
		return mongoClient.getDatabase(database);
	}
}
