package tukano.impl.service.cache;

public class MockCache implements Cache {

	protected MockCache() {
	}

	@Override
	public <T> T get(String id, Class<T> clazz) {
		return null;
	}

	@Override
	public <T> void set(String id, T object, int ttl) {
		return;
	}

	@Override
	public void delete(String id) {
		return;
	}

	@Override
	public void setCounter(String counterId, long number) {
		return;
	}

	@Override
	public long getCounter(String counterId) {
		return 0;
	}

	@Override
	public long incrementCounter(String counterId) {
		return 0;
	}

	@Override
	public long decrementCounter(String counterId) {
		return 0;
	}

	@Override
	public void deleteCounter(String counterId) {
		return;
	}
}
