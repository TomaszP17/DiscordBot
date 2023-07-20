package com.chuwdu.tutorialbot.listeners;

import com.chuwdu.tutorialbot.VoiceActivityListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class VoiceActivity extends ListenerAdapter implements VoiceActivityListener {

    private final Map<Member, Instant> joinTimeMap = new HashMap<>();
    private final Map<Long, Long> activityVoiceMap = new HashMap<>();

    /**
     * Metoda zwraca informacje czy ktos dolaczyl lub odszedl z kanalu
     */
    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        AudioChannel oldChannel = event.getChannelLeft();
        AudioChannel newChannel = event.getChannelJoined();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        String description = "Data: " + currentDate + " \nGodzina: " + currentTime.getHour() + ":" + currentTime.getMinute() + ":" + currentTime.getSecond();
        String avatarURL = event.getMember().getEffectiveAvatarUrl();
        String memberMention = event.getMember().getAsMention();

        Member member = event.getMember();
        Instant time = Instant.now();

        if(newChannel != null){

            embedBuilder.setImage(avatarURL);
            embedBuilder.setColor(Color.GREEN);
            embedBuilder.setTitle("Uzytkownik wszedł na kanał " + newChannel.getAsMention());
            embedBuilder.setDescription(description);
            embedBuilder.addField("Użytkownik", memberMention, false);

            joinTimeMap.put(member, time);

        }else if(oldChannel != null){

            embedBuilder.setImage(avatarURL);
            embedBuilder.setColor(Color.RED);
            embedBuilder.setTitle("Uzytkownik opuścił kanał " + oldChannel.getAsMention());
            embedBuilder.setDescription(description);
            embedBuilder.addField("Użytkownik", memberMention, false);

            Instant joinTime = joinTimeMap.remove(member);
            if(joinTime != null){
                Duration duration = Duration.between(joinTime, time);
                long secondsSpend = duration.getSeconds();
                System.out.println("Uzytkownik: " + memberMention + " spedzil: " + secondsSpend + " na kanale glosowym.");
                long memberId = event.getMember().getIdLong();
                activityVoiceMap.put(memberId, activityVoiceMap.getOrDefault(memberId, 0L) + secondsSpend);
            }
        }
        //Id kanalu na ktory bedzie wysylana wiadomosc
        String channelID = "1130965713536946357";
        TextChannel channel = event.getGuild().getTextChannelById(channelID);
        //event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    public Map<Long, Long> getActivityVoiceMap() {
        return activityVoiceMap;
    }
}
