package com.wellness.bot.Wellness_bot.repository;

import com.wellness.bot.Wellness_bot.model.WellnessCheckin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WellnessCheckinRepository extends JpaRepository<WellnessCheckin, Long> {
    List<WellnessCheckin> findByUserId(String userId);
}
