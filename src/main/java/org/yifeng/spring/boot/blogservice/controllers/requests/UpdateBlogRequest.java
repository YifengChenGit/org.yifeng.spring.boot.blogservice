package org.yifeng.spring.boot.blogservice.controllers.requests;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateBlogRequest {

	@JsonProperty("title")
	@NotBlank
	@Size(min = 1, max = 100)
	private String title;

	@JsonProperty("content")
	@NotBlank
	@Size(min = 1, max = 10000)
	private String content;

	public UpdateBlogRequest() {

	}

	public UpdateBlogRequest(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}
}
