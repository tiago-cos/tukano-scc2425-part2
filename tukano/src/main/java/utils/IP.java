package utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IP {

	public static String hostAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "?.?.?.?";
		}
	}

	public static String hostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "?.?.?.?";
		}
	}

	public static String serverUri() {
		int port = Integer.parseInt(System.getenv("HTTP_PLATFORM_PORT"));
		String serverURI = String.format("http://%s:%s/rest", System.getenv("WEBSITE_HOSTNAME"), port);
		if (serverURI.contains("azurewebsites.net"))
			serverURI = serverURI.substring(0, serverURI.lastIndexOf(':')) + "/rest";
		return serverURI;
	}
}
