package com.chuwdu.tutorialbot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface MessageReceivedListener {
    void onMessageReceived(MessageReceivedEvent event);
}
