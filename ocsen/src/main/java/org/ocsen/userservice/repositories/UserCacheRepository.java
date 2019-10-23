package org.ocsen.userservice.repositories;

import java.util.Date;
import java.util.UUID;

import org.ocsen.userservice.models.ocsen.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserCacheRepository {
	@Value("${jwt.expiration}")
	private long expiration = 10000L;

	@Autowired
	StringRedisTemplate userRedisTemplate;

	public UUID get(User user) {
		String value = userRedisTemplate.opsForValue().get(user.getId().toString());

		if (null == value) {
			return null;
		}

		return UUID.fromString(value);
	}

	public void create(User user, UUID value) {
		if (null == get(user)) {
			add(user, value);
		} else {
			update(user, value);
		}
	}

	public void delete(User user) {
		userRedisTemplate.delete(user.getId().toString());
	}

	private void add(User user, UUID value) {
		userRedisTemplate.opsForValue().setIfAbsent(user.getId().toString(), value.toString());
		userRedisTemplate.expireAt(user.getId().toString(), new Date(new Date().getTime() + expiration));
	}

	private void update(User user, UUID value) {
		userRedisTemplate.opsForValue().set(user.getId().toString(), value.toString());
		userRedisTemplate.expireAt(user.getId().toString(), new Date(new Date().getTime() + expiration));
	}

}
