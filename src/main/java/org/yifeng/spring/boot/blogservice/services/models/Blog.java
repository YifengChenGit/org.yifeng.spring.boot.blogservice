package org.yifeng.spring.boot.blogservice.services.models;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

public class Blog {

	private long id;
	private long createdEpoch;
	private long lastModifiedEpoch;
	
	@NotBlank
	@Size(min = 1, max = 100)
	private String title;
	
	@NotBlank
	@Size(min = 1, max = 10000)
	private String content;

	public Blog(long id, String title, String content, long createdEpoch, long lastModifiedEpoch) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.createdEpoch = createdEpoch;
		this.lastModifiedEpoch = lastModifiedEpoch;
	}

	public Blog(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public long getCreatedEpoch() {
		return createdEpoch;
	}

	public long getLastModifiedEpoch() {
		return lastModifiedEpoch;
	}
}
