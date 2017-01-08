/**
 *
 */
package com.qprogramming.tasq.chat;

import com.qprogramming.tasq.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Integer> {
    ChatMessage findById(Long id);

    List<ChatMessage> findByAccount(Account account);

    List<ChatMessage> findByProject(String project);
}
