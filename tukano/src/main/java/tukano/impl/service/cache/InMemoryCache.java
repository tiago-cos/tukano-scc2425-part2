package tukano.impl.service.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import utils.JSON;

public class InMemoryCache implements Cache {

	private Map<String, String> cache;
	private Map<String, Date> cacheExpiry;
	private Map<String, Long> counters;

	protected InMemoryCache() {
		cache = new HashMap<>();
		cacheExpiry = new HashMap<>();
		counters = new HashMap<>();
	}

	@Override
	public <T> T get(String id, Class<T> clazz) {
		String objectString = cache.get(id);
		Date expiry = cacheExpiry.get(id);
		if (expiry != null && expiry.before(new Date())) {
			cache.remove(id);
			cacheExpiry.remove(id);
			return null;
		}
		return objectString == null ? null : JSON.decode(objectString, clazz);
	}

	@Override
	public <T> void set(String id, T object, int ttl) {
		cache.put(id, JSON.encode(object));
		cacheExpiry.put(id, new Date(System.currentTimeMillis() + ttl * 1000));
	}

	@Override
	public void delete(String id) {
		cache.remove(id);
		cacheExpiry.remove(id);
	}

	@Override
	public void setCounter(String counterId, long number) {
		counters.put("counter:" + counterId, number);
	}

	@Override
	public long getCounter(String counterId) {
		return counters.getOrDefault("counter:" + counterId, 0L);
	}

	@Override
	public long incrementCounter(String counterId) {
		long value = counters.getOrDefault("counter:" + counterId, 0L) + 1;
		counters.put("counter:" + counterId, value);
		return value;
	}

	@Override
	public long decrementCounter(String counterId) {
		long value = counters.getOrDefault("counter:" + counterId, 0L) - 1;
		counters.put("counter:" + counterId, value);
		return value;
	}

	@Override
	public void deleteCounter(String counterId) {
		counters.remove("counter:" + counterId);
	}
}
