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
	private long views;

	public ShortDTO(String shortId, String ownerId, String blobUrl, long timestamp, int totalLikes, int views) {
		this.shortId = shortId;
		this.ownerId = ownerId;
		this.blobUrl = blobUrl;
		this.timestamp = timestamp;
		this.totalLikes = totalLikes;
		this.views = views;
	}

	public ShortDTO(HibernateShort hibernateShort, long totalLikes) {
		this.shortId = hibernateShort.getShortId();
		this.ownerId = hibernateShort.getOwnerId();
		this.blobUrl = hibernateShort.getBlobUrl() + "?token="
				+ Token.get(hibernateShort.getBlobUrl().substring(hibernateShort.getBlobUrl().lastIndexOf('/') + 1));
		this.timestamp = hibernateShort.getTimestamp();
		this.totalLikes = totalLikes;
		this.views = 0;
	}

	public ShortDTO(CosmosDBShort cosmosDBShort) {
		this.shortId = cosmosDBShort.getShortId();
		this.ownerId = cosmosDBShort.getOwnerId();
		this.blobUrl = cosmosDBShort.getBlobUrl() + "?token="
				+ Token.get(cosmosDBShort.getBlobUrl().substring(cosmosDBShort.getBlobUrl().lastIndexOf('/') + 1));
		this.timestamp = cosmosDBShort.getTimestamp();
		this.totalLikes = cosmosDBShort.getTotalLikes();
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

	public long getViews() {
		return views;
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
