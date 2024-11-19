package tukano.impl.service.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import utils.JSON;

public class RedisCache {
	private static final String RedisHostname = String.format("%s.redis.cache.windows.net",
			System.getenv("REDIS_NAME"));
	private static final String RedisKey = System.getenv("REDIS_KEY");
	private static final int REDIS_PORT = 6380;
	private static final int REDIS_TIMEOUT = 1000;
	private static final boolean Redis_USE_TLS = true;
	private static JedisPool instance;

	public static synchronized JedisPool getCachePool() {
		if (instance != null)
			return instance;

		var poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(128);
		poolConfig.setMaxIdle(128);
		poolConfig.setMinIdle(16);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setTestWhileIdle(true);
		poolConfig.setNumTestsPerEvictionRun(3);
		poolConfig.setBlockWhenExhausted(true);
		instance = new JedisPool(poolConfig, RedisHostname, REDIS_PORT, REDIS_TIMEOUT, RedisKey, Redis_USE_TLS);
		return instance;
	}

	public static <T> T get(String id, Class<T> clazz) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			String objectString = jedis.get(id);
			return objectString == null ? null : JSON.decode(objectString, clazz);
		}
	}

	public static <T> void set(String id, T object, int ttl) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.set(id, JSON.encode(object));
			jedis.expire(id, ttl);
		}
	}

	public static void delete(String id) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.del(id);
		}
	}

	public static void setCounter(String counterId, long number) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			String key = "counter:" + counterId;
			jedis.set(key, String.valueOf(number));
			jedis.expire(key, 300);
		}
	}

	public static long getCounter(String counterId) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			String key = "counter:" + counterId;
			String value = jedis.get(key);
			return value != null ? Long.parseLong(value) : 0;
		}
	}

	public static long incrementCounter(String counterId) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			String key = "counter:" + counterId;
			return jedis.incr(key);
		}
	}

	public static long decrementCounter(String counterId) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			String key = "counter:" + counterId;
			return jedis.decr(key);
		}
	}

	public static void deleteCounter(String counterId) {
		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
			jedis.del("counter:" + counterId);
		}
	}
}
