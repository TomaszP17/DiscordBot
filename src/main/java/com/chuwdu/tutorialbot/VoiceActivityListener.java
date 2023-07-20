package com.chuwdu.tutorialbot;

import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import org.jetbrains.annotations.NotNull;

public interface VoiceActivityListener {
    void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event);
}
