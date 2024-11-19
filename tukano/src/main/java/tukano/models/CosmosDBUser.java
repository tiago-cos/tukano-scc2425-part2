package tukano.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CosmosDBUser {

	@JsonProperty("id")
	private String userId;

	private String pwd;
	private String email;
	private String displayName;
	private List<String> followers;
	private List<String> following;

	public CosmosDBUser(String userId, String pwd, String email, String displayName) {
		this.userId = userId;
		this.pwd = pwd;
		this.email = email;
		this.displayName = displayName;
		this.followers = new ArrayList<>();
		this.following = new ArrayList<>();
		this.following.add("tukanorecommends");
	}

	public CosmosDBUser(UserDTO user) {
		this(user.getUserId(), user.getPwd(), user.getEmail(), user.getDisplayName());
	}

	public CosmosDBUser() {
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<String> getFollowers() {
		return followers;
	}

	public void setFollowers(List<String> followers) {
		this.followers = followers;
	}

	public List<String> getFollowing() {
		return following;
	}

	public void setFollowing(List<String> following) {
		this.following = following;
	}

	public void addFollowing(String userId) {
		following.add(userId);
	}

	public void removeFollowing(String userId) {
		following.remove(userId);
	}

	public void addFollower(String userId) {
		followers.add(userId);
	}

	public void removeFollower(String userId) {
		followers.remove(userId);
	}

	public CosmosDBUser updateFrom(UserDTO other) {
		return new CosmosDBUser(userId, other.getPwd() != null ? other.getPwd() : pwd,
				other.getEmail() != null ? other.getEmail() : email,
				other.getDisplayName() != null ? other.getDisplayName() : displayName);
	}

	public CosmosDBUser secureCopy() {
		return new CosmosDBUser(userId, "", email, displayName);
	}

	@Override
	public String toString() {
		return "users:" + userId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		CosmosDBUser other = (CosmosDBUser) obj;
		return userId.equals(other.userId);
	}
}
