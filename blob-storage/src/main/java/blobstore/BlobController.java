package blobstore;

import blobstore.errors.Exceptions.BlobBadRequestException;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Singleton
public class BlobController implements BlobApi {

	private static final BlobService blobService = BlobService.getInstance();

	@Override
	public Response uploadBlob(String blobId, byte[] blob) throws NoSuchAlgorithmException, IOException {
		if (blobId == null || blob == null)
			throw new BlobBadRequestException();

		blobService.uploadBlob(blobId, blob);
		return Response.noContent().build();
	}

	@Override
	public Response deleteBlob(String blobId) throws IOException {
		if (blobId == null)
			throw new BlobBadRequestException();

		blobService.deleteBlob(blobId);
		return Response.noContent().build();
	}

	@Override
	public Response downloadBlob(String blobId) throws IOException {
		if (blobId == null)
			throw new BlobBadRequestException();

		byte[] blob = blobService.downloadBlob(blobId);
		return Response.ok(blob).build();
	}
}
