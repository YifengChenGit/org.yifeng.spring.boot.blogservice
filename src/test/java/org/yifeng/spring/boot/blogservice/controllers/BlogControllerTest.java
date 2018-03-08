package org.yifeng.spring.boot.blogservice.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.yifeng.spring.boot.blogservice.controllers.requests.CreateBlogRequest;
import org.yifeng.spring.boot.blogservice.controllers.requests.UpdateBlogRequest;
import org.yifeng.spring.boot.blogservice.services.BlogService;
import org.yifeng.spring.boot.blogservice.services.models.Blog;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(BlogController.class)
public class BlogControllerTest {

	@Configuration
	@ComponentScan(basePackageClasses = { BlogController.class })
	public static class BlogControllerTestConfiguration {

		@Bean
		public MethodValidationPostProcessor getMethodValidationPostProcessor() {
			return new MethodValidationPostProcessor();
		}
	}
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
    private MockMvc mockMvc;
 
    @MockBean
    private BlogService blogService;
    
    @Test
    public void whenGetBlogs_inEmptyBlogRepository_returnEmptyBlogResponses() throws Exception {
    	when(blogService.getBlogs()).thenReturn(Collections.emptyList());
    	mockMvc.perform(get("/blogs").contentType(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$", notNullValue()))
    	.andExpect(jsonPath("$", hasSize(0)));
    }
    
    @Test
    public void whenGetBlogs_inNonemptyBlogRepository_returnBlogResponses() throws Exception {
    	
    	Blog expectedBlogA = new Blog(1L, "TitleA", "ContentA", new Date().getTime(), new Date().getTime());
    	Blog expectedBlogB = new Blog(2L, "TitleB", "ContentB", new Date().getTime(), new Date().getTime());
    	when(blogService.getBlogs()).thenReturn(Arrays.asList(expectedBlogA, expectedBlogB));

    	mockMvc.perform(get("/blogs").contentType(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$", notNullValue()))
    	.andExpect(jsonPath("$", hasSize(2)))
    	.andExpect(jsonPath("$[0]", notNullValue()))
    	.andExpect(jsonPath("$[0].id", equalTo(1)))
    	.andExpect(jsonPath("$[0].title", equalTo(expectedBlogA.getTitle())))
    	.andExpect(jsonPath("$[0].content", equalTo(expectedBlogA.getContent())))
    	.andExpect(jsonPath("$[0].createdDate", equalTo(expectedBlogA.getCreatedEpoch())))
    	.andExpect(jsonPath("$[0].lastModifiedDate", equalTo(expectedBlogA.getLastModifiedEpoch())))
    	.andExpect(jsonPath("$[1]", notNullValue()))
    	.andExpect(jsonPath("$[1].id", equalTo(2)))
    	.andExpect(jsonPath("$[1].title", equalTo(expectedBlogB.getTitle())))
    	.andExpect(jsonPath("$[1].content", equalTo(expectedBlogB.getContent())))
    	.andExpect(jsonPath("$[1].createdDate", equalTo(expectedBlogB.getCreatedEpoch())))
    	.andExpect(jsonPath("$[1].lastModifiedDate", equalTo(expectedBlogB.getLastModifiedEpoch())));
    }

	@Test
    public void whenGetBlog_withNonnumericBlogId_throwBadRequest() throws Exception {
    	mockMvc.perform(get("/blogs/x").contentType(MediaType.APPLICATION_JSON))
    	.andExpect(status().isBadRequest());
    }
    
	@Test
    public void whenGetBlog_withInvalidBlogId_throwNotFound() throws Exception {
		doThrow(IllegalArgumentException.class).when(blogService).getBlog(1L);
    	mockMvc.perform(get("/blogs/1").contentType(MediaType.APPLICATION_JSON))
    	.andExpect(status().isNotFound());
    }
	
	@Test
	public void whenGetBlog_withValidBlogId_returnBlogResponse() throws Exception {
		
		Blog expectedBlog = new Blog(1L, "Title", "Content", new Date().getTime(), new Date().getTime());
		when(blogService.getBlog(1L)).thenReturn(expectedBlog);
		
		mockMvc.perform(get("/blogs/1").contentType(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$", notNullValue()))
    	.andExpect(jsonPath("$.id", equalTo(1)))
    	.andExpect(jsonPath("$.title", equalTo(expectedBlog.getTitle())))
    	.andExpect(jsonPath("$.content", equalTo(expectedBlog.getContent())))
    	.andExpect(jsonPath("$.createdDate", equalTo(expectedBlog.getCreatedEpoch())))
    	.andExpect(jsonPath("$.lastModifiedDate", equalTo(expectedBlog.getLastModifiedEpoch())));
	}
	
	@Test
	public void whenCreateBlog_withNullCreateBlogRequest_throwBadRequest() throws Exception {
		mockMvc.perform(post("/blogs").contentType(MediaType.APPLICATION_JSON).content((byte[]) null))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void whenCreateBlog_withInvalidCreateBlogRequest_throwBadRequest() throws Exception {
		
		String validTitle = "Title";
		Set<String> titles = getInvalidTitles();
		titles.add(validTitle);

		String validContent = "Content";
		Set<String> contents = getInvalidContents();
		contents.add(validContent);

		for (String title : getInvalidTitles()) {
			for (String content : getInvalidContents()) {
				if (!validTitle.equals(title) || !validContent.equals(content)) {
					CreateBlogRequest createBlogRequest = new CreateBlogRequest(title, content);
					String requestBody = objectMapper.writeValueAsString(createBlogRequest);
					mockMvc.perform(post("/blogs").contentType(MediaType.APPLICATION_JSON).content(requestBody))
					.andExpect(status().isBadRequest());
				}
			}
		}
	}
	
	@Test
	public void whenCreateBlog_withValidCreateBlogRequest_returnBlogResponse() throws Exception {
		
		Blog expectedBlog = new Blog(1L, "Title", "Content", new Date().getTime(), new Date().getTime());
		when(blogService.createBlog(any(Blog.class))).thenReturn(expectedBlog);
		
		CreateBlogRequest createBlogRequest = new CreateBlogRequest(expectedBlog.getTitle(), expectedBlog.getContent());
		String requestBody = objectMapper.writeValueAsString(createBlogRequest);
		mockMvc.perform(post("/blogs").contentType(MediaType.APPLICATION_JSON).content(requestBody))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$", notNullValue()))
    	.andExpect(jsonPath("$.id", equalTo(1)))
    	.andExpect(jsonPath("$.title", equalTo(expectedBlog.getTitle())))
    	.andExpect(jsonPath("$.content", equalTo(expectedBlog.getContent())))
    	.andExpect(jsonPath("$.createdDate", equalTo(expectedBlog.getCreatedEpoch())))
    	.andExpect(jsonPath("$.lastModifiedDate", equalTo(expectedBlog.getLastModifiedEpoch())));
	}
	
	@Test
	public void whenUpdateBlog_withNonnumericBlogIdAndNullUpdateBlogRequest_throwBadReqeust() throws Exception {
		mockMvc.perform(put("/blogs/x").contentType(MediaType.APPLICATION_JSON).content((byte[]) null))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void whenUpdateBlog_withNonnumericBlogIdAndInvalidUpdateBlogRequest_throwBadReqeust() throws Exception {
		
		String validTitle = "Title";
		Set<String> titles = getInvalidTitles();
		titles.add(validTitle);

		String validContent = "Content";
		Set<String> contents = getInvalidContents();
		contents.add(validContent);

		for (String title : getInvalidTitles()) {
			for (String content : getInvalidContents()) {
				if (!validTitle.equals(title) || !validContent.equals(content)) {
					mockMvc.perform(put("/blogs/x").contentType(MediaType.APPLICATION_JSON).content((byte[]) null))
					.andExpect(status().isBadRequest());
				}
			}
		}
	}
	
	@Test
	public void whenUpdateBlog_withNonnumericBlogIdAndValidUpdateBlogRequest_throwBadReqeust() throws Exception {
		UpdateBlogRequest updateBlogRequest = new UpdateBlogRequest("Title", "Content");
		String requestBody = objectMapper.writeValueAsString(updateBlogRequest);
		mockMvc.perform(put("/blogs/x").contentType(MediaType.APPLICATION_JSON).content(requestBody))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void whenUpdateBlog_withInvalidBlogIdAndNullUpdateBlogRequest_throwBadReqeust() throws Exception {
		doThrow(IllegalArgumentException.class).when(blogService).updateBlog(eq(1L), any(Blog.class));
		mockMvc.perform(put("/blogs/1").contentType(MediaType.APPLICATION_JSON).content((byte[]) null))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void whenUpdateBlog_withInvalidBlogIdAndInvalidUpdateBlogRequest_throwBadReqeust() throws Exception {
		
		doThrow(IllegalArgumentException.class).when(blogService).updateBlog(eq(1L), any(Blog.class));
		
		String validTitle = "Title";
		Set<String> titles = getInvalidTitles();
		titles.add(validTitle);

		String validContent = "Content";
		Set<String> contents = getInvalidContents();
		contents.add(validContent);

		for (String title : getInvalidTitles()) {
			for (String content : getInvalidContents()) {
				if (!validTitle.equals(title) || !validContent.equals(content)) {
					UpdateBlogRequest updateBlogRequest = new UpdateBlogRequest(title, content);
					String requestBody = objectMapper.writeValueAsString(updateBlogRequest);
					mockMvc.perform(put("/blogs/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
					.andExpect(status().isBadRequest());
				}
			}
		}
	}
	
	@Test
	public void whenUpdateBlog_withInvalidBlogIdAndValidUpdateBlogRequest_throwNotFound() throws Exception {
		doThrow(IllegalArgumentException.class).when(blogService).updateBlog(eq(1L), any(Blog.class));
		UpdateBlogRequest updateBlogRequest = new UpdateBlogRequest("Title", "Content");
		String requestBody = objectMapper.writeValueAsString(updateBlogRequest);
		mockMvc.perform(put("/blogs/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
		.andExpect(status().isNotFound());
	}
	
	@Test
	public void whenUpdateBlog_withValidBlogIdAndNullUpdateBlogRequest_throwBadReqeust() throws Exception {
		Blog expectedblog = new Blog(1L, "New Title", "New Content", new Date().getTime(), new Date().getTime());
		when(blogService.updateBlog(eq(1L), any(Blog.class))).thenReturn(expectedblog);
		mockMvc.perform(put("/blogs/1").contentType(MediaType.APPLICATION_JSON).content((byte[]) null))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void whenUpdateBlog_withValidBlogIdAndInvalidUpdateBlogRequest_throwBadReqeust() throws Exception {
		
		Blog expectedblog = new Blog(1L, "New Title", "New Content", new Date().getTime(), new Date().getTime());
		when(blogService.updateBlog(eq(1L), any(Blog.class))).thenReturn(expectedblog);
		
		String validTitle = "Title";
		Set<String> titles = getInvalidTitles();
		titles.add(validTitle);

		String validContent = "Content";
		Set<String> contents = getInvalidContents();
		contents.add(validContent);

		for (String title : getInvalidTitles()) {
			for (String content : getInvalidContents()) {
				if (!validTitle.equals(title) || !validContent.equals(content)) {
					UpdateBlogRequest updateBlogRequest = new UpdateBlogRequest(title, content);
					String requestBody = objectMapper.writeValueAsString(updateBlogRequest);
					mockMvc.perform(put("/blogs/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
					.andExpect(status().isBadRequest());
				}
			}
		}
	}
	
	@Test
	public void whenUpdateBlog_withValidBlogIdAndValidUpdateBlogRequest_returnUpdatedBlogResponse() throws Exception {
		
		Blog expectedblog = new Blog(1L, "New Title", "New Content", new Date().getTime(), new Date().getTime());
		when(blogService.updateBlog(eq(1L), any(Blog.class))).thenReturn(expectedblog);
		
		UpdateBlogRequest updateBlogRequest = new UpdateBlogRequest("New Title", "New Content");
		String requestBody = objectMapper.writeValueAsString(updateBlogRequest);
		mockMvc.perform(put("/blogs/1").contentType(MediaType.APPLICATION_JSON).content(requestBody))
		.andExpect(status().isOk())
    	.andExpect(jsonPath("$", notNullValue()))
    	.andExpect(jsonPath("$.id", equalTo(1)))
    	.andExpect(jsonPath("$.title", equalTo(expectedblog.getTitle())))
    	.andExpect(jsonPath("$.content", equalTo(expectedblog.getContent())))
    	.andExpect(jsonPath("$.createdDate", equalTo(expectedblog.getCreatedEpoch())))
    	.andExpect(jsonPath("$.lastModifiedDate", equalTo(expectedblog.getLastModifiedEpoch())));
	}
	
	@Test
    public void whenDeleteBlog_withNonnumericBlogId_throwBadRequest() throws Exception {
    	mockMvc.perform(delete("/blogs/x").contentType(MediaType.APPLICATION_JSON))
    	.andExpect(status().isBadRequest());
    }
    
	@Test
    public void whenDeleteBlog_withInvalidBlogId_throwNotFound() throws Exception {
		doThrow(IllegalArgumentException.class).when(blogService).deleteBlog(1L);
    	mockMvc.perform(delete("/blogs/1").contentType(MediaType.APPLICATION_JSON))
    	.andExpect(status().isNotFound());
    }
	
	@Test
	public void whenDeleteBlog_withValidBlogId_returnOk() throws Exception {
		doNothing().when(blogService).deleteBlog(1L);
		mockMvc.perform(delete("/blogs/1").contentType(MediaType.APPLICATION_JSON))
    	.andExpect(status().isOk());
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
