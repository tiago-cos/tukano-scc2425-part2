package tukano.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CosmosDBShort {

	@JsonProperty("id")
	private String shortId;

	private String ownerId;
	private String blobUrl;
	private long timestamp;
	private List<String> likedBy;
	private long views;

	public CosmosDBShort(String shortId, String ownerId, String blobUrl) {
		this.shortId = shortId;
		this.ownerId = ownerId;
		this.blobUrl = blobUrl;
		this.timestamp = System.currentTimeMillis();
		this.likedBy = new ArrayList<>();
		this.views = 0;
	}

	public CosmosDBShort(ShortDTO shortDTO) {
		this.shortId = shortDTO.getShortId();
		this.ownerId = shortDTO.getOwnerId();
		this.blobUrl = shortDTO.getBlobUrl();
		this.timestamp = shortDTO.getTimestamp();
		this.likedBy = new ArrayList<>();
		this.views = shortDTO.getViews();
	}

	public CosmosDBShort() {
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

	public List<String> getLikedBy() {
		return likedBy;
	}

	public void setLikedBy(List<String> likedBy) {
		this.likedBy = likedBy;
	}

	public int getTotalLikes() {
		return likedBy.size();
	}

	public void addLike(String userId) {
		likedBy.add(userId);
	}

	public void removeLike(String userId) {
		likedBy.remove(userId);
	}

	public long getViews() {
		return views;
	}

	public void setViews(long views) {
		this.views = views;
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
		CosmosDBShort other = (CosmosDBShort) obj;
		return shortId.equals(other.getShortId());
	}
}
