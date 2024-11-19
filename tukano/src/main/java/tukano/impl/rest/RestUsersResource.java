package tukano.impl.rest;

import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Response;
import java.util.List;
import tukano.api.rest.RestUsers;
import tukano.api.service.Users;
import tukano.impl.service.users.JavaUsersHibernate;
import tukano.models.UserDTO;

@Singleton
public class RestUsersResource extends RestResource implements RestUsers {

	final Users impl;

	public RestUsersResource() {
		this.impl = JavaUsersHibernate.getInstance();
	}

	@Override
	public Response createUser(UserDTO user) {
		return super.resultOrThrow(impl.createUser(user));
	}

	@Override
	public Response getUser(String name, String pwd) {
		return super.resultOrThrow(impl.getUser(name, pwd));
	}

	@Override
	public UserDTO updateUser(String name, String pwd, UserDTO user) {
		return super.resultOrThrow(impl.updateUser(name, pwd, user));
	}

	@Override
	public UserDTO deleteUser(String name, String pwd) {
		return super.resultOrThrow(impl.deleteUser(name, pwd));
	}

	@Override
	public List<UserDTO> searchUsers(String pattern) {
		return super.resultOrThrow(impl.searchUsers(pattern));
	}
}
