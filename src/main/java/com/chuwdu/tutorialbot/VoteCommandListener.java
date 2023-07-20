package com.chuwdu.tutorialbot;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface VoteCommandListener {
    void onButtonInteraction(ButtonInteractionEvent event);
}
