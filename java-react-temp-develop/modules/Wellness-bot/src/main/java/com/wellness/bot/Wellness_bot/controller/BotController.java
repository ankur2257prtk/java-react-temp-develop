package com.wellness.bot.Wellness_bot.controller;

import com.microsoft.bot.builder.Bot;
import com.microsoft.bot.builder.InvokeResponse;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.schema.Activity;
import com.wellness.bot.Wellness_bot.model.BreakReminder;  // <-- Add the import here
import com.wellness.bot.Wellness_bot.repository.BreakReminderRepository;  // <-- Add the import here
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;  // <-- Add the import here
import java.time.format.DateTimeFormatter;  // <-- Add the import here
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/messages")
public class BotController {

    private final BotFrameworkHttpAdapter adapter;
    private final Bot bot;

    @Autowired
    private BreakReminderRepository breakReminderRepository;  // <-- Autowire repository here

    public BotController(BotFrameworkHttpAdapter adapter, Bot bot) {
        this.adapter = adapter;
        this.bot = bot;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<InvokeResponse>> processMessage(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @RequestBody Activity activity) {
        return adapter.processIncomingActivity(authHeader, activity, bot)
                .thenApply(ResponseEntity::ok);
    }

    // Define WellnessBot inside BotController
    public static class WellnessBot extends com.microsoft.bot.builder.ActivityHandler {

        @Autowired
        private BreakReminderRepository breakReminderRepository;  // <-- Autowire repository here

        @Override
        protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
            String userMessage = turnContext.getActivity().getText().toLowerCase();
            String response;

            switch (userMessage) {
                case "check-in":
                    response = "How are you feeling today? (Happy, Neutral, Stressed)";
                    break;
                case "happy":
                case "neutral":
                case "stressed":
                    response = "Thanks for your response! Your wellness check-in is recorded.";
                    break;
                case "reminder":
                    response = "I'll remind you to take breaks every hour! Stay hydrated 💧";
                    break;
                case "meditation":
                    response = "Here’s a guided meditation for you: [Click Here](https://www.youtube.com/watch?v=inpok4MKVLM)";
                    break;
                case "set break reminder":
                    response = "Sure! Please provide the time for your break reminder in the format 'HH:mm'. Example: '15:30'.";
                    break;
                default:
                    // Check if the user is trying to set a break reminder with a time
                    if (userMessage.contains("set a break reminder")) {
                        String time = userMessage.replaceAll("[^0-9:]", ""); // Extract the time from the message
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                            LocalDateTime reminderTime = LocalDateTime.now().withHour(Integer.parseInt(time.split(":")[0]))
                                    .withMinute(Integer.parseInt(time.split(":")[1]));
                            // Save the reminder to the database
                            BreakReminder breakReminder = new BreakReminder();
                            breakReminder.setUserId("user123"); // Here you can use actual userId
                            breakReminder.setReminderTime(reminderTime);
                            breakReminderRepository.save(breakReminder);

                            response = "Break reminder set for " + reminderTime.format(DateTimeFormatter.ofPattern("HH:mm"));
                        } catch (Exception e) {
                            response = "Sorry, I could not understand the time format. Please use 'HH:mm'. Example: '15:30'.";
                        }
                    } else {
                        response = "Sorry, I didn't understand. Try 'check-in', 'reminder', 'set break reminder', or 'meditation'.";
                    }
                    break;
            }

            return turnContext.sendActivity(MessageFactory.text(response))
                    .thenApply(resourceResponse -> null);
        }
    }
}
