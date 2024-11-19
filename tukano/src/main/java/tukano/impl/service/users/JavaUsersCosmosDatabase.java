package tukano.impl.service.users;

import static java.lang.String.format;
import static tukano.api.util.Result.ErrorCode.BAD_REQUEST;
import static tukano.api.util.Result.ErrorCode.FORBIDDEN;
import static tukano.api.util.Result.error;
import static tukano.api.util.Result.errorOrValue;
import static tukano.api.util.Result.ok;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import tukano.api.service.Users;
import tukano.api.util.Result;
import tukano.impl.service.blobs.JavaBlobs;
import tukano.impl.service.cache.RedisCache;
import tukano.impl.service.database.AzureCosmosDatabase;
import tukano.impl.service.shorts.JavaShortsCosmosDatabase;
import tukano.models.CosmosDBUser;
import tukano.models.Session;
import tukano.models.UserDTO;
import utils.Token;

public class JavaUsersCosmosDatabase implements Users {

	private static Logger Log = Logger.getLogger(JavaUsersCosmosDatabase.class.getName());

	private static Users instance;
	private static AzureCosmosDatabase DB = AzureCosmosDatabase.getInstance();

	public static synchronized Users getInstance() {
		if (instance == null)
			instance = new JavaUsersCosmosDatabase();
		return instance;
	}

	private JavaUsersCosmosDatabase() {
	}

	private <T> Response addSession(String userId, T response) {
		String sessionId = UUID.randomUUID().toString();
		var cookie = new NewCookie.Builder("auth:session")
			.value(sessionId)
			.path("/")
			.comment("sessionid")
			.secure(false)
			.httpOnly(true)
			.build();

		RedisCache.set(sessionId, new Session(sessionId, userId), 60 * 15);

		return Response.ok(response).cookie(cookie).build();
	}

	@Override
	public Result<String> createUser(UserDTO user) {
		Log.info(() -> format("createUser : %s\n", user));

		if (badUserInfo(user))
			return error(BAD_REQUEST);

		return errorOrValue(DB.insertOne(new CosmosDBUser(user)), user.getUserId());
	}

	@Override
	public Result<Response> getUser(String userId, String pwd) {
		Log.info(() -> format("getUser : userId = %s, pwd = %s\n", userId, pwd));

		if (userId == null)
			return error(BAD_REQUEST);

		Result<CosmosDBUser> res = DB.getOne(userId, CosmosDBUser.class);

		if (!res.isOK())
			return error(res.error());
		if (!res.value().getPwd().equals(pwd))
			return error(FORBIDDEN);

		return ok(addSession(userId, new UserDTO(res.value())));
	}

	@Override
	public Result<UserDTO> updateUser(String userId, String pwd, UserDTO other) {
		Log.info(() -> format("updateUser : userId = %s, pwd = %s, user: %s\n", userId, pwd, other));

		if (badUpdateUserInfo(userId, pwd, other))
			return error(BAD_REQUEST);

		Result<CosmosDBUser> res = DB.getOne(userId, CosmosDBUser.class);

		if (!res.isOK())
			return error(res.error());
		if (!res.value().getPwd().equals(pwd))
			return error(FORBIDDEN);

		CosmosDBUser updatedUser = res.value().updateFrom(other);
		Result<CosmosDBUser> res2 = DB.updateOne(updatedUser);

		if (!res2.isOK())
			return error(res2.error());

		return ok(new UserDTO(res2.value()));
	}

	@Override
	public Result<UserDTO> deleteUser(String userId, String pwd) {
		Log.info(() -> format("deleteUser : userId = %s, pwd = %s\n", userId, pwd));

		if (userId == null || pwd == null)
			return error(BAD_REQUEST);

		Result<CosmosDBUser> res = DB.getOne(userId, CosmosDBUser.class);

		if (!res.isOK())
			return error(res.error());
		if (!res.value().getPwd().equals(pwd))
			return error(FORBIDDEN);

		// Delete user shorts and related info asynchronously in a separate thread
		Executors.defaultThreadFactory().newThread(() -> {
			JavaShortsCosmosDatabase.getInstance().deleteAllShorts(userId, pwd, Token.get(userId));
			JavaBlobs.getInstance().deleteAllBlobs(userId, Token.get(userId));
		}).start();

		Result<CosmosDBUser> res2 = DB.deleteOne(res.value());
		if (!res2.isOK())
			return error(res2.error());

		return ok(new UserDTO(res2.value()));
	}

	@Override
	public Result<List<UserDTO>> searchUsers(String pattern) {
		Log.info(() -> format("searchUsers : patterns = %s\n", pattern));

		var query = format("SELECT * FROM HibernateUser u WHERE UPPER(u.id) LIKE '%%%s%%'", pattern.toUpperCase());
		var hits = DB.sql(query, CosmosDBUser.class).stream().map(CosmosDBUser::secureCopy).map(UserDTO::new).toList();

		return ok(hits);
	}

	private boolean badUserInfo(UserDTO user) {
		return (user.getUserId() == null || user.getPwd() == null || user.getDisplayName() == null
				|| user.getEmail() == null);
	}

	private boolean badUpdateUserInfo(String userId, String pwd, UserDTO info) {
		return (userId == null || pwd == null || info.getUserId() != null && !userId.equals(info.getUserId()));
	}
}
