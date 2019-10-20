package org.ocsen.userservice.filters;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ocsen.userservice.models.ocsen.JwtResult;
import org.ocsen.userservice.models.ocsen.User;
import org.ocsen.userservice.repositories.UserCacheRepository;
import org.ocsen.userservice.utils.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@WebFilter(urlPatterns = { "/v1.0/*" })
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtFilter implements Filter {

	private static final Logger log = LogManager.getLogger(JwtFilter.class);

	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private UserCacheRepository userCacheRepository;

	private final String MISSING_AUTHORIZATION = "Missing Authorization!";
	private final String ACCESS_DENIED = "Access Denied!ss";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		try {

			String token = getTokenFromRequest(req);

			if (null == token) {
				responseFilter(req, res, HttpStatus.UNAUTHORIZED, this.MISSING_AUTHORIZATION);
			} else {
				JwtResult jwtResult = jwtProvider.verify(token);

				if (jwtResult.getCode() == JwtProvider.VERIFY_SUSSCESS_CODE && null != jwtResult.getClaims()) {

					UUID uuid = jwtProvider.getUUID(jwtResult.getClaims());
					User user = jwtProvider.getUser(jwtResult.getClaims());

					if (null == uuid || null == user) {
						responseFilter(req, res, HttpStatus.FORBIDDEN, JwtProvider.UNSUPPORTED_MESSAGE);
					} else {
						if (uuid.equals(userCacheRepository.get(user))) {
							request.setAttribute("uuid", uuid);
							request.setAttribute("user", user);

							chain.doFilter(request, response);
						} else {
							responseFilter(req, res, HttpStatus.FORBIDDEN, JwtProvider.INVALID_MESSAGE);
						}
					}

				} else {
					responseFilter(req, res, HttpStatus.FORBIDDEN,
							jwtProvider.convertCodeToMasage(jwtResult.getCode()));
				}
			}

		} catch (Exception ex) {
			log.error(ex);
			responseFilter(req, res, HttpStatus.FORBIDDEN, this.ACCESS_DENIED);
		}

	}

	private String getTokenFromRequest(HttpServletRequest request) {
		String token = request.getHeader("Authorization");

		if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			return token.substring(7, token.length());
		}

		return null;
	}

	private void responseFilter(HttpServletRequest request, HttpServletResponse response, HttpStatus status,
			String message) {
		try {
			response.setStatus(status.value());
			response.setContentType("application/json; charset=utf-8");
			response.getWriter().write("{\"Code\":" + status.value() + ",\"mesage:\":\"" + message + "\",\"path\":\""
					+ request.getRequestURI() + "\"}");
		} catch (IOException ex) {
			log.error(ex);
		}
	}
}
