package org.yifeng.spring.boot.blogservice.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.yifeng.spring.boot.blogservice.repositories.BlogRepository;
import org.yifeng.spring.boot.blogservice.repositories.records.BlogRecord;
import org.yifeng.spring.boot.blogservice.services.models.Blog;

@RunWith(SpringRunner.class)
public class BlogServiceTest {

	@Configuration
	public static class BlogServiceTestConfiguration {

		@Bean
		public BlogService getBlogService() {
			return new BlogService();
		}

		@Bean
		public MethodValidationPostProcessor getMethodValidationPostProcessor() {
			return new MethodValidationPostProcessor();
		}
	}

	@Autowired
	private BlogService blogService;

	@MockBean
	private BlogRepository blogRepository;

	@Test
	public void whenGetBlogs_inEmptyBlogRepository_returnEmptyBlogs() {
		when(blogRepository.findAll()).thenReturn(Collections.emptyList());
		List<Blog> blogs = blogService.getBlogs();
		assertNotNull(blogs);
		assertEquals(blogs.size(), 0);
	}

	@Test
	public void whenGetBlogs_inNonemptyBlogRepository_returnBlogs() {

		BlogRecord expectedBlogRecordA = mock(BlogRecord.class);
		when(expectedBlogRecordA.getId()).thenReturn(1L);
		when(expectedBlogRecordA.getTitle()).thenReturn("TitleA");
		when(expectedBlogRecordA.getContent()).thenReturn("ContentA");
		when(expectedBlogRecordA.getCreatedDate()).thenReturn(new Date());
		when(expectedBlogRecordA.getLastModifiedDate()).thenReturn(new Date());
		BlogRecord expectedBlogRecordB = mock(BlogRecord.class);
		when(expectedBlogRecordB.getId()).thenReturn(2L);
		when(expectedBlogRecordB.getTitle()).thenReturn("TitleB");
		when(expectedBlogRecordB.getContent()).thenReturn("ContentB");
		when(expectedBlogRecordB.getCreatedDate()).thenReturn(new Date());
		when(expectedBlogRecordB.getLastModifiedDate()).thenReturn(new Date());
		when(blogRepository.findAll()).thenReturn(Arrays.asList(expectedBlogRecordA, expectedBlogRecordB));

		List<Blog> actualBlogs = blogService.getBlogs();
		assertNotNull(actualBlogs);
		assertEquals(2, actualBlogs.size());
		assertNotNull(actualBlogs.get(0));
		assertEquals(expectedBlogRecordA.getId().longValue(), actualBlogs.get(0).getId());
		assertEquals(expectedBlogRecordA.getTitle(), actualBlogs.get(0).getTitle());
		assertEquals(expectedBlogRecordA.getContent(), actualBlogs.get(0).getContent());
		assertEquals(expectedBlogRecordA.getCreatedDate().getTime(), actualBlogs.get(0).getCreatedEpoch());
		assertEquals(expectedBlogRecordA.getLastModifiedDate().getTime(), actualBlogs.get(0).getLastModifiedEpoch());
		assertNotNull(actualBlogs.get(1));
		assertEquals(expectedBlogRecordB.getId().longValue(), actualBlogs.get(1).getId());
		assertEquals(expectedBlogRecordB.getTitle(), actualBlogs.get(1).getTitle());
		assertEquals(expectedBlogRecordB.getContent(), actualBlogs.get(1).getContent());
		assertEquals(expectedBlogRecordB.getCreatedDate().getTime(), actualBlogs.get(1).getCreatedEpoch());
		assertEquals(expectedBlogRecordB.getLastModifiedDate().getTime(), actualBlogs.get(1).getLastModifiedEpoch());
	}

	@Test(expected = IllegalArgumentException.class)
	public void whenGetBlog_withInvalidBlogId_throwIllegalArgumentException() {
		when(blogRepository.findOne(1L)).thenReturn(null);
		blogService.getBlog(1L);
	}

