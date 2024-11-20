package blobstore;

import blobstore.utils.Hash;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;

public class BlobData {
	private static final String DEFAULT_DIR = "/tmp/blobs/";

	private static BlobData instance;

	public static synchronized BlobData getInstance() {
		if (instance == null)
			instance = new BlobData();
		return instance;
	}

	private BlobData() {
	}

	public byte[] read(String path) throws IOException {
		return Files.readAllBytes(toFile(path).toPath());
	}

	public void write(String path, byte[] bytes) throws IOException {
		File file = toFile(path);
		Files.write(file.toPath(), bytes);
		return;
	}

	public void delete(String path) throws IOException {
		File file = toFile(path);
		Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		return;
	}

	public boolean idExists(String path) {
		File file = toFile(path);
		return file.exists();
	}

	public boolean fileExists(String path, byte[] bytes) throws IOException, NoSuchAlgorithmException {
		File file = toFile(path);
		if (!file.exists())
			return false;

		return Arrays.equals(Hash.sha256(bytes), Hash.sha256(Files.readAllBytes(file.toPath())));
	}

	private File toFile(String path) {
		File res = new File(DEFAULT_DIR + path);

		File parent = res.getParentFile();
		if (!parent.exists())
			parent.mkdirs();

		return res;
	}
}
