package org.ocsen.userservice.models.ocsen;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Information of Token when register user on OcSen service")
public class Token implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8487221372509945476L;

	@ApiModelProperty(notes = "JWT contain user information")
	private String token;
	@ApiModelProperty(notes = "Token time created")
	private Date createAt;
	@ApiModelProperty(notes = "Token time expired")
	private Date expired;

	public Token(String token) {
		this.token = token;
	}

	public Token(String token, Date createAt, Date expired) {
		this.token = token;
		this.createAt = createAt;
		this.expired = expired;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Date getExpired() {
		return expired;
	}

	public void setExpired(Date expired) {
		this.expired = expired;
	}

}
