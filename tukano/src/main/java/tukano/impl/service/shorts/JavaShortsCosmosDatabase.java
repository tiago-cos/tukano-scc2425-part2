package tukano.impl.service.shorts;

import static java.lang.String.format;
import static tukano.api.util.Result.ErrorCode.BAD_REQUEST;
import static tukano.api.util.Result.ErrorCode.CONFLICT;
import static tukano.api.util.Result.ErrorCode.FORBIDDEN;
import static tukano.api.util.Result.error;
import static tukano.api.util.Result.errorOrResult;
import static tukano.api.util.Result.errorOrValue;
import static tukano.api.util.Result.ok;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import jakarta.ws.rs.core.Response;
import tukano.api.service.Blobs;
import tukano.api.service.Shorts;
import tukano.api.util.Result;
import tukano.impl.service.blobs.JavaBlobs;
import tukano.impl.service.database.AzureCosmosDatabase;
import tukano.impl.service.users.JavaUsersCosmosDatabase;
import tukano.models.CosmosDBShort;
import tukano.models.CosmosDBUser;
import tukano.models.ShortDTO;
import utils.IP;
import utils.Token;

public class JavaShortsCosmosDatabase implements Shorts {

	private static Logger Log = Logger.getLogger(JavaShortsCosmosDatabase.class.getName());

	private static Shorts instance;
	private static AzureCosmosDatabase DB = AzureCosmosDatabase.getInstance();

	public static synchronized Shorts getInstance() {
		if (instance == null)
			instance = new JavaShortsCosmosDatabase();
		return instance;
	}

	private JavaShortsCosmosDatabase() {
	}

	@Override
	public Result<ShortDTO> createShort(String userId, String password) {
		Log.info(() -> format("createShort : userId = %s, pwd = %s\n", userId, password));

		return errorOrResult(okUser(userId, password), user -> {
			String shortId = format("%s!%s", userId, UUID.randomUUID());
			String blobUrl = format("%s/%s/%s", IP.serverUri(), Blobs.NAME, shortId);
			CosmosDBShort shrt = new CosmosDBShort(shortId, userId, blobUrl);

			Result<CosmosDBShort> result = DB.insertOne(shrt);

			if (!result.isOK())
				return error(result.error());

			return Result.ok(new ShortDTO(result.value()));
		});
	}

	@Override
	public Result<ShortDTO> getShort(String shortId) {
		Log.info(() -> format("getShort : shortId = %s\n", shortId));

		if (shortId == null)
			return error(BAD_REQUEST);

		Result<CosmosDBShort> result = DB.getOne(shortId, CosmosDBShort.class);

		if (!result.isOK())
			return error(result.error());

		return Result.ok(new ShortDTO(result.value()));
	}

	@Override
	public Result<Void> deleteShort(String shortId, String password) {
		Log.info(() -> format("deleteShort : shortId = %s, pwd = %s\n", shortId, password));

		Result<ShortDTO> shortResult = getShort(shortId);
		if (!shortResult.isOK())
			return error(shortResult.error());

		Result<Response> userResult = okUser(shortResult.value().getOwnerId(), password);
		if (!userResult.isOK())
			return error(userResult.error());

		Result<CosmosDBShort> deleteResult = DB.deleteOne(new CosmosDBShort(shortResult.value()));
		if (!deleteResult.isOK())
			return error(deleteResult.error());

		JavaBlobs.getInstance().delete(shortResult.value().getBlobUrl(), Token.get());
		return ok();
	}

	@Override
	public Result<List<String>> getShorts(String userId) {
		Log.info(() -> format("getShorts : userId = %s\n", userId));

		var query = format("SELECT * FROM HibernateShort s WHERE s.ownerId = '%s'", userId);
		return errorOrValue(okUser(userId),
				DB.sql(query, CosmosDBShort.class).stream().map(CosmosDBShort::getShortId).toList());
	}

	@Override
	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
		Log.info(() -> format("follow : userId1 = %s, userId2 = %s, isFollowing = %s, pwd = %s\n", userId1, userId2,
				isFollowing, password));

		Result<Response> user1Result = okUser(userId1, password);

		if (!user1Result.isOK())
			return error(user1Result.error());

		Result<Void> user2Result = okUser(userId2);

		if (!user2Result.isOK())
			return error(user2Result.error());

