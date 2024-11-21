package tukano.impl.rest;

import jakarta.inject.Singleton;
import jakarta.ws.rs.core.UriInfo;

import java.util.List;
import tukano.api.rest.RestShorts;
import tukano.api.service.Shorts;
import tukano.impl.service.shorts.JavaShortsHibernate;
import tukano.models.ShortDTO;

@Singleton
public class RestShortsResource extends RestResource implements RestShorts {

	static final Shorts impl = JavaShortsHibernate.getInstance();

	@Override
	public ShortDTO createShort(String userId, String password, UriInfo uriInfo) {
		return super.resultOrThrow(impl.createShort(userId, password, uriInfo));
	}

	@Override
	public void deleteShort(String shortId, String password) {
		super.resultOrThrow(impl.deleteShort(shortId, password));
	}

	@Override
	public ShortDTO getShort(String shortId) {
		return super.resultOrThrow(impl.getShort(shortId));
	}

	@Override
	public List<String> getShorts(String userId) {
		return super.resultOrThrow(impl.getShorts(userId));
	}

	@Override
	public void follow(String userId1, String userId2, boolean isFollowing, String password) {
		super.resultOrThrow(impl.follow(userId1, userId2, isFollowing, password));
	}

	@Override
	public List<String> followers(String userId, String password) {
		return super.resultOrThrow(impl.followers(userId, password));
	}

	@Override
	public void like(String shortId, String userId, boolean isLiked, String password) {
		super.resultOrThrow(impl.like(shortId, userId, isLiked, password));
	}

	@Override
	public List<String> likes(String shortId, String password) {
		return super.resultOrThrow(impl.likes(shortId, password));
	}

	@Override
	public List<String> getFeed(String userId, String password) {
		return super.resultOrThrow(impl.getFeed(userId, password));
	}

	@Override
	public void deleteAllShorts(String userId, String password, String token) {
		super.resultOrThrow(impl.deleteAllShorts(userId, password, token));
	}
}
