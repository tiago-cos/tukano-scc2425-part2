package blobstore.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {
	private static MessageDigest sha256;

	public static byte[] sha256(byte[] data) throws NoSuchAlgorithmException {
		if (sha256 == null)
			sha256 = MessageDigest.getInstance("SHA-256");
		sha256.reset();
		sha256.update(data == null ? new byte[0] : data);
		return sha256.digest();
	}
}
