package blobstore.auth;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class AuthFilter implements ContainerRequestFilter {
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String TOKEN = System.getenv("STORAGE_TOKEN");

	@Override
	public void filter(ContainerRequestContext requestContext) {
		String authHeader = requestContext.getHeaderString(AUTHORIZATION_HEADER);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			return;
		}

		if (!authHeader.substring(7).equals(TOKEN)) {
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		}
	}
}
