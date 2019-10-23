package org.ocsen.userservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ocsen.userservice.models.facebook.FBData;
import org.ocsen.userservice.models.facebook.FBPicture;
import org.ocsen.userservice.models.facebook.FBUser;
import org.ocsen.userservice.models.ocsen.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
	@MockBean
	private RestTemplate restTemplate;
	@Autowired
	private UserService userService;

	private static String faceBookToken = "I-would-like-find-a-good-job";

	private FBUser fb;
	private User user;

	@BeforeEach
	public void Init() {
		this.fb = new FBUser();
		FBPicture picture = new FBPicture();
		FBData data = new FBData();
		data.setHeight(720);
		data.setWidth(720);
		data.setUrl("");
		picture.setData(data);
		this.fb.setId(1234567890);
		this.fb.setName("Minh Dep Trai");
		this.fb.setPicture(picture);
		this.user = new User(Long.toString(fb.getId()), fb.getName(), fb.getPicture().getData().getUrl(), "facebook");
	}

	@Test
	public void VerifyFacebookTokenSuccessTest() {		
		when(restTemplate.getForObject(UserService.facebookUrlV4 + faceBookToken, FBUser.class)).thenReturn(fb);
		try {
			User result = userService.VerifyFacebookToken(faceBookToken);
			
			assertNotNull(result);
			assertEquals(user.getUserID(), result.getUserID());
			assertEquals(user.getAvatar(), result.getAvatar());
			assertEquals(user.getName(), result.getName());
			assertEquals(user.getType(), result.getType());
			
		} catch (Exception e) {

		}
	}
	
	@Test
	public void VerifyFacebookTokenFailedTest() {		
		when(restTemplate.getForObject(UserService.facebookUrlV4 + faceBookToken, FBUser.class)).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));
		
		try {			
			userService.VerifyFacebookToken(faceBookToken);
			
		} catch (Exception e) {
			assertEquals(e.getMessage(), "Verify failed!");
		}
	}
}
