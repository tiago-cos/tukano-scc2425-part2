package tukano.impl.service.blobs;

import static java.lang.String.format;
import static tukano.api.util.Result.ErrorCode.FORBIDDEN;
import static tukano.api.util.Result.error;

import java.util.logging.Logger;
import tukano.api.service.Blobs;
import tukano.api.util.Result;
import tukano.impl.service.storage.BlobStorage;
import tukano.impl.service.storage.StorageFactory;
import utils.Hash;
import utils.Hex;
import utils.Token;
import utils.auth.AuthenticationFactory;

public class JavaBlobs implements Blobs {

	private static Blobs instance;
	private static Logger Log = Logger.getLogger(JavaBlobs.class.getName());

	private BlobStorage storage;

	public static synchronized Blobs getInstance() {
		if (instance == null)
			instance = new JavaBlobs();
		return instance;
	}

	private JavaBlobs() {
		storage = StorageFactory.getStorage();
	}

	@Override
	public Result<Void> upload(String blobId, byte[] bytes, String token) {
		Log.info(() -> format("upload : blobId = %s, sha256 = %s, token = %s\n", blobId, Hex.of(Hash.sha256(bytes)),
				token));

		if (!validBlobId(blobId, token) || !AuthenticationFactory.getAuthentication().isAuthenticated())
			return error(FORBIDDEN);

		return storage.write(toPath(blobId), bytes);
	}

	@Override
	public Result<byte[]> download(String blobId, String token) {
		Log.info(() -> format("download : blobId = %s, token=%s\n", blobId, token));

		if (!validBlobId(blobId, token) || !AuthenticationFactory.getAuthentication().isAuthenticated())
			return error(FORBIDDEN);

		Result<byte[]> result = storage.read(toPath(blobId));

		if (!result.isOK())
			return result;

		return result;
	}

	@Override
	public Result<Void> delete(String blobId, String token) {
		Log.info(() -> format("delete : blobId = %s, token=%s\n", blobId, token));

		if (!validBlobId(blobId, token) || !AuthenticationFactory.getAuthentication().validateSession("admin"))
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
