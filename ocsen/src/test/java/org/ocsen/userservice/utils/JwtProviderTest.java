package org.ocsen.userservice.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ocsen.userservice.models.ocsen.JwtResult;
import org.ocsen.userservice.models.ocsen.Token;
import org.ocsen.userservice.models.ocsen.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.RsaProvider;

@SpringBootTest
@ActiveProfiles("test")
public class JwtProviderTest {

	@Value("${jwt.serect}")
	private String serectKey = "";

	@Value("${jwt.expiration}")
	private long expiration = 10000L;

	@Autowired
	JwtProvider jwtProvider;

	private User user = new User();

	@BeforeEach
	private void Init() {
		user.setId(UUID.randomUUID());
		user.setUserID(new Random().longs(1000000000, 99999999999L).toString());
		user.setName("Do Lam Binh Minh");
		user.setAvatar("No-Photo");
		user.setType("facebook");
	}

	@Test
	public void jwtVerifySuccessTest() {
		UUID id = UUID.randomUUID();
		Token token = jwtProvider.tokenCreate(user, id);
		JwtResult jwtResult = jwtProvider.verify(token.getToken().replace("Bearer ", ""));

		assertEquals(JwtProvider.VERIFY_SUSSCESS_CODE, jwtResult.getCode());
		assertNotNull(jwtResult.getClaims());

		User jwtUser = jwtProvider.getUser(jwtResult.getClaims());
		UUID jwtUUID = jwtProvider.getUUID(jwtResult.getClaims());

		assertEquals(id, jwtUUID);
		assertEquals(user.getId(), jwtUser.getId());
		assertEquals(user.getUserID(), jwtUser.getUserID());
		assertEquals(user.getName(), jwtUser.getName());
		assertEquals(user.getType(), jwtUser.getType());
		assertEquals(user.getAvatar(), jwtUser.getAvatar());
	}

	@Test
	public void jwtVerifyExpiredTest() {
		jwtProvider.setExpiration(1);
		UUID id = UUID.randomUUID();
		Token token = jwtProvider.tokenCreate(user, id);
		JwtResult jwtResult = jwtProvider.verify(token.getToken().replace("Bearer ", ""));

		assertEquals(JwtProvider.EXPIRED_CODE, jwtResult.getCode());
		assertNull(jwtResult.getClaims());
	}

	@Test
	public void jwtVerifyInvalidTest() {
		UUID id = UUID.randomUUID();
		Token token = jwtProvider.tokenCreate(user, id);
		String newToken = token.getToken().replace("Bearer ", "");
		Character invalidChar = newToken.charAt(30);
		String invalidValue = "";

		// Create invalid token
		if (Character.isUpperCase(invalidChar)) {
			invalidValue = invalidChar.toString().toLowerCase();
		} else {
			invalidValue = invalidChar.toString().toUpperCase();
		}

		newToken = newToken.substring(0, 29) + invalidValue + newToken.substring(30, newToken.length());

		JwtResult jwtResult = jwtProvider.verify(newToken);

		assertEquals(JwtProvider.INVALID_CODE, jwtResult.getCode());
		assertNull(jwtResult.getClaims());
	}

	@Test
	public void jwtVerifyUnsupportTest() {
		JwtResult jwtResult = jwtProvider.verify("");
		
		assertEquals(JwtProvider.UNSUPPORTED_CODE, jwtResult.getCode());
		assertNull(jwtResult.getClaims());
		
		jwtResult = jwtProvider.verify("01234567890123456789");
		
		assertEquals(JwtProvider.INVALID_CODE, jwtResult.getCode());
		assertNull(jwtResult.getClaims());
	}

	@Test
	public void jwtVerifyIllegalArgumentTest() throws NoSuchAlgorithmException {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration);
		Key key = RsaProvider.generateKeyPair(4096).getPrivate();
		
		String jwt = Jwts.builder().setClaims(null).setExpiration(expiryDate).signWith(SignatureAlgorithm.RS512, key)
				.compact();

		JwtResult jwtResult = jwtProvider.verify(jwt);

		assertEquals(JwtProvider.ILLEGAL_ARGUMENT_CODE, jwtResult.getCode());
		assertNull(jwtResult.getClaims());
	}

	@Test
	public void jwtVerifyUnknowTest() {
		UUID id = UUID.randomUUID();
		Token token = jwtProvider.tokenCreate(user, id);
		JwtResult jwtResult = jwtProvider.verify("Unknow" + token.getToken());
		assertEquals(JwtProvider.UNKNOW_CODE, jwtResult.getCode());
		assertNull(jwtResult.getClaims());
	}
}
