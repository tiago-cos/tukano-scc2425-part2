package tukano.impl.service.storage;

import static tukano.api.util.Result.ErrorCode.BAD_REQUEST;
import static tukano.api.util.Result.ErrorCode.CONFLICT;
import static tukano.api.util.Result.ErrorCode.INTERNAL_ERROR;
import static tukano.api.util.Result.ErrorCode.NOT_FOUND;
import static tukano.api.util.Result.error;
import static tukano.api.util.Result.ok;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;
import tukano.api.util.Result;
import utils.Hash;
import utils.IO;

public class AzureStorage implements BlobStorage {

	private static final int CHUNK_SIZE = 4096;
	private static final String CONNECTION_STRING = System.getenv("STORAGE_CONNECTION_STRING");
	private static final String CONTAINER_NAME = System.getenv("STORAGE_CONTAINER_NAME");

	private static AzureStorage instance;

	public static synchronized AzureStorage getInstance() {
		if (instance != null)
			return instance;

		BlobContainerClient client = new BlobContainerClientBuilder().connectionString(CONNECTION_STRING)
				.containerName(CONTAINER_NAME).buildClient();

		instance = new AzureStorage(client);

		return instance;
	}

	private BlobContainerClient client;

	private AzureStorage(BlobContainerClient client) {
		this.client = client;
	}

	@Override
	public Result<Void> write(String path, byte[] bytes) {

		if (path == null)
			return error(BAD_REQUEST);

		BlobClient blob = client.getBlobClient(path);

		if (blob.exists()) {
			byte[] data = blob.downloadContent().toBytes();
			if (Arrays.equals(Hash.sha256(bytes), Hash.sha256(data)))
				return ok();
			else
				return error(CONFLICT);
		}

		var data = BinaryData.fromBytes(bytes);
		blob.upload(data);

		return ok();
	}

	@Override
	public Result<Void> delete(String path) {
		if (path == null)
			return error(BAD_REQUEST);

		if (!path.contains("/"))
			path += "/";

		Iterator<BlobItem> blobItem = client.listBlobsByHierarchy(path).iterator();
		while (blobItem.hasNext()) {
			BlobClient blob1 = client.getBlobClient(blobItem.next().getName());
			blob1.delete();
		}
		return ok();
	}

	@Override
	public Result<byte[]> read(String path) {
		if (path == null)
			return error(BAD_REQUEST);

		BlobClient blob = client.getBlobClient(path);
		if (!blob.exists())
			return error(NOT_FOUND);

		// Download contents to BinaryData (check documentation for other alternatives)
		byte[] data = blob.downloadContent().toBytes();

		return data.length != 0 ? ok(data) : error(INTERNAL_ERROR);
	}

	@Override
	public Result<Void> read(String path, Consumer<byte[]> sink) {
		if (path == null)
			return error(BAD_REQUEST);

		BlobClient blob = client.getBlobClient(path);
		if (!blob.exists())
			return error(NOT_FOUND);

		// Download contents to BinaryData (check documentation for other alternatives)
		byte[] data = blob.downloadContent().toBytes();

		IO.read(data, CHUNK_SIZE, sink);

		return ok();
	}
}
