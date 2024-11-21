package tukano.impl.service.storage;

public class StorageFactory {

	private static BlobStorage instance;

	private StorageFactory() {
	}

	public static synchronized BlobStorage getStorage() {
		if (instance != null)
			return instance;

		String storageType = System.getenv().getOrDefault("BLOB_STORAGE_TYPE", "LOCAL");

		if (storageType.equals("REMOTE"))
			instance = new RemoteStorage();
		else
			instance = new FilesystemStorage();

		return instance;
	}
}
