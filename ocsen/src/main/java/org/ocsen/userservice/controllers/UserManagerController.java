package org.ocsen.userservice.controllers;

import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ocsen.userservice.models.ocsen.Token;
import org.ocsen.userservice.models.ocsen.User;
import org.ocsen.userservice.repositories.UserCacheRepository;
import org.ocsen.userservice.services.UserService;
import org.ocsen.userservice.utils.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class UserManagerController {
	private static final Logger log = LogManager.getLogger(UserManagerController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private UserCacheRepository userCacheRepository;

	@ApiOperation(value = "Register User On OcSen Service By Facebook Token", response = Token.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Register Success!"),
			@ApiResponse(code = 400, message = "Register Fail!"), })
	@GetMapping("/facebook")
	public Token Login(@RequestParam("token") String token) {
		try {
			User user = userService.VerifyFacebookToken(token);
			UUID uuid = UUID.randomUUID();
			userCacheRepository.create(user, uuid);
			return jwtProvider.tokenCreate(user, uuid);
		} catch (Exception ex) {
			log.error(ex);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't verify Token!", null);
		}
	}

	@ApiOperation(value = "Logout On OcSen Service", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Logut Success!"),
			@ApiResponse(code = 401, message = "Missing Authorization!"),
			@ApiResponse(code = 403, message = "Access denied!"), })

	
	@DeleteMapping("/v1.0/logout")
	public String Logout(@ApiIgnore @RequestAttribute("user") User user) {
		userCacheRepository.delete(user);
		return "Logout Success";
	}
}
