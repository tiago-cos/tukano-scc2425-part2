package tukano.impl.rest;

import java.net.URI;
import java.util.logging.Logger;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import utils.Args;
import utils.RequestCookiesFilter;
import utils.Token;

public class TukanoRestServer {
	private static final Logger Log = Logger.getLogger(TukanoRestServer.class.getName());

	static final String INETADDR_ANY = "0.0.0.0";
	static String SERVER_BASE_URI = "http://%s:%s/rest";

	public static final int PORT = Integer.parseInt(System.getenv("HTTP_PLATFORM_PORT"));

	public static String serverURI;

	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s");
	}

	protected TukanoRestServer() {
		serverURI = String.format(SERVER_BASE_URI, System.getenv("WEBSITE_HOSTNAME"), PORT);
	}

	protected void start() throws Exception {

		ResourceConfig config = new ResourceConfig();

		config.register(RequestCookiesFilter.class);
		config.register(RestBlobsResource.class);
		config.register(RestUsersResource.class);
		config.register(RestShortsResource.class);

		JdkHttpServerFactory.createHttpServer(
				URI.create(serverURI.replace(System.getenv("WEBSITE_HOSTNAME"), INETADDR_ANY)), config);

		// When deploying to Azure, the server port is not the same as the one in the
		// URL
		if (serverURI.contains("azurewebsites.net"))
			serverURI = serverURI.substring(0, serverURI.lastIndexOf(':')) + "/rest";

		Log.info(String.format("Tukano Server ready @ %s\n", serverURI));
	}

	public static void main(String[] args) throws Exception {
		Args.use(args);

		Token.setSecret(Args.valueOf("-secret", ""));
		// Props.load( Args.valueOf("-props", "").split(","));

		new TukanoRestServer().start();
	}
}
