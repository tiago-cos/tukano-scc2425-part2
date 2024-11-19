package tukano.impl.rest;

import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import utils.RequestCookiesFilter;
import utils.Token;

public class TomcatMain extends Application {

	private Set<Class<?>> resources = new HashSet<>();

	public TomcatMain() {
		resources.add(RequestCookiesFilter.class);
		resources.add(RestBlobsResource.class);
		resources.add(RestUsersResource.class);
		resources.add(RestShortsResource.class);
		Token.setSecret("secret");
	}

	@Override
	public Set<Class<?>> getClasses() {
		return resources;
	}
}
