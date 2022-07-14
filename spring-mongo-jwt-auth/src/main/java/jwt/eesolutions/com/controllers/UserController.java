package jwt.eesolutions.com.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class UserController {
	@GetMapping("/all")
	public String allAccess() {
		return "Public content";
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('user') or hasRole('moderator') or hasRole('admin')")
	public String userAccess() {
		return "User content";
	}

	@GetMapping("/moderator")
	@PreAuthorize("hasRole('moderator')")
	public String moderatorAccess() {
		return "Moderator content";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('admin')")
	public String adminAccess() {
		return "Admin content";
	}
}
