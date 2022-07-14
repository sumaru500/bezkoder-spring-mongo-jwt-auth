package jwt.eesolutions.com.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class JwtResponse {
	private String accessToken;
	private String type = "Bearer";
	private String id;
	private String username;
	private String email;
	private List<String> roles;
}
