package blobstore;

import blobstore.auth.AuthFilter;
import java.net.URI;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class Server {
	private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8081"));
	private static final String HOST = System.getenv().getOrDefault("HOST", "0.0.0.0");
	private static final String SERVER_BASE_URI = "http://%s:%s/rest";

	public static void main(String[] args) {
		ResourceConfig config = new ResourceConfig();

		config.register(AuthFilter.class);
		config.register(BlobController.class);

		JdkHttpServerFactory.createHttpServer(URI.create(String.format(SERVER_BASE_URI, HOST, PORT)), config);
	}
}
