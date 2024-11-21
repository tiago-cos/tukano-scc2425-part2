package tukano.models;

import jakarta.persistence.Entity;
import utils.Token;

@Entity
public class ShortDTO {

	private String shortId;
	private String ownerId;
	private String blobUrl;
	private long timestamp;
	private long totalLikes;

	public ShortDTO(String shortId, String ownerId, String blobUrl, long timestamp, int totalLikes) {
		this.shortId = shortId;
		this.ownerId = ownerId;
		this.blobUrl = blobUrl;
		this.timestamp = timestamp;
		this.totalLikes = totalLikes;
	}

	public ShortDTO(HibernateShort hibernateShort, long totalLikes) {
		this.shortId = hibernateShort.getShortId();
		this.ownerId = hibernateShort.getOwnerId();
		this.blobUrl = hibernateShort.getBlobUrl() + "?token="
				+ Token.get(hibernateShort.getBlobUrl().substring(hibernateShort.getBlobUrl().lastIndexOf('/') + 1));
		this.timestamp = hibernateShort.getTimestamp();
		this.totalLikes = totalLikes;
	}

	public ShortDTO() {
	}

	public String getShortId() {
		return shortId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public String getBlobUrl() {
		return blobUrl;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public long getTotalLikes() {
		return totalLikes;
	}

	@Override
	public String toString() {
		return "Short [shortId=" + shortId + ", ownerId=" + ownerId + ", blobUrl=" + blobUrl + ", timestamp="
				+ timestamp + ", totalLikes=" + totalLikes + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		ShortDTO other = (ShortDTO) obj;
		return shortId.equals(other.shortId);
	}
}
