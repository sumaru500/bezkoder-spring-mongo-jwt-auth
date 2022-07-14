package jwt.eesolutions.com.payload.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SignupRequest extends LoginRequest {
	@NotBlank
	@Size(max = 50)
	@Email
	private String email;
	private Set<String> roles;
}
