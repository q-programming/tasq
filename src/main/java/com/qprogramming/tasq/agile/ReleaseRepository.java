package com.qprogramming.tasq.agile;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, Integer> {

	Release findByRelease(String release);

	Release findById(Long id);

	List<Release> findByProjectId(Long projectId);
}
