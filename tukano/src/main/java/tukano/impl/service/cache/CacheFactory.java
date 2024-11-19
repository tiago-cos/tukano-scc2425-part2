package tukano.impl.service.cache;

public class CacheFactory {
	private static Cache instance;

	private CacheFactory() {
	}

	public static synchronized Cache getCache() {
		if (instance != null)
			return instance;

		String useCache = System.getenv().getOrDefault("USE_CACHE", "TRUE");
		String cacheType = System.getenv().getOrDefault("CACHE_TYPE", "IN_MEMORY");

		if (useCache.equals("FALSE"))
			instance = new MockCache();
		else if (cacheType.equals("REDIS"))
			instance = new RedisCache();
		else
			instance = new InMemoryCache();

		return instance;
	}
}
