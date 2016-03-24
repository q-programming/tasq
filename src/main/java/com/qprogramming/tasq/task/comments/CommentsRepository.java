/**
 * 
 */
package com.qprogramming.tasq.task.comments;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qprogramming.tasq.account.Account;

public interface CommentsRepository extends JpaRepository<Comment, Integer> {

	Comment findById(Long id);

	Set<Comment> findByAuthor(Account author);

	Set<Comment> findByTaskIdOrderByDateDesc(String id);

}
