package org.ocsen.userservice.models.ocsen;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Filter authorization message")
public class FilterResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3420827648377284395L;

	@ApiModelProperty(notes = "Code {401, 403}")
	private int code;
	@ApiModelProperty(notes = "Message rootcause issue")
	private String message;
	@ApiModelProperty(notes = "Path have issue")
	private String path;

	public FilterResponse(int code, String message, String path) {
		this.code = code;
		this.message = message;
		this.path = path;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
