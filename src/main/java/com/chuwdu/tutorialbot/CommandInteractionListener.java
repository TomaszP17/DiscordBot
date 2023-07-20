package com.chuwdu.tutorialbot;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface CommandInteractionListener {
    void onSlashCommandInteraction(SlashCommandInteractionEvent event);
    void onGuildReady(GuildReadyEvent event);
}
