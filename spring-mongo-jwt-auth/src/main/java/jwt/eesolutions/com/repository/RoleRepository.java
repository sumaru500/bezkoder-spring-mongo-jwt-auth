package jwt.eesolutions.com.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import jwt.eesolutions.com.model.Role;
import jwt.eesolutions.com.model.enums.ERole;

public interface RoleRepository extends MongoRepository<Role, String> {
	Optional<Role> findByName(ERole name);
}
