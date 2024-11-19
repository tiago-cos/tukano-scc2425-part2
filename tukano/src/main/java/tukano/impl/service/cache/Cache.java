package tukano.impl.service.cache;

public interface Cache {
	public <T> T get(String id, Class<T> clazz);

	public <T> void set(String id, T object, int ttl);

	public void delete(String id);

	public void setCounter(String counterId, long number);

	public long getCounter(String counterId);

	public long incrementCounter(String counterId);

	public long decrementCounter(String counterId);

	public void deleteCounter(String counterId);
}
