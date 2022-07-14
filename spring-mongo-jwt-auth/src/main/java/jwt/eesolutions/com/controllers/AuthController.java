package jwt.eesolutions.com.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jwt.eesolutions.com.model.Role;
import jwt.eesolutions.com.model.User;
import jwt.eesolutions.com.model.enums.ERole;
import jwt.eesolutions.com.payload.request.LoginRequest;
import jwt.eesolutions.com.payload.request.SignupRequest;
import jwt.eesolutions.com.payload.response.JwtResponse;
import jwt.eesolutions.com.payload.response.MessageResponse;
import jwt.eesolutions.com.repository.RoleRepository;
import jwt.eesolutions.com.repository.UserRepository;
import jwt.eesolutions.com.security.jwt.JwtUtils;
import jwt.eesolutions.com.security.services.impl.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private JwtUtils jwtUtils;

	/**
	 * (1) check existing username/email <br>
	 * (2) create new User (with ROLE_USER if not specifying role) <br>
	 * (3) save User to database using UserRepository <br>
	 * 
	 * @return
	 */
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {
		// check exists by username
		if (userRepository.existsByUsername(signupRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		// check exists by email
		if (userRepository.existsByEmail(signupRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
		}

		// create a new User
		User newUser = new User();
		newUser.setUsername(signupRequest.getUsername());
		newUser.setPassword(encoder.encode(signupRequest.getPassword()));
		newUser.setEmail(signupRequest.getEmail());

		// get roles
		Set<String> providedRoles = signupRequest.getRoles();
		Set<Role> roles = new HashSet<>();
		if (providedRoles == null || providedRoles.isEmpty()) {
			// default is ROLE_USER
			Role userRole = roleRepository.findByName(ERole.user)
					.orElseThrow(() -> new RuntimeException("Error: Role user is not found."));
			roles.add(userRole);
		} else {
			// find provided role in DB
			for (String strRole : providedRoles) {
				ERole eRole = ERole.valueOf(strRole);
				if (eRole == null) {
					eRole = ERole.user;
				}
				String label = eRole.getLabel();
				Role userRole = roleRepository.findByName(eRole)
						.orElseThrow(() -> new RuntimeException("Error: not found Role " + label));
				roles.add(userRole);
			}
		}

		newUser.setRoles(roles);

		// save register user into DB
		userRepository.save(newUser);

		// every thing OK => return OK
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	/**
	 * (1) authenticate { username, pasword } <br>
	 * (2) update SecurityContext using Authentication object <br>
	 * (3) generate JWT <br>
	 * (4) get UserDetails from Authentication object <br>
	 * (5) response contains JWT and UserDetails data <br>
	 * 
	 * @param loginRequest
	 * @return
	 */
	@PostMapping("/signin")
	public ResponseEntity<?> signin(@Valid @RequestBody LoginRequest loginRequest) {
		// authenticate
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		// set authentication object
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// generate token to return
		String jwt = jwtUtils.generateToken(authentication);

		// get UserDetails to build response
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		// return response
		List<String> roles = userDetails.getAuthorities().stream()
				.map(elem -> elem.getAuthority())
				.collect(Collectors.toList());

		JwtResponse jwtResponse = new JwtResponse();
		jwtResponse.setAccessToken(jwt);
		jwtResponse.setUsername(userDetails.getUsername());
		jwtResponse.setEmail(userDetails.getEmail());
		jwtResponse.setRoles(roles);

		return ResponseEntity.ok(jwtResponse);
	}
}
