package blobstore.errors;

import blobstore.errors.Exceptions.BlobBadRequestException;
import blobstore.errors.Exceptions.BlobConflictException;
import blobstore.errors.Exceptions.BlobNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

public class ExceptionHandlers {

	@Provider
	public static class BlobConflictExceptionHandler implements ExceptionMapper<BlobConflictException> {
		@Override
		public Response toResponse(BlobConflictException exception) {
			return exception.getResponse();
		}
	}

	@Provider
	public static class BlobNotFoundExceptionHandler implements ExceptionMapper<BlobNotFoundException> {
		@Override
		public Response toResponse(BlobNotFoundException exception) {
			return exception.getResponse();
		}
	}

	@Provider
	public static class BlobBadRequestExceptionHandler implements ExceptionMapper<BlobBadRequestException> {
		@Override
		public Response toResponse(BlobBadRequestException exception) {
			return exception.getResponse();
		}
	}

	@Provider
	public static class DefaultExceptionHandler implements ExceptionMapper<Exception> {
		@Override
		public Response toResponse(Exception exception) {
			return Response.status(500).entity("Internal server error.").build();
		}
	}
}
