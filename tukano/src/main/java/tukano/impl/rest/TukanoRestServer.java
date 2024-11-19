package tukano.impl.rest;

import java.net.URI;
import java.util.logging.Logger;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import utils.RequestCookiesFilter;
import utils.Token;

public class TukanoRestServer {
	private static final Logger Log = Logger.getLogger(TukanoRestServer.class.getName());
	private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
	private static final String HOST = System.getenv().getOrDefault("HOST", "localhost");
	private static final String SERVER_BASE_URI = "http://%s:%s/rest";

	protected void start() throws Exception {
		ResourceConfig config = new ResourceConfig();

		config.register(RequestCookiesFilter.class);
		config.register(RestBlobsResource.class);
		config.register(RestUsersResource.class);
		config.register(RestShortsResource.class);

		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");

		JdkHttpServerFactory.createHttpServer(URI.create(String.format(SERVER_BASE_URI, HOST, PORT)), config);

		Log.info(String.format("Tukano Server ready @ %s\n", String.format(SERVER_BASE_URI, HOST, PORT)));
	}

	public static void main(String[] args) throws Exception {
		Token.setSecret(System.getenv().getOrDefault("BLOBS_TOKEN", "secret"));
		new TukanoRestServer().start();
	}
}
