package tukano.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a Short video uploaded by an user.
 *
 * <p>
 * A short has an unique shortId and is owned by a given user; Comprises of a
 * short video, stored as a binary blob at some bloburl;. A post also has a
 * number of likes, which can increase or decrease over time. It is the only
 * piece of information that is mutable. A short is timestamped when it is
 * created.
 */
@Entity
@Table(name = "HibernateShort")
public class HibernateShort {

	@Id
	private String shortId;
	private String ownerId;
	private String blobUrl;
	private long timestamp;

	public HibernateShort(String shortId, String ownerId, String blobUrl, long timestamp, int totalLikes) {
		this.shortId = shortId;
		this.ownerId = ownerId;
		this.blobUrl = blobUrl;
		this.timestamp = timestamp;
	}

	public HibernateShort(String shortId, String ownerId, String blobUrl) {
		this(shortId, ownerId, blobUrl, System.currentTimeMillis(), 0);
	}

	public HibernateShort(ShortDTO shrt) {
		this(shrt.getShortId(), shrt.getOwnerId(), shrt.getBlobUrl(), shrt.getTimestamp(), 0);
	}

	public HibernateShort() {
	}

	public String getShortId() {
		return shortId;
	}

	public void setShortId(String shortId) {
		this.shortId = shortId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getBlobUrl() {
		return blobUrl;
	}

	public void setBlobUrl(String blobUrl) {
		this.blobUrl = blobUrl;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "shorts:" + shortId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		HibernateShort other = (HibernateShort) obj;
		return shortId.equals(other.getShortId());
	}
}
