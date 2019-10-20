package org.ocsen.userservice.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
public class HomeController {

	@ApiOperation(value = "Home Page", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Welcome to OcSen! ^.^"),
			@ApiResponse(code = 404, message = "Page not found!"), })
	@GetMapping("/")
	public String index() {

		return "index";
	}

	@ApiOperation(value = "Test permission", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Welcome to OcSen! ^.^"),
			@ApiResponse(code = 401, message = "Missing Authorization!"),
			@ApiResponse(code = 403, message = "Access denied!"), })
	@GetMapping("/v1.0/test")
	public String permission() {

		return "index";
	}
}
