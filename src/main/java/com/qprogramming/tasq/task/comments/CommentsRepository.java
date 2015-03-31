/**
 * 
 */
package com.qprogramming.tasq.task.comments;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qprogramming.tasq.account.Account;

public interface CommentsRepository extends JpaRepository<Comment, Integer> {

	public Comment findById(Long id);

	public Set<Comment> findByAuthor(Account author);

	public Set<Comment> findByTaskIdOrderByDateDesc(String id);

}
