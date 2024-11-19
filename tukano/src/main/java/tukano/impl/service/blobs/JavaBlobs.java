package tukano.impl.service.blobs;

import static java.lang.String.format;
import static tukano.api.util.Result.ErrorCode.FORBIDDEN;
import static tukano.api.util.Result.error;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.logging.Logger;
import tukano.api.service.Blobs;
import tukano.api.util.Result;
import tukano.impl.service.storage.AzureStorage;
import tukano.impl.service.storage.BlobStorage;
import utils.Hash;
import utils.Hex;
import utils.IP;
import utils.Token;

public class JavaBlobs implements Blobs {

	private static Blobs instance;
	private static Logger Log = Logger.getLogger(JavaBlobs.class.getName());

	public String baseURI;
	private BlobStorage storage;
	private HttpClient client;

	public static synchronized Blobs getInstance() {
		if (instance == null)
			instance = new JavaBlobs();
		return instance;
	}

	private JavaBlobs() {
		storage = AzureStorage.getInstance();
		baseURI = String.format("%s/%s/", IP.serverUri(), Blobs.NAME);
		client = HttpClient.newHttpClient();
	}

	@Override
	public Result<Void> upload(String blobId, byte[] bytes, String token) {
		Log.info(() -> format("upload : blobId = %s, sha256 = %s, token = %s\n", blobId, Hex.of(Hash.sha256(bytes)),
				token));

		if (!validBlobId(blobId, token))
			return error(FORBIDDEN);

		return storage.write(toPath(blobId), bytes);
	}

	@Override
	public Result<byte[]> download(String blobId, String token) {
		Log.info(() -> format("download : blobId = %s, token=%s\n", blobId, token));

		if (!validBlobId(blobId, token))
			return error(FORBIDDEN);

		Result<byte[]> result = storage.read(toPath(blobId));

		if (!result.isOK())
			return result;

		try {
			String url = String.format("http://%s.azurewebsites.net/rest/blobs/%s", System.getenv("FUNCTIONS_NAME"),
					blobId);
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

			client.sendAsync(request, BodyHandlers.ofString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public Result<Void> delete(String blobId, String token) {
		Log.info(() -> format("delete : blobId = %s, token=%s\n", blobId, token));

		if (!validBlobId(blobId, token))
			return error(FORBIDDEN);

		return storage.delete(toPath(blobId));
	}

	@Override
	public Result<Void> deleteAllBlobs(String userId, String token) {
		Log.info(() -> format("deleteAllBlobs : userId = %s, token=%s\n", userId, token));

		if (!Token.isValid(token, userId))
			return error(FORBIDDEN);

		return storage.delete(toPath(userId));
	}

	private boolean validBlobId(String blobId, String token) {
		return Token.isValid(token, blobId);
	}

	private String toPath(String blobId) {
		return blobId.replace("!", "/");
	}
}
