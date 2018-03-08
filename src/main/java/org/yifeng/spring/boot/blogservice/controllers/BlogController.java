package org.yifeng.spring.boot.blogservice.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yifeng.spring.boot.blogservice.controllers.requests.CreateBlogRequest;
import org.yifeng.spring.boot.blogservice.controllers.requests.UpdateBlogRequest;
import org.yifeng.spring.boot.blogservice.controllers.responses.BlogResponse;
import org.yifeng.spring.boot.blogservice.services.BlogService;
import org.yifeng.spring.boot.blogservice.services.models.Blog;

@RestController
@RequestMapping("/blogs")
@Validated
public class BlogController {

	@Autowired
	private BlogService blogService;

	@GetMapping
	public List<BlogResponse> getBlogs() {
		return toBlogResponses(blogService.getBlogs());
	}

	@GetMapping("/{blogId}")
	public BlogResponse getBlog(@Valid @NotNull @PathVariable(value = "blogId") Long blogId) {
		return toBlogResponse(blogService.getBlog(blogId));
	}

	@PostMapping
	public BlogResponse createBlog(@Valid @NotNull @RequestBody CreateBlogRequest createBlogRequest) {
		Blog blog = new Blog(createBlogRequest.getTitle(), createBlogRequest.getContent());
		return toBlogResponse(blogService.createBlog(blog));
	}

	@PutMapping("/{blogId}")
	public BlogResponse updateBlog(
			@Valid @NotNull @PathVariable(value = "blogId") Long blogId,
			@Valid @NotNull @RequestBody UpdateBlogRequest updateBlogRequest) {
		Blog blog = new Blog(updateBlogRequest.getTitle(), updateBlogRequest.getContent());
		return toBlogResponse(blogService.updateBlog(blogId, blog));
	}

	@DeleteMapping("/{blogId}")
	public void deleteBlog(@Valid @NotNull @PathVariable(value = "blogId") Long blogId) {
		blogService.deleteBlog(blogId);
	}

	private List<BlogResponse> toBlogResponses(List<Blog> blogs) {
		return blogs.stream().map(blog -> toBlogResponse(blog)).collect(Collectors.toList());
	}

	private BlogResponse toBlogResponse(Blog blog) {
		return new BlogResponse(
				blog.getId(),
				blog.getTitle(),
				blog.getContent(),
				blog.getCreatedEpoch(),
				blog.getLastModifiedEpoch());
	}
}