	@Test
	public void whenGetBlog_withValidBlogId_returnBlog() {

		BlogRecord expectedBlogRecord = mock(BlogRecord.class);
		when(expectedBlogRecord.getId()).thenReturn(1L);
		when(expectedBlogRecord.getTitle()).thenReturn("Title");
		when(expectedBlogRecord.getContent()).thenReturn("Content");
		when(expectedBlogRecord.getCreatedDate()).thenReturn(new Date());
		when(expectedBlogRecord.getLastModifiedDate()).thenReturn(new Date());
		when(blogRepository.findOne(expectedBlogRecord.getId())).thenReturn(expectedBlogRecord);

		Blog actualBlog = blogService.getBlog(expectedBlogRecord.getId());
		assertNotNull(actualBlog);
		assertEquals(expectedBlogRecord.getId().longValue(), actualBlog.getId());
		assertEquals(expectedBlogRecord.getTitle(), actualBlog.getTitle());
		assertEquals(expectedBlogRecord.getContent(), actualBlog.getContent());
		assertEquals(expectedBlogRecord.getCreatedDate().getTime(), actualBlog.getCreatedEpoch());
		assertEquals(expectedBlogRecord.getLastModifiedDate().getTime(), actualBlog.getLastModifiedEpoch());
	}

	@Test(expected = ConstraintViolationException.class)
	public void whenCreateBlog_withNullBlog_throwConstraintViolationException() {
		blogService.createBlog(null);
	}

	@Test(expected = ConstraintViolationException.class)
	public void whenCreateBlog_withInvalidBlog_throwConstraintViolationException() {

		String validTitle = "Title";
		Set<String> titles = getInvalidTitles();
		titles.add(validTitle);

		String validContent = "Content";
		Set<String> contents = getInvalidContents();
		contents.add(validContent);

		for (String title : getInvalidTitles()) {
			for (String content : getInvalidContents()) {
				if (!validTitle.equals(title) || !validContent.equals(content)) {
					blogService.createBlog(new Blog(title, content));
				}
			}
		}
	}

	@Test
	public void whenCreateBlog_withValidBlog_returnBlog() {

		BlogRecord expectedBlogRecord = mock(BlogRecord.class);
		when(expectedBlogRecord.getId()).thenReturn(1L);
		when(expectedBlogRecord.getTitle()).thenReturn("Title");
		when(expectedBlogRecord.getContent()).thenReturn("Content");
		when(expectedBlogRecord.getCreatedDate()).thenReturn(new Date());
		when(expectedBlogRecord.getLastModifiedDate()).thenReturn(new Date());
		when(blogRepository.save(any(BlogRecord.class))).thenReturn(expectedBlogRecord);

		Blog actualBlog = blogService.createBlog(new Blog("Title", "Content"));
		assertNotNull(actualBlog);
		assertEquals(expectedBlogRecord.getId().longValue(), actualBlog.getId());
		assertEquals(expectedBlogRecord.getTitle(), actualBlog.getTitle());
		assertEquals(expectedBlogRecord.getContent(), actualBlog.getContent());
		assertEquals(expectedBlogRecord.getCreatedDate().getTime(), actualBlog.getCreatedEpoch());
		assertEquals(expectedBlogRecord.getLastModifiedDate().getTime(), actualBlog.getLastModifiedEpoch());
	}

	@Test(expected = ConstraintViolationException.class)
	public void whenUpdateBlog_withInvalidBlogIdAndNullBlog_throwConstraintViolationException() {
		when(blogRepository.findOne(1L)).thenReturn(null);
		blogService.updateBlog(1L, null);
	}

