package utils.auth;

public class MockAuthentication implements Authentication {

    public boolean validateSession(String userId) {
        return true;
    }

    public boolean isAuthenticated() {
        return true;
    }
    
}
