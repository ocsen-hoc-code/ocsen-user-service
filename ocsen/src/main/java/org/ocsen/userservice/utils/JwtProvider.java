package org.ocsen.userservice.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ocsen.userservice.models.ocsen.JwtResult;
import org.ocsen.userservice.models.ocsen.Token;
import org.ocsen.userservice.models.ocsen.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtProvider {

	private final Logger log = LogManager.getLogger(JwtProvider.class);

	@Value("${jwt.serect}")
	private String serectKey = "non-sekect-key";

	@Value("${jwt.expiration}")
	private long expiration = 10000L;

	public static final int VERIFY_SUSSCESS_CODE = 0;
	public static final int INVALID_CODE = 1;
	public static final int EXPIRED_CODE = 2;
	public static final int UNSUPPORTED_CODE = 3;
	public static final int CLAIMS_EMPTY_CODE = 4;
	public static final int UNKNOW_CODE = 5;

	// Constant Message
	public static final String VERIFY_SUSSCESS_MESSAGE = "Verify Susscess";
	public static final String INVALID_MESSAGE = "Invalid JWT token";
	public static final String EXPIRED_MESSAGE = "Expired JWT token";
	public static final String UNSUPPORTED_MESSAGE = "Unsupported JWT token";
	public static final String CLAIMS_EMPTY_MESSAGE = "JWT string is empty";
	public static final String UNKNOW_MESSAGE = "Unknow Exception";

	@SuppressWarnings("deprecation")
	public Token tokenCreate(User user, UUID id) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration);
		Map<String, Object> claims = new HashMap<String, Object>();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			byte[] hashInBytes = md.digest(serectKey.getBytes(StandardCharsets.UTF_8));
			claims.put("uuid", id);
			claims.put("user", user);
			String token = Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiryDate)
					.signWith(SignatureAlgorithm.HS512, hashInBytes).compact();
			return new Token(token, now, expiryDate);
		} catch (Exception ex) {
			log.error(ex);
		}

		return null;
	}

	private <T> T convertValue(Object fromValue, Class<T> toValueType) {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.convertValue(fromValue, toValueType);
	}

	private <T> T getItemFromClaim(String token, String itemName, Class<T> toValueType) {
		try {
			Claims claims = getClaimsFromToken(token);

			return convertValue(claims.get(itemName), toValueType);
		} catch (Exception ex) {
			log.error(ex);
		}

		return null;
	}

	private <T> T getItemFromClaim(Claims claims, String itemName, Class<T> toValueType) {
		return convertValue(claims.get(itemName), toValueType);
	}

	public UUID getUUID(String token) {
		return getItemFromClaim(token, "uuid", UUID.class);
	}

	public User getUser(String token) {
		return getItemFromClaim(token, "user", User.class);
	}

	public UUID getUUID(Claims claims) {
		return getItemFromClaim(claims, "uuid", UUID.class);
	}

	public User getUser(Claims claims) {
		return getItemFromClaim(claims, "user", User.class);
	}

	public Claims getClaimsFromToken(String token) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		byte[] hashInBytes = md.digest(serectKey.getBytes(StandardCharsets.UTF_8));

		return Jwts.parser().setSigningKey(hashInBytes).parseClaimsJws(token).getBody();
	}

	public String convertCodeToMasage(int code) {
		switch (code) {
		case VERIFY_SUSSCESS_CODE:
			return VERIFY_SUSSCESS_MESSAGE;
		case INVALID_CODE:
			return INVALID_MESSAGE;
		case EXPIRED_CODE:
			return EXPIRED_MESSAGE;
		case UNSUPPORTED_CODE:
			return UNSUPPORTED_MESSAGE;
		case CLAIMS_EMPTY_CODE:
			return CLAIMS_EMPTY_MESSAGE;
		default:
			return UNKNOW_MESSAGE;
		}
	}

	public JwtResult verify(String token) {
		try {
			Claims claims = getClaimsFromToken(token);

			return new JwtResult(claims, VERIFY_SUSSCESS_CODE);
		} catch (MalformedJwtException invalidException) {
			log.error(invalidException);
			return new JwtResult(null, INVALID_CODE);
		} catch (ExpiredJwtException expiredException) {
			log.error(expiredException);
			return new JwtResult(null, EXPIRED_CODE);
		} catch (UnsupportedJwtException unsupportException) {
			log.error(unsupportException);
			return new JwtResult(null, UNSUPPORTED_CODE);
		} catch (IllegalArgumentException emptyClaimException) {
			log.error(emptyClaimException);
			return new JwtResult(null, CLAIMS_EMPTY_CODE);
		} catch (Exception unknowException) {
			log.error(unknowException);
			return new JwtResult(null, UNKNOW_CODE);
		}

	}
}
