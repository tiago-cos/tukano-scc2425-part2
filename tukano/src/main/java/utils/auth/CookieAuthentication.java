package utils.auth;

import jakarta.ws.rs.core.Cookie;
import java.util.Map;
import tukano.impl.service.cache.CacheFactory;
import tukano.models.Session;

public class CookieAuthentication implements Authentication {

	public boolean validateSession(String userId) {
		Map<String, Cookie> cookies = RequestCookies.get();
		return validateSession(cookies.get("auth:session"), userId);
	}

	public boolean isAuthenticated() {
		Cookie sessionCookie = RequestCookies.get().get("auth:session");

		if (sessionCookie == null)
			return false;

		Session session = CacheFactory.getCache().get(sessionCookie.getValue(), Session.class);
		if (session == null)
			return false;

		if (session == null || session.userId().length() == 0)
			return false;

		return true;
	}

	private boolean validateSession(Cookie sessionCookie, String userId) {
		if (sessionCookie == null)
			return false;

		Session session = CacheFactory.getCache().get(sessionCookie.getValue(), Session.class);
		if (session == null)
			return false;

		if (session == null || session.userId().length() == 0)
			return false;

		if (!session.userId().equals(userId))
			return false;

		return true;
	}
}
