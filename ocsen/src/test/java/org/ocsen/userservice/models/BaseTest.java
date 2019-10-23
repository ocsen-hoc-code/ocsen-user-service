package org.ocsen.userservice.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.ocsen.userservice.models.facebook.FBUser;
import org.ocsen.userservice.models.ocsen.Token;
import org.ocsen.userservice.models.ocsen.User;
import org.ocsen.userservice.repositories.UserCacheRepository;
import org.ocsen.userservice.services.UserService;
import org.ocsen.userservice.utils.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class BaseTest {

	protected static String urlLogin = "/facebook?token=";
	protected static String urlTestPermision = "/v1.0/test";

	@MockBean
	protected RestTemplate restTemplate;
	@MockBean
	UserCacheRepository userCacheRepository;

	@Autowired
	protected TestRestTemplate template;
	@Autowired
	protected JwtProvider JwtProvider;

	protected Map<String, FBUser> tokenMapper = new HashMap<String, FBUser>();
	protected Map<String, UUID> redisMapper = new HashMap<String, UUID>();

	protected void register(String token, FBUser user) {
		tokenMapper.put(token, user);
	}

	protected Token getTokenLoginFB(String url) {
		ResponseEntity<Token> token = template.getForEntity(url, Token.class);
		assertEquals(HttpStatus.OK, token.getStatusCode());
		return token.getBody();
	}

	protected void mockFaceBookApi() {
		when(restTemplate.getForObject(Mockito.anyString(), Mockito.any(Class.class))).then(new Answer<FBUser>() {

			@Override
			public FBUser answer(InvocationOnMock invocation) throws Throwable {
				String faceBookApiUrl = invocation.getArgument(0, String.class);
				String token = faceBookApiUrl.replace(UserService.facebookUrlV4, "");
				return tokenMapper.get(token);
			}
		});
	}

	protected void mockUserCacheRepository() {

		doAnswer(new Answer<UUID>() {

			@Override
			public UUID answer(InvocationOnMock invocation) throws Throwable {
				User user = invocation.getArgument(0, User.class);

				return redisMapper.get(user.getId().toString());
			}
		}).when(userCacheRepository).get(Mockito.any());

		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				User user = invocation.getArgument(0, User.class);
				UUID uuid = invocation.getArgument(1, UUID.class);

				redisMapper.put(user.getId().toString(), uuid);

				return null;
			}
		}).when(userCacheRepository).create(Mockito.any(User.class), Mockito.any(UUID.class));

		doAnswer(new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				User user = invocation.getArgument(0, User.class);

				redisMapper.remove(user.getId().toString());

				return null;
			}
		}).when(userCacheRepository).delete(Mockito.any(User.class));

	}

	protected void JwtMock() {
		mockFaceBookApi();
		mockUserCacheRepository();
	}
}
