package org.ocsen.userservice.models.ocsen;

import io.jsonwebtoken.Claims;

public class JwtResult {
	private Claims claims;
	private int code;

	public JwtResult(Claims claims, int code) {
		this.claims = claims;
		this.code = code;
	}

	public Claims getClaims() {
		return claims;
	}

	public void setClaims(Claims claims) {
		this.claims = claims;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
