package tukano.impl.service.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import utils.JSON;

public class RedisCache implements Cache {
	private static final String RedisHostname = "redis";
	private static final String RedisKey = System.getenv("REDIS_KEY");
	private static final int REDIS_PORT = 6380;
	private static final int REDIS_TIMEOUT = 1000;
	private static final boolean Redis_USE_TLS = true;

	private JedisPool instance;

	protected RedisCache() {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(128);
		poolConfig.setMaxIdle(128);
		poolConfig.setMinIdle(16);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);
		poolConfig.setTestWhileIdle(true);
		poolConfig.setNumTestsPerEvictionRun(3);
		poolConfig.setBlockWhenExhausted(true);

		instance = new JedisPool(poolConfig, RedisHostname, REDIS_PORT, REDIS_TIMEOUT, RedisKey, Redis_USE_TLS);
	}

	@Override
	public <T> T get(String id, Class<T> clazz) {
		try (Jedis jedis = instance.getResource()) {
			String objectString = jedis.get(id);
			return objectString == null ? null : JSON.decode(objectString, clazz);
		}
	}

	@Override
	public <T> void set(String id, T object, int ttl) {
		try (Jedis jedis = instance.getResource()) {
			jedis.set(id, JSON.encode(object));
			jedis.expire(id, ttl);
		}
	}

	@Override
	public void delete(String id) {
		try (Jedis jedis = instance.getResource()) {
			jedis.del(id);
		}
	}

	@Override
	public void setCounter(String counterId, long number) {
		try (Jedis jedis = instance.getResource()) {
			String key = "counter:" + counterId;
			jedis.set(key, String.valueOf(number));
			jedis.expire(key, 300);
		}
	}

	@Override
	public long getCounter(String counterId) {
		try (Jedis jedis = instance.getResource()) {
			String key = "counter:" + counterId;
			String value = jedis.get(key);
			return value != null ? Long.parseLong(value) : 0;
		}
	}

	@Override
	public long incrementCounter(String counterId) {
		try (Jedis jedis = instance.getResource()) {
			String key = "counter:" + counterId;
			return jedis.incr(key);
		}
	}

	@Override
	public long decrementCounter(String counterId) {
		try (Jedis jedis = instance.getResource()) {
			String key = "counter:" + counterId;
			return jedis.decr(key);
		}
	}

	@Override
	public void deleteCounter(String counterId) {
		try (Jedis jedis = instance.getResource()) {
			jedis.del("counter:" + counterId);
		}
	}
}
