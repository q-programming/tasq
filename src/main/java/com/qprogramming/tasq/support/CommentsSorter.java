package com.qprogramming.tasq.support;

import java.util.Comparator;
import com.qprogramming.tasq.task.comments.Comment;

public class CommentsSorter implements Comparator<Comment> {
	private boolean isDescending;

	public CommentsSorter(boolean isDescending) {
		this.isDescending = isDescending;
	}

	public int compare(Comment a, Comment b) {
		int result = 0;

		if (a.getDate() == b.getDate()) {
			result = 0;
		} else if (a.getRawDate().before(b.getRawDate())) {
			result = -1;
		} else {
			result = 1;
		}
		return isDescending ? result : -result;
	}
}
