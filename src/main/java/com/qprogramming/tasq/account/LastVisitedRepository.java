package com.qprogramming.tasq.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Khobar on 04.10.2016.
 */
@Repository
public interface LastVisitedRepository extends JpaRepository<LastVisited, Integer> {

    LastVisited findByItemName(String name);

    List<LastVisited> findByAccountOrderByTimeAsc(Long id);

    List<LastVisited> findByAccountAndTypeNotNullOrderByTimeAsc(Long id);

    List<LastVisited> findByAccountAndTypeNullOrderByTimeAsc(Long id);

    List<LastVisited> findByAccountAndTypeNotNullOrderByTimeDesc(Long id);

    List<LastVisited> findByAccountAndTypeNullOrderByTimeDesc(Long id);

    List<LastVisited> findByAccountOrderByTimeDesc(Long id);

//    LastVisited findByItemName(String name);
}
