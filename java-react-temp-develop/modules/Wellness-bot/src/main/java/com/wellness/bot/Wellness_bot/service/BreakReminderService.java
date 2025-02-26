package com.wellness.bot.Wellness_bot.service;

import com.wellness.bot.Wellness_bot.model.BreakReminder;
import com.wellness.bot.Wellness_bot.repository.BreakReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BreakReminderService {

    @Autowired
    private BreakReminderRepository breakReminderRepository;  // Autowire the repository

    // Method to retrieve reminders for a user
    public List<BreakReminder> getRemindersForUser(String userId) {
        return breakReminderRepository.findByUserId(userId);
    }
    public void scheduleBreakReminder(BreakReminder reminder) {
        // Logic to handle break reminder scheduling (e.g., save it to the database)
        breakReminderRepository.save(reminder);
    }
    public List<BreakReminder> getUserReminders(String userId) {
        return breakReminderRepository.findByUserId(userId);  // Assuming findByUserId is defined in repository
    }

    // Method to save a new break reminder
    public void saveBreakReminder(BreakReminder breakReminder) {
        breakReminderRepository.save(breakReminder);
    }
}
