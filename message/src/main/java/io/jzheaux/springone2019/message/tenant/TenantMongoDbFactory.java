package io.jzheaux.springone2019.message.tenant;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

public class TenantMongoDbFactory extends SimpleMongoDbFactory {
	public TenantMongoDbFactory(MongoClient mongoClient, String databaseName) {
		super(mongoClient, databaseName);
	}

	@Override
	public MongoDatabase getDb(String dbName) throws DataAccessException {
		String tenant = TenantResolver.resolve();
		return super.getDb(tenant + "_" + dbName);
	}
}
