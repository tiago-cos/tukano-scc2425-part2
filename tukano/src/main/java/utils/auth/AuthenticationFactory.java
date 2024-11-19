package utils.auth;

public class AuthenticationFactory {
	private static Authentication instance;

	private AuthenticationFactory() {
	}

	public static synchronized Authentication getAuthentication() {
		if (instance != null)
			return instance;

		String useAuth = System.getenv().getOrDefault("USE_AUTH", "TRUE");

		if (useAuth.equals("TRUE"))
			instance = new CookieAuthentication();
		else
			instance = new MockAuthentication();

		return instance;
	}
}
