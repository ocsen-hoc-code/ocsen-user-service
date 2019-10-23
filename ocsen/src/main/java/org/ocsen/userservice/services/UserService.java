package org.ocsen.userservice.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ocsen.userservice.models.facebook.FBUser;
import org.ocsen.userservice.models.ocsen.User;
import org.ocsen.userservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserService {

	private static final Logger log = LogManager.getLogger(UserService.class);
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RestTemplate restTemplate;

	public static final String facebookUrlV4 = "https://graph.facebook.com/v4.0/me?fields=id,name,picture.width(720).height(720)&access_token=";

	public User VerifyFacebookToken(String token) throws Exception {
		try {
			FBUser fb = restTemplate.getForObject(facebookUrlV4 + token, FBUser.class);
			User user = new User(Long.toString(fb.getId()), fb.getName(), fb.getPicture().getData().getUrl(),
					"facebook");
			User userExist = userRepository.findByUserIDAndType(Long.toString(fb.getId()), "facebook");
			if (null != userExist) {
				user.setId(userExist.getId());
			}

			user = userRepository.save(user);
			return user;
		} catch (Exception ex) {
			log.error(ex);
			throw new Exception("Verify failed!");
		}
	}

	public User Find(User user) throws Exception {
		try {
			User rs = userRepository.findByUserIDAndType(user.getUserID(), user.getType());
			return rs;
		} catch (Exception ex) {
			log.error(ex);
			throw new Exception("User not exist!");
		}
	}
}
