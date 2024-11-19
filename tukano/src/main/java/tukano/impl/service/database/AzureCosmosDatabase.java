package tukano.impl.service.database;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.CosmosException;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import java.util.List;
import java.util.function.Supplier;
import tukano.api.util.Result;
import tukano.api.util.Result.ErrorCode;
import tukano.impl.service.cache.RedisCache;
import tukano.models.CosmosDBShort;
import tukano.models.CosmosDBUser;

public class AzureCosmosDatabase {
	private static final String CONNECTION_URL = String.format("https://%s.documents.azure.com:443/",
			System.getenv("COSMOSDB_ACCOUNT_NAME"));
	private static final String DB_KEY = System.getenv("COSMOSDB_KEY");
	private static final String DB_NAME = System.getenv("COSMOSDB_DATABASE_NAME");
	private static final String USERS_CONTAINER = System.getenv("DB_CONTAINER_USERS");
	private static final String SHORTS_CONTAINER = System.getenv("DB_CONTAINER_SHORTS");
	private static final boolean CACHE_ENABLED = true;
	private static final int CACHE_TTL = 300;

	private static AzureCosmosDatabase instance;

	public static synchronized AzureCosmosDatabase getInstance() {
		if (instance != null)
			return instance;

		CosmosClient client = new CosmosClientBuilder().endpoint(CONNECTION_URL).key(DB_KEY).directMode()
				// .gatewayMode()
				.consistencyLevel(ConsistencyLevel.SESSION).connectionSharingAcrossClientsEnabled(true)
				.contentResponseOnWriteEnabled(true).buildClient();

		instance = new AzureCosmosDatabase(client);

		return instance;
	}

	private CosmosClient client;
	private CosmosDatabase db;
	private CosmosContainer usersContainer;
	private CosmosContainer shortsContainer;
	private CosmosContainer currentContainer;

	private AzureCosmosDatabase(CosmosClient client) {
		this.client = client;
	}

	private synchronized void init() {
		if (db != null)
			return;
		db = client.getDatabase(DB_NAME);
		usersContainer = db.getContainer(USERS_CONTAINER);
		shortsContainer = db.getContainer(SHORTS_CONTAINER);
	}

	public void close() {
		client.close();
	}

	public <T> Result<T> getOne(String id, Class<T> clazz) {
		return tryCatch(() -> {
			setContainer(clazz);
			T cachedObject = CACHE_ENABLED ? RedisCache.get(getCachePrefix(clazz) + id, clazz) : null;
			if (cachedObject != null)
				return cachedObject;

			T object = currentContainer.readItem(id, new PartitionKey(id), clazz).getItem();

			if (CACHE_ENABLED)
				RedisCache.set(getCachePrefix(clazz) + id, object, CACHE_TTL);

			return object;
		});
	}

	public <T> Result<T> deleteOne(T obj) {
		return tryCatch(() -> {
			setContainer(obj);
			currentContainer.deleteItem(obj, new CosmosItemRequestOptions());
			if (CACHE_ENABLED)
				RedisCache.delete(obj.toString());
			return obj;
		});
	}

	public <T> Result<T> updateOne(T obj) {
		return tryCatch(() -> {
			setContainer(obj);
			currentContainer.upsertItem(obj).getItem();
			if (CACHE_ENABLED)
				RedisCache.set(obj.toString(), obj, CACHE_TTL);
			return obj;
		});
	}

	public <T> Result<T> insertOne(T obj) {
		return tryCatch(() -> {
			setContainer(obj);
			T res = currentContainer.createItem(obj).getItem();
			if (CACHE_ENABLED)
				RedisCache.set(obj.toString(), obj, CACHE_TTL);
			return res;
		});
	}

	public <T> List<T> sql(String query, Class<T> clazz) {
		setContainer(clazz);
		var res = currentContainer.queryItems(query, new CosmosQueryRequestOptions(), clazz);
		return res.stream().toList();
	}

	private <T> void setContainer(T obj) {
		if (obj instanceof CosmosDBUser || obj == CosmosDBUser.class)
			currentContainer = usersContainer;
		else if (obj instanceof CosmosDBShort || obj == CosmosDBShort.class)
			currentContainer = shortsContainer;
		else
			throw new IllegalArgumentException("Unknown object type");
	}

	<T> Result<T> tryCatch(Supplier<T> supplierFunc) {
		try {
			init();
			return Result.ok(supplierFunc.get());
		} catch (CosmosException ce) {
			ce.printStackTrace();
			return Result.error(errorCodeFromStatus(ce.getStatusCode()));
		} catch (Exception x) {
			x.printStackTrace();
			return Result.error(ErrorCode.INTERNAL_ERROR);
		}
	}

	static Result.ErrorCode errorCodeFromStatus(int status) {
		return switch (status) {
			case 200 -> ErrorCode.OK;
			case 404 -> ErrorCode.NOT_FOUND;
			case 409 -> ErrorCode.CONFLICT;
			default -> ErrorCode.INTERNAL_ERROR;
		};
	}

	private <T> String getCachePrefix(Class<T> clazz) {
		if (clazz == CosmosDBUser.class)
			return "users:";
		if (clazz == CosmosDBShort.class)
			return "shorts:";
		throw new IllegalArgumentException("Unknown object type");
	}
}
