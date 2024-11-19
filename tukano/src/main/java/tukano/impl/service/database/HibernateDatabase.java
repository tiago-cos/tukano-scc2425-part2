package tukano.impl.service.database;

import java.util.List;
import tukano.api.util.Result;
import tukano.impl.service.cache.CacheFactory;
import tukano.models.HibernateFollowing;
import tukano.models.HibernateLikes;
import tukano.models.HibernateShort;
import tukano.models.HibernateUser;
import utils.Hibernate;

public class HibernateDatabase {
	private static final int CACHE_TTL = 300;

	public <T> Result<T> getOne(String id, Class<T> clazz) {
		T cachedObject = CacheFactory.getCache().get(getCachePrefix(clazz) + id, clazz);
		if (cachedObject != null)
			return Result.ok(cachedObject);

		Result<T> res = Hibernate.getInstance().getOne(id, clazz);
		if (res.isOK() && clazz != HibernateFollowing.class && clazz != HibernateLikes.class)
			CacheFactory.getCache().set(getCachePrefix(clazz) + id, res.value(), CACHE_TTL);

		return res;
	}

	public <T> Result<T> deleteOne(T obj) {
		Result<T> res = Hibernate.getInstance().deleteOne(obj);
		if (res.isOK() && obj.getClass() != HibernateFollowing.class && obj.getClass() != HibernateLikes.class)
			CacheFactory.getCache().delete(obj.toString());
		return res;
	}

	public <T> Result<T> updateOne(T obj) {
		Result<T> res = Hibernate.getInstance().updateOne(obj);
		if (res.isOK() && obj.getClass() != HibernateFollowing.class && obj.getClass() != HibernateLikes.class)
			CacheFactory.getCache().set(obj.toString(), obj, CACHE_TTL);
		return res;
	}

	public <T> Result<T> insertOne(T obj) {
		Result<Void> res = Hibernate.getInstance().persistOne(obj);
		if (res.isOK() && obj.getClass() != HibernateFollowing.class && obj.getClass() != HibernateLikes.class)
			CacheFactory.getCache().set(obj.toString(), obj, CACHE_TTL);
		return Result.errorOrValue(res, obj);
	}

	public <T> List<T> sql(String query, Class<T> clazz) {
		return Hibernate.getInstance().sql(query, clazz);
	}

	public int sql(String query) {
		return Hibernate.getInstance().sql(query);
	}

	public Result<Void> transaction(List<DatabaseCommand<?>> commands) {
		return Hibernate.getInstance().execute((hibernate) -> {
			for (DatabaseCommand<?> command : commands)
				command.execute(this);
			return Result.ok();
		});
	}

	private <T> String getCachePrefix(Class<T> clazz) {
		if (clazz == HibernateUser.class)
			return "users:";
		if (clazz == HibernateShort.class)
			return "shorts:";
		throw new IllegalArgumentException("Unknown object type");
	}
}
