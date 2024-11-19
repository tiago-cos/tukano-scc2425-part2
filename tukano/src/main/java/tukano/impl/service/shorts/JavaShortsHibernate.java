package tukano.impl.service.shorts;

import static java.lang.String.format;
import static tukano.api.util.Result.ErrorCode.BAD_REQUEST;
import static tukano.api.util.Result.ErrorCode.FORBIDDEN;
import static tukano.api.util.Result.error;
import static tukano.api.util.Result.errorOrResult;
import static tukano.api.util.Result.errorOrValue;
import static tukano.api.util.Result.errorOrVoid;
import static tukano.api.util.Result.ok;
import static utils.DB.getOne;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import jakarta.ws.rs.core.Response;
import tukano.api.service.Blobs;
import tukano.api.service.Shorts;
import tukano.api.util.Result;
import tukano.impl.service.blobs.JavaBlobs;
import tukano.impl.service.cache.RedisCache;
import tukano.impl.service.database.DatabaseCommand;
import tukano.impl.service.database.HibernateDatabase;
import tukano.impl.service.users.JavaUsersHibernate;
import tukano.models.HibernateFollowing;
import tukano.models.HibernateLikes;
import tukano.models.HibernateShort;
import tukano.models.ShortDTO;
import utils.IP;
import utils.Token;

public class JavaShortsHibernate implements Shorts {

	private static Logger Log = Logger.getLogger(JavaShortsHibernate.class.getName());

	private static Shorts instance;
	private static HibernateDatabase DB = new HibernateDatabase();

	public static synchronized Shorts getInstance() {
		if (instance == null)
			instance = new JavaShortsHibernate();
		return instance;
	}

	private JavaShortsHibernate() {
	}

	@Override
	public Result<ShortDTO> createShort(String userId, String password) {
		Log.info(() -> format("createShort : userId = %s, pwd = %s\n", userId, password));

		return errorOrResult(okUser(userId, password), user -> {
			String shortId = format("%s!%s", userId, UUID.randomUUID());
			String blobUrl = format("%s/%s/%s", IP.serverUri(), Blobs.NAME, shortId);
			HibernateShort shrt = new HibernateShort(shortId, userId, blobUrl);

			return errorOrValue(DB.insertOne(shrt), s -> new ShortDTO(s, 0));
		});
	}

	@Override
	public Result<ShortDTO> getShort(String shortId) {
		Log.info(() -> format("getShort : shortId = %s\n", shortId));

		if (shortId == null)
			return error(BAD_REQUEST);

		long cachedLikes = RedisCache.getCounter(shortId);
		String query = format("SELECT count(*) FROM Likes l WHERE l.shortId = '%s'", shortId);
		Long likes = cachedLikes == 0 ? DB.sql(query, Long.class).get(0) : cachedLikes;
		RedisCache.setCounter(shortId, likes);

		return errorOrValue(getOne(shortId, HibernateShort.class), shrt -> new ShortDTO(shrt, likes));
	}

	@Override
	public Result<Void> deleteShort(String shortId, String password) {
		Log.info(() -> format("deleteShort : shortId = %s, pwd = %s\n", shortId, password));

		return errorOrResult(getShort(shortId), shrt -> {
			return errorOrResult(okUser(shrt.getOwnerId(), password), user -> {
				JavaBlobs.getInstance().delete(shrt.getShortId(), Token.get(shrt.getShortId()));

				RedisCache.deleteCounter(shortId);

				List<DatabaseCommand<?>> commands = new ArrayList<>();
				commands.add(db -> db.deleteOne(new HibernateShort(shrt)));
				commands.add(db -> ok(db.sql(format("DELETE FROM Likes l WHERE l.shortId = '%s'", shortId))));

				DB.transaction(commands);
				return ok();
			});
		});
	}

