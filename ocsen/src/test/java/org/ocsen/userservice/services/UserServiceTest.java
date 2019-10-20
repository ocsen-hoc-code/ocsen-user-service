package org.ocsen.userservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.ocsen.userservice.models.facebook.FBData;
import org.ocsen.userservice.models.facebook.FBPicture;
import org.ocsen.userservice.models.facebook.FBUser;
import org.ocsen.userservice.models.ocsen.User;
import org.ocsen.userservice.repositories.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {
	@Mock
	private RestTemplate restTemplate;
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private UserService userService = new UserService();

	private static String token = "I-would-like-find-a-good-job";

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
		this.user.setId(UUID.randomUUID());
	}

	@Test
	public void VerifyFacebookTokenSuccessTest() {
		when(userRepository.findByUserIDAndType(Long.toString(fb.getId()), "facebook")).thenReturn(user);
		when(userRepository.save(ArgumentMatchers.any(User.class))).thenReturn(user);
		when(restTemplate.getForObject(UserService.facebookUrlV4 + token, FBUser.class)).thenReturn(fb);
		try {
			User result = userService.VerifyFacebookToken(token);
			assertEquals(user, result);
		} catch (Exception e) {

		}
	}
}
