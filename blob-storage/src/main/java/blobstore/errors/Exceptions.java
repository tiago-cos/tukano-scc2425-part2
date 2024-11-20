package blobstore.errors;

import jakarta.ws.rs.WebApplicationException;

public class Exceptions {

	public static class BlobConflictException extends WebApplicationException {
		public BlobConflictException() {
			super("Blob with that id already exists.", 409);
		}
	}

	public static class BlobNotFoundException extends WebApplicationException {
		public BlobNotFoundException() {
			super("Blob with that id does not exist.", 404);
		}
	}

	public static class BlobBadRequestException extends WebApplicationException {
		public BlobBadRequestException() {
			super("Blob id is not valid.", 400);
		}
	}
}