	@Override
	public Result<List<String>> getShorts(String userId) {
		Log.info(() -> format("getShorts : userId = %s\n", userId));

		String query = format("SELECT s.shortId FROM HibernateShort s WHERE s.ownerId = '%s'", userId);
		return errorOrValue(okUser(userId), DB.sql(query, String.class));
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
		Log.info(() -> format("follow : userId1 = %s, userId2 = %s, isFollowing = %s, pwd = %s\n", userId1, userId2,
				isFollowing, password));

		return errorOrResult(okUser(userId1, password), user -> {
			HibernateFollowing f = new HibernateFollowing(userId1, userId2);
			return errorOrVoid(okUser(userId2), isFollowing ? DB.insertOne(f) : DB.deleteOne(f));
		});
	}

	@Override
	public Result<List<String>> followers(String userId, String password) {
		Log.info(() -> format("followers : userId = %s, pwd = %s\n", userId, password));

		String query = format("SELECT f.follower FROM Following f WHERE f.followee = '%s'", userId);
		return errorOrValue(okUser(userId, password), DB.sql(query, String.class));
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		Log.info(() -> format("like : shortId = %s, userId = %s, isLiked = %s, pwd = %s\n", shortId, userId, isLiked,
				password));

		return errorOrResult(getShort(shortId), shrt -> {
			HibernateLikes l = new HibernateLikes(userId, shortId, shrt.getOwnerId());
			Result<Void> result = errorOrVoid(okUser(userId, password), isLiked ? DB.insertOne(l) : DB.deleteOne(l));
			if (result.isOK() && isLiked)
				RedisCache.incrementCounter(shortId);
			if (result.isOK() && !isLiked)
				RedisCache.decrementCounter(shortId);

			return result;
		});
	}

	@Override
	public Result<List<String>> likes(String shortId, String password) {
		Log.info(() -> format("likes : shortId = %s, pwd = %s\n", shortId, password));

		return errorOrResult(getShort(shortId), shrt -> {
			var query = format("SELECT l.userId FROM Likes l WHERE l.shortId = '%s'", shortId);
			return errorOrValue(okUser(shrt.getOwnerId(), password), DB.sql(query, String.class));
		});
	}

	@Override
	public Result<List<String>> getFeed(String userId, String password) {
		Log.info(() -> format("getFeed : userId = %s, pwd = %s\n", userId, password));

		final var QUERY_FMT = """
				SELECT s.shortId, s.timestamp FROM HibernateShort s WHERE s.ownerId = '%s'
				UNION
				(SELECT s.shortId, s.timestamp FROM HibernateShort s
				JOIN Following f ON f.followee = s.ownerId
				WHERE f.follower = '%s'
				ORDER BY s.timestamp DESC)""";

		return errorOrValue(okUser(userId, password), DB.sql(format(QUERY_FMT, userId, userId), String.class));
	}

	protected Result<Response> okUser(String userId, String pwd) {
		return JavaUsersHibernate.getInstance().getUser(userId, pwd);
	}

	private Result<Void> okUser(String userId) {
		var res = okUser(userId, "");
		if (res.error() == FORBIDDEN)
			return ok();
		else
			return error(res.error());
	}

	@Override
	public Result<Void> deleteAllShorts(String userId, String password, String token) {
		Log.info(() -> format("deleteAllShorts : userId = %s, password = %s, token = %s\n", userId, password, token));

		if (!Token.isValid(token, userId))
			return error(FORBIDDEN);

		var query0 = format("SELECT s.shortId FROM HibernateShort s WHERE s.ownerId = '%s'", userId);
		List<String> userShorts = DB.sql(query0, String.class);

		for (String shortId : userShorts) {
			RedisCache.delete("shorts:" + shortId);
			RedisCache.deleteCounter(shortId);
		}

		var query1 = format("DELETE FROM HibernateShort s WHERE s.ownerId = '%s'", userId);
		var query2 = format("DELETE FROM Following f WHERE f.follower = '%s' OR f.followee = '%s'", userId, userId);
		var query3 = format("DELETE FROM Likes l WHERE l.ownerId = '%s' OR l.userId = '%s'", userId, userId);

		List<DatabaseCommand<?>> commands = new ArrayList<>();
		commands.add(db -> Result.ok(db.sql(query1)));
		commands.add(db -> Result.ok(db.sql(query2)));
		commands.add(db -> Result.ok(db.sql(query3)));

		return DB.transaction(commands);
	}
}
