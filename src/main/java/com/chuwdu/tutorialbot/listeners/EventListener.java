package com.chuwdu.tutorialbot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventListener extends ListenerAdapter {

    private final Map<Long, String> originalContents = new HashMap<>();

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        User user = event.getUser();
        String emoji = event.getReaction().getEmoji().getAsReactionCode();
        String channelMention = event.getChannel().getAsMention();

        String message = user.getAsTag() + " reacted to the message with " + emoji + " in the " + channelMention + " channel!";

        event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessage(message).queue();
    }

    /**
     * ping pong reakcja
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();

        if (message.contains("ping")) {
            event.getGuildChannel().sendMessage("pong").queue();
        }
    }

    /**
     * Metoda wyswietla wiadomosc powitalna na serwerze pingujac uzytkownika ze zdjeciem
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        String avatarUrl = event.getUser().getEffectiveAvatarUrl();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Member Joined");
        embed.setDescription("User: " + event.getUser().getAsMention() + " has joined the server!");
        embed.setColor(Color.GREEN);
        embed.setThumbnail(avatarUrl);

        event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessageEmbeds(embed.build()).queue();
    }

    /**
     * Wysyla wiadomosc o zmianie nicku uzytkownika, wyswietla stary nick oraz nowy nick
     */
    @Override
    public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
        String avatarUrl = event.getUser().getEffectiveAvatarUrl();

        String oldNickname = event.getOldNickname();
        if(oldNickname == null){
            oldNickname = "None";
        }

        String newNickname = event.getNewNickname();
        if(newNickname == null){
            newNickname = "None";
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Changed nickname");
        embedBuilder.setDescription("User: " + event.getUser().getAsMention() + " changed their nickname");
        embedBuilder.addField("Old Nickname", oldNickname, false);
        embedBuilder.addField("New Nickname", newNickname, false);
        embedBuilder.setColor(Color.YELLOW);
        embedBuilder.setImage(avatarUrl);

        event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessageEmbeds(embedBuilder.build()).queue();
    }

    /**
     * Metoda wysyła wiadomość o zmianie ilości użytkowników online
     */
    @Override
    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {

        List<Member> members = event.getGuild().getMembers();
        int onlineMembers = 0;
        for(Member member : members){
            if(member.getOnlineStatus() == OnlineStatus.ONLINE){
                onlineMembers++;
            }
        }

        User user = event.getUser();
        String message = "**" + user.getAsTag() + " updated their status! There are now " + onlineMembers + " users online in this guild!";
        event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessage(message).queue();
    }

    /**
     * Metoda wyswietla informacje o tym ze uzytkownik zedytowal wiadomosc i wyswietla zawartosc zedytowanej wiadomosci
     * NIE DZIALA STARA WIADOMOSC, TRZEBA BEDZIE UTWORZYC PLIK KTORY BEDZIE PRZECHOWYWAL ZAWARTOSC - ID WIAD i nastepnie program bedzie szukal jej i zastepowal
     */
    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        long messageId = event.getMessageIdLong();
        String oldContent = originalContents.getOrDefault(messageId, "None");
        String newContent = event.getMessage().getContentRaw();
        originalContents.put(messageId, oldContent);

        String avatarUrl = event.getAuthor().getEffectiveAvatarUrl();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setImage(avatarUrl);
        embedBuilder.setTitle("change message");
        embedBuilder.setDescription("User: " + event.getAuthor().getAsMention() + " change the message content.");
        embedBuilder.addField("Old Content: ", oldContent, false);
        embedBuilder.addField("New Content: ", newContent, false);
        embedBuilder.setColor(Color.ORANGE);

        event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessageEmbeds(embedBuilder.build()).queue();

    }
}
