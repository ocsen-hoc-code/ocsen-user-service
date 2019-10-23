package org.ocsen.userservice.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.ocsen.userservice.filters.JwtFilter;
import org.ocsen.userservice.models.BaseTest;
import org.ocsen.userservice.models.facebook.FBData;
import org.ocsen.userservice.models.facebook.FBPicture;
import org.ocsen.userservice.models.facebook.FBUser;
import org.ocsen.userservice.models.ocsen.FilterResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HomeControllerTest extends BaseTest {

	String faceBookToken = "I-would-like-find-a-good-job";

	@Autowired
	private TestRestTemplate template;

	@Test
	public void homeTest() throws Exception {
		ResponseEntity<String> result = template.getForEntity("/", String.class);
		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertTrue(result.getBody().contains("Welcome to Ocsen"));
	}

	@Test
	public void permissionWithoutJwtTest() {
		String url = "/v1.0/test";
		ResponseEntity<FilterResponse> result = template.getForEntity(url, FilterResponse.class);
		FilterResponse filterResponse = result.getBody();
		assertEquals(HttpStatus.UNAUTHORIZED.value(), filterResponse.getCode());
		assertEquals(JwtFilter.MISSING_AUTHORIZATION, filterResponse.getMessage());
		assertEquals(url, filterResponse.getPath());
	}

	private void setupData() {
		FBUser fb = new FBUser();
		FBPicture picture = new FBPicture();
		FBData data = new FBData();

		data.setHeight(720);
		data.setWidth(720);
		data.setUrl("");
		picture.setData(data);
		fb.setId(1234567890);
		fb.setName("Do Lam Binh Minh");
		fb.setPicture(picture);

		register(faceBookToken, fb);
	}

	@Test
	public void permissionWithJwtTest() {
		JwtMock();
		setupData();

		String jwt = getTokenLoginFB(BaseTest.urlLogin + faceBookToken).getToken();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", jwt);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		ResponseEntity<String> result = template.exchange(urlTestPermision, HttpMethod.GET, entity, String.class);
		assertEquals(HttpStatus.OK, result.getStatusCode());
		assertTrue(result.getBody().contains("Welcome to Ocsen"));
	}
}
