package clients.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import tukano.api.rest.RestUsers;
import tukano.api.util.Result;
import tukano.models.UserDTO;

public class RestUsersClient extends RestClient {

	public RestUsersClient(String serverURI) {
		super(serverURI, RestUsers.PATH);
	}

	private Result<String> _createUser(UserDTO user) {
		return super.toJavaResult(target.request().accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(user, MediaType.APPLICATION_JSON)), String.class);
	}

	private Result<UserDTO> _getUser(String userId, String pwd) {
		return super.toJavaResult(
				target.path(userId).queryParam(RestUsers.PWD, pwd).request().accept(MediaType.APPLICATION_JSON).get(),
				UserDTO.class);
	}

	public Result<UserDTO> _updateUser(String userId, String password, UserDTO user) {
		return super.toJavaResult(target.path(userId).queryParam(RestUsers.PWD, password).request()
				.accept(MediaType.APPLICATION_JSON).put(Entity.entity(user, MediaType.APPLICATION_JSON)),
				UserDTO.class);
	}

	public Result<UserDTO> _deleteUser(String userId, String password) {
		return super.toJavaResult(target.path(userId).queryParam(RestUsers.PWD, password).request()
				.accept(MediaType.APPLICATION_JSON).delete(), UserDTO.class);
	}

	public Result<List<UserDTO>> _searchUsers(String pattern) {
		return super.toJavaResult(
				target.queryParam(RestUsers.QUERY, pattern).request().accept(MediaType.APPLICATION_JSON).get(),
				new GenericType<List<UserDTO>>() {
				});
	}

	public Result<String> createUser(UserDTO user) {
		return super.reTry(() -> _createUser(user));
	}

	public Result<UserDTO> getUser(String userId, String pwd) {
		return super.reTry(() -> _getUser(userId, pwd));
	}

	public Result<UserDTO> updateUser(String userId, String pwd, UserDTO user) {
		return super.reTry(() -> _updateUser(userId, pwd, user));
	}

	public Result<UserDTO> deleteUser(String userId, String pwd) {
		return super.reTry(() -> _deleteUser(userId, pwd));
	}

	public Result<List<UserDTO>> searchUsers(String pattern) {
		return super.reTry(() -> _searchUsers(pattern));
	}
}
