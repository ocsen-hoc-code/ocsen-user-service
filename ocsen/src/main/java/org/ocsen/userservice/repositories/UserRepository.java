package org.ocsen.userservice.repositories;

import java.util.UUID;

import org.ocsen.userservice.models.ocsen.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
	User findByUserIDAndType(String userID, String type);
}