		CosmosDBUser dbUser1 = DB.getOne(userId1, CosmosDBUser.class).value();
		CosmosDBUser dbUser2 = DB.getOne(userId2, CosmosDBUser.class).value();

		if (isFollowing) {
			dbUser1.addFollowing(userId2);
			dbUser2.addFollower(userId1);
		} else {
			dbUser1.removeFollowing(userId2);
			dbUser2.removeFollower(userId1);
		}

		DB.updateOne(dbUser1);
		DB.updateOne(dbUser2);

		return ok();
	}

	@Override
	public Result<List<String>> followers(String userId, String password) {
		Log.info(() -> format("followers : userId = %s, pwd = %s\n", userId, password));

		Result<Response> userResult = okUser(userId, password);
		if (!userResult.isOK())
			return error(userResult.error());

		CosmosDBUser dbUser = DB.getOne(userId, CosmosDBUser.class).value();
		return ok(dbUser.getFollowers());
	}

	@Override
	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		Log.info(() -> format("like : shortId = %s, userId = %s, isLiked = %s, pwd = %s\n", shortId, userId, isLiked,
				password));

		Result<CosmosDBShort> shortResult = DB.getOne(shortId, CosmosDBShort.class);

		if (!shortResult.isOK())
			return error(shortResult.error());

		Result<Response> userResult = okUser(userId, password);
		if (!userResult.isOK())
			return error(userResult.error());

		CosmosDBShort dbShort = shortResult.value();

		if (isLiked && dbShort.getLikedBy().contains(userId))
			return error(CONFLICT);

		if (isLiked)
			dbShort.addLike(userId);
		else
			dbShort.removeLike(userId);

		DB.updateOne(dbShort);

		return ok();
	}

	@Override
	public Result<List<String>> likes(String shortId, String password) {
		Log.info(() -> format("likes : shortId = %s, pwd = %s\n", shortId, password));

		Result<CosmosDBShort> shortResult = DB.getOne(shortId, CosmosDBShort.class);
		if (!shortResult.isOK())
			return error(shortResult.error());
		CosmosDBShort dbShort = shortResult.value();

		Result<Response> userResult = okUser(dbShort.getOwnerId(), password);

		if (!userResult.isOK())
			return error(userResult.error());

		return ok(dbShort.getLikedBy());
	}

	@Override
	public Result<List<String>> getFeed(String userId, String password) {
		Log.info(() -> format("getFeed : userId = %s, pwd = %s\n", userId, password));

		Result<Response> userResult = okUser(userId, password);
		if (!userResult.isOK())
			return error(userResult.error());

		CosmosDBUser userResultDB = DB.getOne(userId, CosmosDBUser.class).value();

		List<String> following = userResultDB.getFollowing();
		following.add(userId);

		var query = format("SELECT * FROM Short s WHERE s.ownerId IN ('%s') ORDER BY s.timestamp DESC",
				String.join("', '", following));

		return ok(DB.sql(query, CosmosDBShort.class).stream().map(CosmosDBShort::getShortId).toList());
	}

	protected Result<Response> okUser(String userId, String pwd) {
		return JavaUsersCosmosDatabase.getInstance().getUser(userId, pwd);
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

		var query0 = format("SELECT * FROM HibernateUser u WHERE ARRAY_CONTAINS(u.followers, '%s')", userId);
		List<CosmosDBUser> followers = DB.sql(query0, CosmosDBUser.class);

		for (CosmosDBUser userCosmosDB : followers) {
			userCosmosDB.removeFollower(userId);
			DB.updateOne(userCosmosDB);
		}

		var query1 = format("SELECT * FROM HibernateShort s WHERE s.ownerId = '%s'", userId);
		List<CosmosDBShort> shorts = DB.sql(query1, CosmosDBShort.class);

		for (CosmosDBShort shortCosmosDB : shorts) {
			DB.deleteOne(shortCosmosDB);
		}

		var query2 = format("SELECT * FROM HibernateShort s WHERE ARRAY_CONTAINS(s.likedBy, '%s')", userId);
		List<CosmosDBShort> likedShorts = DB.sql(query2, CosmosDBShort.class);

		for (CosmosDBShort shortCosmosDB : likedShorts) {
			shortCosmosDB.removeLike(userId);
			DB.updateOne(shortCosmosDB);
		}

		return ok();
	}
}
