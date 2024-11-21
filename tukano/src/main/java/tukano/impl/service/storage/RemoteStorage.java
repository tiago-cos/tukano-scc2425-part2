package tukano.impl.service.storage;

import static tukano.api.util.Result.ErrorCode.INTERNAL_ERROR;
import static tukano.api.util.Result.ErrorCode.TIMEOUT;
import static tukano.api.util.Result.error;
import static tukano.api.util.Result.ok;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.util.function.Consumer;
import java.util.function.Supplier;
import tukano.api.util.Result;
import tukano.api.util.Result.ErrorCode;
import utils.Sleep;

public class RemoteStorage implements BlobStorage {

	private static final String STORAGE_HOST = System.getenv().getOrDefault("BLOB_STORAGE_HOST", "storage");
	private static final String STORAGE_PORT = System.getenv().getOrDefault("BLOB_STORAGE_PORT", "8081");
	private static final String STORAGE_URL = String.format("http://%s:%s/rest/blobs", STORAGE_HOST, STORAGE_PORT);
	private static final String STORAGE_TOKEN = String.format("Bearer %s", System.getenv("BLOB_STORAGE_TOKEN"));

	protected static final int MAX_RETRIES = 3;
	protected static final int RETRY_SLEEP = 1000;

	private Client client;

	protected RemoteStorage() {
		client = jakarta.ws.rs.client.ClientBuilder.newClient();
	}

	private Result<Void> _write(String path, byte[] bytes) {
		Response response = client
			.target(STORAGE_URL)
			.path(path.replace("/", "%2F"))
			.request()
			.header("Authorization", STORAGE_TOKEN)
			.post(Entity.entity(bytes, MediaType.APPLICATION_OCTET_STREAM_TYPE));

		return toJavaResult(response);
	}

	private Result<byte[]> _read(String path) {
		Response response = client
			.target(STORAGE_URL)
			.path(path.replace("/", "%2F"))
			.request()
			.accept(MediaType.APPLICATION_OCTET_STREAM_TYPE)
			.header("Authorization", STORAGE_TOKEN)
			.get();

		return toJavaResult(response, byte[].class);
	}

	public Result<Void> _delete(String path) {
		Response response = client
			.target(STORAGE_URL)
			.path(path.replace("/", "%2F"))
			.request()
			.header("Authorization", STORAGE_TOKEN)
			.delete();

		return toJavaResult(response);
	}

	@Override
	public Result<Void> write(String path, byte[] bytes) {
		return reTry(() -> _write(path, bytes));
	}

	@Override
	public Result<byte[]> read(String path) {
		return reTry(() -> _read(path));
	}

	@Override
	public Result<Void> delete(String path) {
		return reTry(() -> _delete(path));
	}

	@Override
	public Result<Void> read(String path, Consumer<byte[]> sink) {
		throw new UnsupportedOperationException("Unimplemented method 'read'");
	}

	private <T> Result<T> reTry(Supplier<Result<T>> func) {
		for (int i = 0; i < MAX_RETRIES; i++)
			try {
				return func.get();
			} catch (ProcessingException x) {
				x.printStackTrace();
				Sleep.ms(RETRY_SLEEP);
			} catch (Exception x) {
				x.printStackTrace();
				return Result.error(INTERNAL_ERROR);
			}
		return Result.error(TIMEOUT);
	}

	private Result<Void> toJavaResult(Response r) {
		try {
			Status status = r.getStatusInfo().toEnum();
			if (status == Status.OK && r.hasEntity()) {
				return ok(null);
			} else if (status == Status.NO_CONTENT)
				return ok();

			return error(getErrorCodeFrom(status.getStatusCode()));
		} finally {
			r.close();
		}
	}

	private <T> Result<T> toJavaResult(Response r, Class<T> entityType) {
		try {
			var status = r.getStatusInfo().toEnum();
			if (status == Status.OK && r.hasEntity())
				return ok(r.readEntity(entityType));
			else if (status == Status.NO_CONTENT)
				return ok();

			return error(getErrorCodeFrom(status.getStatusCode()));
		} finally {
			r.close();
		}
	}

	private ErrorCode getErrorCodeFrom(int status) {
		return switch (status) {
			case 200, 204 -> ErrorCode.OK;
			case 409 -> ErrorCode.CONFLICT;
			case 403 -> ErrorCode.FORBIDDEN;
			case 404 -> ErrorCode.NOT_FOUND;
			case 400 -> ErrorCode.BAD_REQUEST;
			case 500 -> ErrorCode.INTERNAL_ERROR;
			case 501 -> ErrorCode.NOT_IMPLEMENTED;
			default -> ErrorCode.INTERNAL_ERROR;
		};
	}
}
