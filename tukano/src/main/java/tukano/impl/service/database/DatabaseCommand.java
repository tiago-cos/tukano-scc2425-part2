package tukano.impl.service.database;

import tukano.api.util.Result;

@FunctionalInterface
public interface DatabaseCommand<T> {
	public Result<T> execute(HibernateDatabase db);
}
