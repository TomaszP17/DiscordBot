package com.chuwdu.tutorialbot.listeners;

import com.chuwdu.tutorialbot.MessageReceivedListener;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.HashMap;
import java.util.Map;

public class MessageReceived extends ListenerAdapter implements MessageReceivedListener {

    private final Map<Long, Long> activityTextMap = new HashMap<>();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Long authorId = event.getAuthor().getIdLong();
        if (!event.getAuthor().isBot()) {
            activityTextMap.putIfAbsent(authorId, 0L);
            Long counter = activityTextMap.get(authorId);
            activityTextMap.put(authorId, ++counter);

            StringBuilder mapContent = new StringBuilder("Activity Count:\n");
            for (Map.Entry<Long, Long> entry : activityTextMap.entrySet()) {
                Long userId = entry.getKey();
                Long count = entry.getValue();
                mapContent.append("User ID: ").append(userId).append(", Count: ").append(count).append("\n");
            }
        }
    }
    public Map<Long, Long> getActivityTextMap() {
        return activityTextMap;
    }
}

