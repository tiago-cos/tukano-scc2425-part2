package tukano.api.service;

import java.util.List;

import jakarta.ws.rs.core.Response;
import tukano.api.util.Result;
import tukano.models.UserDTO;

public interface Users {

	String NAME = "users";

	/**
	 * Creates a new user.
	 *
	 * @param user
	 *            - User to be created
	 * @return OK - the userId of the user. CONFLICT - if the name already exists.
	 *         BAD_REQUEST - otherwise.
	 */
	Result<String> createUser(UserDTO user);

	/**
	 * Obtains the information on the user identified by userId
	 *
	 * @param userId
	 *            - the userId of the user
	 * @param pwd
	 *            - the password of the user
	 * @return OK and the user object, if the userId exists and password matches the
	 *         existing password; FORBIDDEN - if the password is incorrect;
	 *         NOT_FOUND - if no user exists with the provided userId
	 */
	Result<Response> getUser(String userId, String pwd);

	/**
	 * Modifies the information of a user. Value of null, in any field of the user
	 * argument, means the field will remain as unchanged (the userId cannot be
	 * modified).
	 *
	 * @param userId
	 *            - the userId of the user
	 * @param pwd
	 *            - password of the user
	 * @param user
	 *            - Updated information
	 * @return OK and the updated user object, if the userId exists and password
	 *         matches the existing password FORBIDDEN - if the password is
	 *         incorrect NOT_FOUND - if no user exists with the provided userId
	 *         BAD_REQUEST - otherwise.
	 */
	Result<UserDTO> updateUser(String userId, String pwd, UserDTO user);

	/**
	 * Deletes the user identified by userId
	 *
	 * @param userId
	 *            - the userId of the user
	 * @param pwd
	 *            - password of the user
	 * @return OK and the deleted user object, if the name exists and pwd matches
	 *         the existing password FORBIDDEN if the password is incorrect
	 *         NOT_FOUND if no user exists with the provided name BAD_REQUEST
	 *         otherwise
	 */
	Result<UserDTO> deleteUser(String userId, String pwd);

	/**
	 * Returns the list of users for which the pattern is a substring of the userId,
	 * case-insensitive. The password of the users returned by the query must be set
	 * to the empty string "".
	 *
	 * @param pattern
	 *            - substring to search
	 * @return OK when the search was successful, regardless of the number of hits
	 *         (including 0 hits). BAD_REQUEST - otherwise.
	 */
	Result<List<UserDTO>> searchUsers(String pattern);
}
