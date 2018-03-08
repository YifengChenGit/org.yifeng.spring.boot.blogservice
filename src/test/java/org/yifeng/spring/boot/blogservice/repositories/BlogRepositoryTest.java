package org.yifeng.spring.boot.blogservice.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;
import org.yifeng.spring.boot.blogservice.repositories.records.BlogRecord;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BlogRepositoryTest {

	@Autowired
	private BlogRepository blogRepository;

	@Autowired
	private TestEntityManager testEntityManager;

	@Test
	public void whenFindAll_inEmptyBlogTable_returnEmptyBlogRecords() {
		List<BlogRecord> blogRecords = blogRepository.findAll();
		assertNotNull(blogRecords);
		assertEquals(blogRecords.size(), 0);
	}

	@Test
	public void whenFindAll_inNonemptyBlogTable_returnBlogRecords() {
		BlogRecord expectedBlogRecordA = testEntityManager.persist(new BlogRecord("TitleA", "ContentA"));
		BlogRecord expectedBlogRecordB = testEntityManager.persist(new BlogRecord("TitleB", "ContentB"));
		List<BlogRecord> actualBlogRecords = blogRepository.findAll();
		assertNotNull(actualBlogRecords);
		assertEquals(2, actualBlogRecords.size());
		assertNotNull(actualBlogRecords.get(0));
		assertEquals(expectedBlogRecordA.getId(), actualBlogRecords.get(0).getId());
		assertEquals(expectedBlogRecordA.getTitle(), actualBlogRecords.get(0).getTitle());
		assertEquals(expectedBlogRecordA.getContent(), actualBlogRecords.get(0).getContent());
		assertEquals(expectedBlogRecordA.getCreatedDate(), actualBlogRecords.get(0).getCreatedDate());
		assertEquals(expectedBlogRecordA.getLastModifiedDate(), actualBlogRecords.get(0).getLastModifiedDate());
		assertNotNull(actualBlogRecords.get(1));
		assertEquals(expectedBlogRecordB.getId(), actualBlogRecords.get(1).getId());
		assertEquals(expectedBlogRecordB.getTitle(), actualBlogRecords.get(1).getTitle());
		assertEquals(expectedBlogRecordB.getContent(), actualBlogRecords.get(1).getContent());
		assertEquals(expectedBlogRecordB.getCreatedDate(), actualBlogRecords.get(1).getCreatedDate());
		assertEquals(expectedBlogRecordB.getLastModifiedDate(), actualBlogRecords.get(1).getLastModifiedDate());
	}

	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void whenFindOne_withNullId_throwInvalidDataAccessApiUsageException() {
		blogRepository.findOne((Long) null);
	}

	@Test
	public void whenFindOne_withInvalidId_returnNull() {
		assertNull(blogRepository.findOne(1L));
	}

	@Test
	public void whenFindOne_withValidId_returnBlogRecord() {
		BlogRecord expectedBlogRecord = testEntityManager.persist(new BlogRecord("Title", "Content"));
		BlogRecord actualBlogRecord = blogRepository.findOne(expectedBlogRecord.getId());
		assertNotNull(actualBlogRecord);
		assertEquals(expectedBlogRecord.getId(), actualBlogRecord.getId());
		assertEquals(expectedBlogRecord.getTitle(), actualBlogRecord.getTitle());
		assertEquals(expectedBlogRecord.getContent(), actualBlogRecord.getContent());
		assertEquals(expectedBlogRecord.getCreatedDate(), actualBlogRecord.getCreatedDate());
		assertEquals(expectedBlogRecord.getLastModifiedDate(), actualBlogRecord.getLastModifiedDate());
	}

	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void whenSave_withNullBlogRecord_throwInvalidDataAccessApiUsageException() {
		blogRepository.save((BlogRecord) null);
	}

	@Test(expected = ConstraintViolationException.class)
	public void whenSave_withInvalidBlogRecord_throwConstraintViolationException() {

		String validTitle = "Title";
		Set<String> titles = getInvalidTitles();
		titles.add(validTitle);

		String validContent = "Content";
		Set<String> contents = getInvalidContents();
		contents.add(validContent);

		for (String title : titles) {
			for (String content : contents) {
				if (!validTitle.equals(title) || !validContent.equals(content)) {
					blogRepository.save(new BlogRecord(title, content));
				}
			}
		}
	}

	@Test
	public void whenSave_withValidBlogRecord_returnBlogRecord() {
		BlogRecord expectedBlogRecord = new BlogRecord("Title", "Content");
		BlogRecord actualBlogRecord = blogRepository.save(expectedBlogRecord);
		assertNotNull(actualBlogRecord);
		assertEquals(expectedBlogRecord.getId(), actualBlogRecord.getId());
		assertEquals(expectedBlogRecord.getTitle(), actualBlogRecord.getTitle());
		assertEquals(expectedBlogRecord.getContent(), actualBlogRecord.getContent());
		assertEquals(expectedBlogRecord.getCreatedDate(), actualBlogRecord.getCreatedDate());
		assertEquals(expectedBlogRecord.getLastModifiedDate(), actualBlogRecord.getLastModifiedDate());
	}

	@Test(expected = InvalidDataAccessApiUsageException.class)
	public void whenDelete_withNullId_throwInvalidDataAccessApiUsageException() {
		blogRepository.delete((Long) null);
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void whenDelete_withInvalidId_throwEmptyResultDataAccessException() {
		blogRepository.delete(1L);
	}

	@Test
	public void whenDelete_withValidId_deleteBlogRecord() {
		BlogRecord blogRecord = testEntityManager.persist(new BlogRecord("Title", "Content"));
		blogRepository.delete(blogRecord.getId());
		assertNull(testEntityManager.find(BlogRecord.class, blogRecord.getId()));
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
