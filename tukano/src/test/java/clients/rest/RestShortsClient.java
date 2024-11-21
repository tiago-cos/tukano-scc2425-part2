package clients.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import tukano.api.rest.RestShorts;
import tukano.api.util.Result;
import tukano.models.ShortDTO;

public class RestShortsClient extends RestClient {

	public RestShortsClient(String serverURI) {
		super(serverURI, RestShorts.PATH);
	}

	public Result<ShortDTO> _createShort(String userId, String password) {
		return super.toJavaResult(target.path(userId).queryParam(RestShorts.PWD, password).request()
				.accept(MediaType.APPLICATION_JSON).post(Entity.json(null)), ShortDTO.class);
	}

	public Result<Void> _deleteShort(String shortId, String password) {
		return super.toJavaResult(target.path(shortId).queryParam(RestShorts.PWD, password).request().delete());
	}

	public Result<ShortDTO> _getShort(String shortId) {
		return super.toJavaResult(target.path(shortId).request().get(), ShortDTO.class);
	}

	public Result<List<String>> _getShorts(String userId) {
		return super.toJavaResult(
				target.path(userId).path(RestShorts.SHORTS).request().accept(MediaType.APPLICATION_JSON).get(),
				new GenericType<List<String>>() {
				});
	}

	public Result<Void> _follow(String userId1, String userId2, boolean isFollowing, String password) {
		return super.toJavaResult(
				target.path(userId1).path(userId2).path(RestShorts.FOLLOWERS).queryParam(RestShorts.PWD, password)
						.request().post(Entity.entity(isFollowing, MediaType.APPLICATION_JSON)));
	}

	public Result<List<String>> _followers(String userId, String password) {
		return super.toJavaResult(target.path(userId).path(RestShorts.FOLLOWERS).queryParam(RestShorts.PWD, password)
				.request().accept(MediaType.APPLICATION_JSON).get(), new GenericType<List<String>>() {
				});
	}

	public Result<Void> _like(String shortId, String userId, boolean isLiked, String password) {
		return super.toJavaResult(
				target.path(shortId).path(userId).path(RestShorts.LIKES).queryParam(RestShorts.PWD, password).request()
						.post(Entity.entity(isLiked, MediaType.APPLICATION_JSON)));
	}

	public Result<List<String>> _likes(String shortId, String password) {
		return super.toJavaResult(target.path(shortId).path(RestShorts.LIKES).queryParam(RestShorts.PWD, password)
				.request().accept(MediaType.APPLICATION_JSON).get(), new GenericType<List<String>>() {
				});
	}

	public Result<List<String>> _getFeed(String userId, String password) {
		return super.toJavaResult(target.path(userId).path(RestShorts.FEED).queryParam(RestShorts.PWD, password)
				.request().accept(MediaType.APPLICATION_JSON).get(), new GenericType<List<String>>() {
				});
	}

	public Result<Void> _deleteAllShorts(String userId, String password, String token) {
		return super.toJavaResult(target.path(userId).path(RestShorts.SHORTS).queryParam(RestShorts.PWD, password)
				.queryParam(RestShorts.TOKEN, token).request().delete());
	}

	public Result<Void> _verifyBlobURI(String blobId) {
		return super.toJavaResult(target.path(blobId).request().get());
	}

	public Result<ShortDTO> createShort(String userId, String password) {
		return super.reTry(() -> _createShort(userId, password));
	}

	public Result<Void> deleteShort(String shortId, String password) {
		return super.reTry(() -> _deleteShort(shortId, password));
	}

	public Result<ShortDTO> getShort(String shortId) {
		return super.reTry(() -> _getShort(shortId));
	}

	public Result<List<String>> getShorts(String userId) {
		return super.reTry(() -> _getShorts(userId));
	}

	public Result<Void> follow(String userId1, String userId2, boolean isFollowing, String password) {
		return super.reTry(() -> _follow(userId1, userId2, isFollowing, password));
	}

	public Result<List<String>> followers(String userId, String password) {
		return super.reTry(() -> _followers(userId, password));
	}

	public Result<Void> like(String shortId, String userId, boolean isLiked, String password) {
		return super.reTry(() -> _like(shortId, userId, isLiked, password));
	}

	public Result<List<String>> likes(String shortId, String password) {
		return super.reTry(() -> _likes(shortId, password));
	}

	public Result<List<String>> getFeed(String userId, String password) {
		return super.reTry(() -> _getFeed(userId, password));
	}

	public Result<Void> deleteAllShorts(String userId, String password, String token) {
		return super.reTry(() -> _deleteAllShorts(userId, password, token));
	}
}
