package utils;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.core.Cookie;
import java.util.Map;
import tukano.impl.service.cache.CacheFactory;
import tukano.models.Session;

public class Authentication {

	public static Session validateSession(String userId) {
		Map<String, Cookie> cookies = RequestCookies.get();
		return validateSession(cookies.get("scc:session"), userId);
	}

	public static Session validateSession(Cookie sessionCookie, String userId) throws NotAuthorizedException {

		if (sessionCookie == null)
			throw new NotAuthorizedException("No session initialized");

		Session session = CacheFactory.getCache().get(sessionCookie.getValue(), Session.class);
		if (session == null)
			throw new NotAuthorizedException("No valid session initialized");

		if (session == null || session.userId().length() == 0)
			throw new NotAuthorizedException("No valid session initialized");

		if (!session.userId().equals(userId))
			throw new NotAuthorizedException("Invalid user : " + session.userId());

		return session;
	}
}
