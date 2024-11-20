package blobstore;

import blobstore.errors.Exceptions.BlobConflictException;
import blobstore.errors.Exceptions.BlobNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class BlobService {

	private static BlobService instance;

	public static synchronized BlobService getInstance() {
		if (instance == null)
			instance = new BlobService();
		return instance;
	}

	private BlobService() {
		this.blobData = BlobData.getInstance();
	}

	private BlobData blobData;

	public void uploadBlob(String path, byte[] bytes) throws NoSuchAlgorithmException, IOException {
		if (blobData.fileExists(path, bytes))
			return;

		if (blobData.idExists(path))
			throw new BlobConflictException();

		blobData.write(path, bytes);
		return;
	}

	public byte[] downloadBlob(String path) throws IOException {
		if (!blobData.idExists(path))
			throw new BlobNotFoundException();

		return blobData.read(path);
	}

	public void deleteBlob(String path) throws IOException {
		if (!blobData.idExists(path))
			throw new BlobNotFoundException();

		blobData.delete(path);
		return;
	}
}
