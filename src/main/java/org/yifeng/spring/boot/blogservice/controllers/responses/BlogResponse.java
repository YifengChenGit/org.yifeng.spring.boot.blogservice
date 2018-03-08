package org.yifeng.spring.boot.blogservice.controllers.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlogResponse {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("content")
	private String content;

	@JsonProperty("createdDate")
	private Long createdDate;

	@JsonProperty("lastModifiedDate")
	private Long lastModifiedDate;

	public BlogResponse(Long id, String title, String content, Long createdDate, Long lastModifiedDate) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.createdDate = createdDate;
		this.lastModifiedDate = lastModifiedDate;
	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public Long getLastModifiedDate() {
		return lastModifiedDate;
	}
}
