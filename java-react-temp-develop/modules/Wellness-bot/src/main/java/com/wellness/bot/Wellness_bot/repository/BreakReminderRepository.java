package com.wellness.bot.Wellness_bot.repository;

import com.wellness.bot.Wellness_bot.model.BreakReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreakReminderRepository extends JpaRepository<BreakReminder, Long> {
    List<BreakReminder> findByUserId(String userId);
}

