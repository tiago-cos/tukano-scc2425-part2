package tukano.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "HibernateUser")
public class HibernateUser {

	@Id
	private String userId;
	private String pwd;
	private String email;
	private String displayName;

	public HibernateUser(String userId, String pwd, String email, String displayName) {
		this.pwd = pwd;
		this.email = email;
		this.userId = userId;
		this.displayName = displayName;
	}

	public HibernateUser(UserDTO user) {
		this(user.getUserId(), user.getPwd(), user.getEmail(), user.getDisplayName());
	}

	public HibernateUser() {
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

	public HibernateUser updateFrom(UserDTO other) {
		return new HibernateUser(userId, other.getPwd() != null ? other.getPwd() : pwd,
				other.getEmail() != null ? other.getEmail() : email,
				other.getDisplayName() != null ? other.getDisplayName() : displayName);
	}

	public HibernateUser secureCopy() {
		return new HibernateUser(userId, "", email, displayName);
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
		HibernateUser other = (HibernateUser) obj;
		return userId.equals(other.userId);
	}
}
