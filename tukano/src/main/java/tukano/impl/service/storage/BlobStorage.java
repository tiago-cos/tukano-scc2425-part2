package tukano.impl.service.storage;

import java.util.function.Consumer;
import tukano.api.util.Result;

public interface BlobStorage {

	public Result<Void> write(String path, byte[] bytes);

	public Result<Void> delete(String path);

	public Result<byte[]> read(String path);

	public Result<Void> read(String path, Consumer<byte[]> sink);
}
