package utils.auth;

public interface Authentication {

	public boolean validateSession(String userId);

	public boolean isAuthenticated();
}
