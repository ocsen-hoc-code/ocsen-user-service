package org.ocsen.userservice.configs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

//	Constant
	private static String AUTHORIZATION = "Authorization";
	private static String HEADER = "header";
	private static String JWT = "JWT";
	private static String ALL_URL = "/.*";

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("org.ocsen.userservice.controllers")).paths(PathSelectors.any())
				.build().apiInfo(apiInfo()).securityContexts(securityContext()).securitySchemes(apiKey());
	}

	private ApiInfo apiInfo() {
		return new ApiInfo(
				"OcSen Service", "OcSen Sevice Backend", "v1.0.0", "Empty", new Contact("Do Lam Binh Minh",
						"https://www.linkedin.com/in/dolambinhminh/", "dolambinhminh@yahoo.com"),
				"License of Do Lam Binh Minh", "Empty", Collections.emptyList());
	}

	private List<SecurityScheme> apiKey() {
		List<SecurityScheme> list = new ArrayList<SecurityScheme>();
		list.add(new ApiKey(JWT, AUTHORIZATION, HEADER));
		return list;
	}

	private ArrayList<SecurityContext> securityContext() {

		return Lists.newArrayList((SecurityContext.builder().securityReferences(defaultAuth())
				.forPaths(PathSelectors.regex(ALL_URL)).build()));
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Lists.newArrayList(new SecurityReference(JWT, authorizationScopes));
	}
}
