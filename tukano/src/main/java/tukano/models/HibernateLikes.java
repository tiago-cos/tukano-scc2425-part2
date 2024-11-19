package tukano.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "Likes")
public class HibernateLikes {

	@Id
	String userId;
	@Id
	String shortId;
	String ownerId;

	public HibernateLikes(String userId, String shortId, String ownerId) {
		this.userId = userId;
		this.shortId = shortId;
		this.ownerId = ownerId;
	}

	public HibernateLikes() {
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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

	@Override
	public int hashCode() {
		return Objects.hash(ownerId, shortId, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HibernateLikes other = (HibernateLikes) obj;
		return ownerId.equals(other.ownerId) && shortId.equals(other.shortId) && userId.equals(other.userId);
	}

	@Override
	public String toString() {
		return "Likes [userId=" + userId + ", shortId=" + shortId + ", ownerId=" + ownerId + "]";
	}
}
