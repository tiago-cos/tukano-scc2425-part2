package tukano.models;

import jakarta.persistence.Entity;

@Entity
public class UserDTO {

	private String userId;
	private String pwd;
	private String email;
	private String displayName;

	public UserDTO(String userId, String pwd, String email, String displayName) {
		this.userId = userId;
		this.pwd = pwd;
		this.email = email;
		this.displayName = displayName;
	}

	public UserDTO(HibernateUser hibernateUser) {
		this.userId = hibernateUser.getUserId();
		this.pwd = hibernateUser.getPwd();
		this.email = hibernateUser.getEmail();
		this.displayName = hibernateUser.getDisplayName();
	}

	public UserDTO() {
	}

	public String getUserId() {
		return userId;
	}

	public String getPwd() {
		return pwd;
	}

	public String getEmail() {
		return email;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", pwd=" + pwd + ", email=" + email + ", displayName=" + displayName + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		UserDTO other = (UserDTO) obj;
		return userId.equals(other.userId);
	}
}
