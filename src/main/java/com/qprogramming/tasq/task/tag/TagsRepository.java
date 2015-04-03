package com.qprogramming.tasq.task.tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagsRepository extends JpaRepository<Tag, Integer> {
	
	Tag findByName(String name);
	List<Tag> findByNameContainingIgnoreCase(String name);
}
