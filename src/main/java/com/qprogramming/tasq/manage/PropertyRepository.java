package com.qprogramming.tasq.manage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {

	Property findByKey(String key);
}
