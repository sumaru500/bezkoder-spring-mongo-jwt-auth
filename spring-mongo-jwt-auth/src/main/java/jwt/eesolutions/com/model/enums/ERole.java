package jwt.eesolutions.com.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ERole {
	user("user"), moderator("moderator"), admin("admin");

	private String label;

}
