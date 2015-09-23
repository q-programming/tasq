package com.qprogramming.tasq.support.sorters;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.qprogramming.tasq.support.Utils;
import com.qprogramming.tasq.support.sorters.CommentsSorter;
import com.qprogramming.tasq.task.comments.Comment;

@RunWith(MockitoJUnitRunner.class)
public class CommentsSorterTest {

	private List<Comment> commentList;

	@Before
	public void setUp() {
		commentList = createList();
	}

	@Test
	public void sortCommentsTest() {
		Collections.sort(commentList, new CommentsSorter(true));
		Assert.assertEquals("1", commentList.get(0).getMessage());
		Collections.sort(commentList, new CommentsSorter(false));
		Assert.assertEquals("5", commentList.get(0).getMessage());
	}

	private List<Comment> createList() {
		List<Comment> commentList = new LinkedList<Comment>();
		commentList.add(createComment("03-12-2014", "3"));
		commentList.add(createComment("02-12-2014", "2"));
		commentList.add(createComment("01-12-2014", "1"));
		commentList.add(createComment("04-12-2014", "4"));
		commentList.add(createComment("05-12-2014", "5"));
		return commentList;
	}

	private Comment createComment(String date, String no) {
		Comment comment = new Comment();
		comment.setMessage(no);
		comment.setDate(Utils.convertStringToDate(date));
		return comment;

	}
}
