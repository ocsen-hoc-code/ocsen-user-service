package org.ocsen.userservice.models.facebook;

public class FBUser {
	private long id;
	private String name;
	private FBPicture picture;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public FBPicture getPicture() {
		return picture;
	}

	public void setPicture(FBPicture picture) {
		this.picture = picture;
	}
}