	@Test(expected = ConstraintViolationException.class)
	public void whenUpdateBlog_withInvalidBlogIdAndInvalidBlog_throwConstraintViolationException() {
		
		when(blogRepository.findOne(1L)).thenReturn(null);
		
		String validTitle = "Title";
		Set<String> titles = getInvalidTitles();
		titles.add(validTitle);

		String validContent = "Content";
		Set<String> contents = getInvalidContents();
		contents.add(validContent);

		for (String title : titles) {
			for (String content : contents) {
				if (!validTitle.equals(title) || !validContent.equals(content)) {
					blogService.updateBlog(1L, new Blog(title, content));
				}
			}
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void whenUpdateBlog_withInvalidBlogIdAndValidBlog_throwIllegalArgumentException() {
		when(blogRepository.findOne(1L)).thenReturn(null);
		blogService.updateBlog(1L, new Blog("Title", "Content"));
	}

	@Test(expected = ConstraintViolationException.class)
	public void whenUpdateBlog_withValidBlogIdAndNullBlog_throwConstraintViolationException() {
		BlogRecord expectedBlogRecord = mock(BlogRecord.class);
		when(expectedBlogRecord.getId()).thenReturn(1L);
		when(expectedBlogRecord.getTitle()).thenReturn("Title");
		when(expectedBlogRecord.getContent()).thenReturn("Content");
		when(expectedBlogRecord.getCreatedDate()).thenReturn(new Date());
		when(expectedBlogRecord.getLastModifiedDate()).thenReturn(new Date());
		when(blogRepository.findOne(1L)).thenReturn(expectedBlogRecord);
		blogService.updateBlog(1L, null);
	}

	@Test(expected = ConstraintViolationException.class)
	public void whenUpdateBlog_withValidBlogIdAndInvalidBlog_throwConstraintViolationException() {

		String validTitle = "Title";
		String validContent = "Content";

		BlogRecord expectedBlogRecord = mock(BlogRecord.class);
		when(expectedBlogRecord.getId()).thenReturn(1L);
		when(expectedBlogRecord.getTitle()).thenReturn(validContent);
		when(expectedBlogRecord.getContent()).thenReturn(validContent);
		when(expectedBlogRecord.getCreatedDate()).thenReturn(new Date());
		when(expectedBlogRecord.getLastModifiedDate()).thenReturn(new Date());
		when(blogRepository.findOne(1L)).thenReturn(expectedBlogRecord);

		Set<String> titles = getInvalidTitles();
		titles.add(validTitle);
		Set<String> contents = getInvalidContents();
		contents.add(validContent);

		for (String title : titles) {
			for (String content : contents) {
				if (!validTitle.equals(title) || !validContent.equals(content)) {
					blogService.updateBlog(1L, new Blog(title, content));
				}
			}
		}
	}

	@Test
	public void whenUpdateBlog_withValidBlogIdAndValidBlog_returnUpdatedBlog() {

		BlogRecord originalBlogRecord = mock(BlogRecord.class);
		when(originalBlogRecord.getId()).thenReturn(1L);
		when(originalBlogRecord.getTitle()).thenReturn("Title");
		when(originalBlogRecord.getContent()).thenReturn("Content");
		when(originalBlogRecord.getCreatedDate()).thenReturn(new Date());
		when(originalBlogRecord.getLastModifiedDate()).thenReturn(new Date());
		BlogRecord updatedBlogRecord = mock(BlogRecord.class);
		when(updatedBlogRecord.getId()).thenReturn(1L);
		when(updatedBlogRecord.getTitle()).thenReturn("New Title");
		when(updatedBlogRecord.getContent()).thenReturn("New Content");
		when(updatedBlogRecord.getCreatedDate()).thenReturn(new Date());
		when(updatedBlogRecord.getLastModifiedDate()).thenReturn(new Date());
		when(blogRepository.findOne(1L)).thenReturn(originalBlogRecord);
		when(blogRepository.save(any(BlogRecord.class))).thenReturn(updatedBlogRecord);

		Blog actualBlog = blogService.updateBlog(1L, new Blog("New Title", "New Content"));
		assertNotNull(actualBlog);
		assertEquals(updatedBlogRecord.getId().longValue(), actualBlog.getId());
		assertEquals(updatedBlogRecord.getTitle(), actualBlog.getTitle());
		assertEquals(updatedBlogRecord.getContent(), actualBlog.getContent());
		assertEquals(updatedBlogRecord.getCreatedDate().getTime(), actualBlog.getCreatedEpoch());
		assertEquals(updatedBlogRecord.getLastModifiedDate().getTime(), actualBlog.getLastModifiedEpoch());
	}

	@Test(expected = IllegalArgumentException.class)
	public void whenDeleteBlog_withInvalidBlogId_throwIllegalArgumentException() {
		doThrow(EmptyResultDataAccessException.class).when(blogRepository).delete(1L);
		blogService.deleteBlog(1L);
	}

	@Test
	public void whenDeleteBlog_withValidBlogId_deleteBlog() {
		doNothing().when(blogRepository).delete(1L);
		blogService.deleteBlog(1L);
	}

	private Set<String> getInvalidTitles() {
		String nullTitle = null;
		String emptyTitle = "";
		String blankTitle = " ";
		String tooLongTitle = new String(new char[101]).replace("\0", "t");
		return new HashSet<>(Arrays.asList(nullTitle, emptyTitle, blankTitle, tooLongTitle));
	}

	private Set<String> getInvalidContents() {
		String nullContent = null;
		String emptyContent = "";
		String blankContent = " ";
		String tooLongContent = new String(new char[10001]).replace("\0", "c");
		return new HashSet<>(Arrays.asList(nullContent, emptyContent, blankContent, tooLongContent));
	}
}
