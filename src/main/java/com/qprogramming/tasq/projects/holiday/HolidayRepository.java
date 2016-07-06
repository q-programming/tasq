package com.qprogramming.tasq.projects.holiday;

import com.qprogramming.tasq.projects.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jromaniszyn on 06.07.2016.
 */
@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Integer> {
}



