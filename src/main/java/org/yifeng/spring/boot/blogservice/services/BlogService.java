package org.yifeng.spring.boot.blogservice.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.yifeng.spring.boot.blogservice.repositories.BlogRepository;
import org.yifeng.spring.boot.blogservice.repositories.records.BlogRecord;
import org.yifeng.spring.boot.blogservice.services.models.Blog;

@Service
@Validated
public class BlogService {

	@Autowired
	private BlogRepository blogRepository;

	public List<Blog> getBlogs() {
		return toBlogs(blogRepository.findAll());
	}

	public Blog getBlog(long blogId) {
		return toBlog(getExistBlog(blogId));
	}

	public Blog createBlog(@Valid @NotNull Blog blog) {
		BlogRecord blogRecord = new BlogRecord(blog.getTitle(), blog.getContent());
		return toBlog(blogRepository.save(blogRecord));
	}

	public Blog updateBlog(long blogId, @Valid @NotNull Blog blog) {
		BlogRecord blogRecord = getExistBlog(blogId);
		blogRecord.setTitle(blog.getTitle());
		blogRecord.setContent(blog.getContent());
		return toBlog(blogRepository.save(blogRecord));
	}

	public void deleteBlog(long blogId) {
		try {
			blogRepository.delete(blogId);
		} catch (EmptyResultDataAccessException e) {
			throw new IllegalArgumentException("No blog with id: " + blogId, e);
		}
	}

	private BlogRecord getExistBlog(long blogId) {
		BlogRecord blogRecord = blogRepository.findOne(blogId);
		if (blogRecord == null) {
			throw new IllegalArgumentException("No blog with id: " + blogId);
		}
		return blogRecord;
	}

	private List<Blog> toBlogs(List<BlogRecord> blogRecords) {
		return blogRecords.stream().map(blogRecord -> toBlog(blogRecord)).collect(Collectors.toList());
	}

	private Blog toBlog(BlogRecord blogRecord) {
		return new Blog(
			blogRecord.getId(),
			blogRecord.getTitle(),
			blogRecord.getContent(),
			blogRecord.getCreatedDate().getTime(),
			blogRecord.getLastModifiedDate().getTime());
	}
}
