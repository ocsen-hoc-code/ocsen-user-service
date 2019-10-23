package org.ocsen.userservice.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ocsen.userservice.models.BaseTest;
import org.ocsen.userservice.models.facebook.FBData;
import org.ocsen.userservice.models.facebook.FBPicture;
import org.ocsen.userservice.models.facebook.FBUser;
import org.ocsen.userservice.models.ocsen.FilterResponse;
import org.ocsen.userservice.models.ocsen.Token;
import org.ocsen.userservice.models.ocsen.User;
import org.ocsen.userservice.utils.JwtProvider;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserManageControllerTest extends BaseTest {

	String faceBookToken = "I-would-like-find-a-good-job";
	String notExistToken = "Not-exist-Token";

	FBUser fbUser = new FBUser();

	@BeforeEach
	private void init() {
		JwtMock();
	}

	private void setupData() {

		FBPicture picture = new FBPicture();
		FBData data = new FBData();

		data.setHeight(720);
		data.setWidth(720);
		data.setUrl("No-Photo");
		picture.setData(data);
		fbUser.setId(1234567890);
		fbUser.setName("Do Lam Binh Minh");
		fbUser.setPicture(picture);
		register(faceBookToken, fbUser);
	}

	@Test
	public void faceBookLoginSuccessTest() {
		setupData();

		ResponseEntity<Token> token = template.getForEntity(BaseTest.urlLogin + faceBookToken, Token.class);
		String jwt = token.getBody().getToken();
		User user = jwtProvider.getUser(jwt.substring(7, jwt.length()));

		assertEquals(HttpStatus.OK, token.getStatusCode());
		assertEquals(fbUser.getId(), Long.parseLong(user.getUserID()));
		assertEquals(fbUser.getName(), user.getName());
		assertEquals(fbUser.getPicture().getData().getUrl(), user.getAvatar());

	}

	@Test
	public void faceBookLoginFailedTest() {
		ResponseEntity<Token> token = template.getForEntity(BaseTest.urlLogin + notExistToken, Token.class);
		assertEquals(HttpStatus.BAD_REQUEST, token.getStatusCode());
		assertEquals(null, token.getBody().getToken());
	}

	private String logoutTest() {
		setupData();

		ResponseEntity<Token> token = template.getForEntity(BaseTest.urlLogin + faceBookToken, Token.class);
		String jwt = token.getBody().getToken();
		User user = jwtProvider.getUser(jwt.substring(7, jwt.length()));

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", jwt);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<String> result = template.exchange(urlLogout, HttpMethod.DELETE, entity, String.class);

		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertTrue(result.getBody().contains("Logout Success"));
		assertFalse(redisMapper.containsKey(user.getId().toString()));

		return jwt;
	}

	@Test
	public void logoutSuccessTest() {
		logoutTest();
	}

	@Test
	public void logoutJwtInvalidTest() {
		String jwt = logoutTest();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", jwt);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<FilterResponse> result = template.exchange(urlLogout, HttpMethod.DELETE, entity,
				FilterResponse.class);
		FilterResponse filterResponse = result.getBody();
		
		assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
		assertEquals(HttpStatus.FORBIDDEN.value(), filterResponse.getCode());
		assertEquals(JwtProvider.INVALID_MESSAGE, filterResponse.getMessage());
		assertEquals(BaseTest.urlLogout, filterResponse.getPath());
	}
}
