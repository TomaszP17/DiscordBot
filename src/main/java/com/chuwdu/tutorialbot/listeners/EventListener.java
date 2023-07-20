package com.chuwdu.tutorialbot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

public class EventListener extends ListenerAdapter{

    public final Map<Long, List<String>> messageMap = new HashMap<>();

    /**
     * DONE
     * Metoda wysyla wiadomosc gdy bot sie wlaczy
     */
    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("The discord bot is now working");
    }
    /**
     * DONE
     * Metoda informuje ze uzytkownik wyszedl z serwera
     */
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        String avatarUrl = event.getUser().getEffectiveAvatarUrl();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        embedBuilder.setTitle("Member exit");
        embedBuilder.setDescription("User: " + event.getUser().getAsMention() + " left the server");
        embedBuilder.setColor(Color.RED);
        embedBuilder.setThumbnail(avatarUrl);
        String footer = "Date: " + currentDate + " \nTime: " + currentTime.getHour() + ":" + currentTime.getMinute() + ":" + currentTime.getSecond();
        embedBuilder.setFooter(footer);

        //Id kanalu na ktory bedzie wysylana wiadomosc
        String channelID = "1130965713536946357";
        TextChannel channel = event.getGuild().getTextChannelById(channelID);
        //event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    /**
     * DONE - BUT [ reakcje dzialaja tylko pod 1 embedem - trzeba dopisac recznie reszte]
     * Metoda wysyla wiadomosc jak uzytkownik doda reakcje pod wiadomoscia
     */
    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        User user = event.getUser();
        String emoji = event.getReaction().getEmoji().getAsReactionCode();
        String channel = event.getChannel().getName();
        //Id kanalu na ktory bedzie wysylana wiadomosc
        /*String channelID = "1130965713536946357";
        TextChannel channel = event.getGuild().getTextChannelById(channelID);
        //event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        channel.sendMessage(message).queue();*/

        String messageID = event.getMessageId();
        if(messageID.equals("1125879835382796391")){
            Member member = event.getMember();
            switch (emoji){
                case "1️⃣":
                    Role role = event.getGuild().getRoleById(1125864115496300658L);
                    event.getGuild().addRoleToMember(member, role).queue();
                    break;
                case "2️⃣":
                    Role role2 = event.getGuild().getRoleById(1125864197763387507L);
                    event.getGuild().addRoleToMember(member, role2).queue();
                    break;
                case "3️⃣":
                    Role role3 = event.getGuild().getRoleById(1125864250448023553L);
                    event.getGuild().addRoleToMember(member, role3).queue();
                    break;
                case "4️⃣":
                    Role role4 = event.getGuild().getRoleById(1125864296430194791L);
                    event.getGuild().addRoleToMember(member, role4).queue();
                    break;
            }

        }else if(messageID.equals("1125879836439744683")){

        }else if(messageID.equals("1125879837555433526")){

        }else{
            //wiadomosc zostala wyslana na totalnie inna wiadomosc niz ta ktora daje role
            String message = user.getEffectiveName() + " reacted to a message with " + emoji + " in the " + channel + " channel!";
            System.out.println(message);
        }
    }

    /**
     * DONE
     * Metoda nadaje początkową rangę oraz wysyła wiadomość informacyjną o dołączączeniu dla admina
     */
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        //give role to user on the joining to the server
        Member member = event.getMember();
        Role role = event.getGuild().getRoleById(1125856734750187581L);
        if(role != null){
            event.getGuild().addRoleToMember(member, role).queue();
        }

        String avatarUrl = event.getUser().getEffectiveAvatarUrl();
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Member Joined");
        embed.setDescription("User: " + event.getUser().getAsMention() + " has joined the server!");
        embed.setColor(Color.GREEN);
        embed.setThumbnail(avatarUrl);
        String footer = "Date: " + currentDate + " \nTime: " + currentTime.getHour() + ":" + currentTime.getMinute() + ":" + currentTime.getSecond();
        embed.setFooter(footer);

        //Id kanalu na ktory bedzie wysylana wiadomosc
        String channelID = "1130965713536946357";
        TextChannel channel = event.getGuild().getTextChannelById(channelID);
        //event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        channel.sendMessageEmbeds(embed.build()).queue();
    }

    /**
     * DONE
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

        //Id kanalu na ktory bedzie wysylana wiadomosc
        String channelID = "1130965713536946357";
        TextChannel channel = event.getGuild().getTextChannelById(channelID);
        //event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        channel.sendMessageEmbeds(embedBuilder.build()).queue();
    }

    /**
     * DONE
     * Metoda wysyła wiadomość o zmianie ilości użytkowników online
     */
    @Override
    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {

        List<Member> members = event.getGuild().getMembers();
        int onlineMembers = 0;
        for(Member member : members){
            if(member.getOnlineStatus() !=  OnlineStatus.OFFLINE){
                onlineMembers++;
            }
        }

        User user = event.getUser();
        String message = "**" + user.getAsTag() + " updated their status! There are now " + onlineMembers + " users online in this guild!";
        //Id kanalu na ktory bedzie wysylana wiadomosc
        String channelID = "1130965713536946357";
        TextChannel channel = event.getGuild().getTextChannelById(channelID);
        //event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessageEmbeds(embedBuilder.build()).queue();
        channel.sendMessage(message).queue();
    }

    /**
     * DONE Metoda zwraca informacje na kanał admina o usunięciu wiadomości
     * Informacje wysyłane:
     * Author, Zawartosc wiadomosci, Nazwa kanału, avatar, data i godzina
     */
    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        String channelID = "1130965713536946357";
        TextChannel channel = event.getGuild().getTextChannelById(channelID);

        long deletedMessage = event.getMessageIdLong();

        Iterator<Map.Entry<Long, List<String>>> iterator = messageMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, List<String>> entry = iterator.next();
            if (entry.getKey() == deletedMessage) {
                List<String> deletedArray = entry.getValue();
                String author = deletedArray.get(0);
                String content = deletedArray.get(1);
                String avatarURL = deletedArray.get(2);
                String channelName = deletedArray.get(3);
                System.out.println("Znaleziono klucz");
                iterator.remove();
                System.out.println("Klucz został usunięty przez: " + author);
                System.out.println("Zawartość: " + content);

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Deleted message");
                embedBuilder.setImage(avatarURL);
                embedBuilder.setColor(Color.RED);
                embedBuilder.setAuthor(author);
                embedBuilder.addField("Message Content", content, false);
                embedBuilder.addField("Channel", channelName, false);
                LocalDate currentDate = LocalDate.now();
                LocalTime currentTime = LocalTime.now();
                String footer = "Date: " + currentDate + " \nTime: " + currentTime.getHour() + ":" + currentTime.getMinute() + ":" + currentTime.getSecond();
                embedBuilder.setFooter(footer);

                channel.sendMessageEmbeds(embedBuilder.build()).queue();
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.getAuthor().isBot()){
            Long messageID = event.getMessageIdLong();
            String message = event.getMessage().getContentRaw();
            String author = event.getAuthor().getEffectiveName();
            String avatarURL = event.getAuthor().getEffectiveAvatarUrl();
            String channel = event.getChannel().getName();
            List<String> array = new ArrayList<>();

            array.add(author);
            array.add(message);
            array.add(avatarURL);
            array.add(channel);
            messageMap.put(messageID, array);

            /*for(Map.Entry<Long, List<String>> x : messageMap.entrySet()){
                System.out.println(x + " ");
            }*/
        }
    }

    /**
     * DONE
     * Metoda zwraca informacje na kanał admina o edycji wiadomości:
     * Zawartość wiadomości:
     * - Oznaczona osoba która edytowała wiadomość
     * - Stara zawartość wiadomości
     * - Nowa zawartość wiadomości
     * - Avatar
     * - Data i godzina
     */
    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        long messageId = event.getMessageIdLong();

        for(Map.Entry<Long, List<String>> x : messageMap.entrySet()){
            if(messageId == x.getKey()){
                String newContent = event.getMessage().getContentRaw();
                List<String> array = x.getValue();
                String oldContent = array.get(1);

                String avatarUrl = event.getAuthor().getEffectiveAvatarUrl();
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setImage(avatarUrl);
                embedBuilder.setTitle("change message");
                embedBuilder.setDescription("User: " + event.getAuthor().getAsMention() + " change the message content.");
                embedBuilder.addField("Old Content: ", oldContent, false);
                embedBuilder.addField("New Content: ", newContent, false);
                embedBuilder.setColor(Color.ORANGE);
                LocalDate currentDate = LocalDate.now();
                LocalTime currentTime = LocalTime.now();
                String footer = "Date: " + currentDate + " \nTime: " + currentTime.getHour() + ":" + currentTime.getMinute() + ":" + currentTime.getSecond();
                embedBuilder.setFooter(footer);

                //Id kanalu na ktory bedzie wysylana wiadomosc
                String channelID = "1130965713536946357";
                TextChannel channel = event.getGuild().getTextChannelById(channelID);
                //event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                channel.sendMessageEmbeds(embedBuilder.build()).queue();
            }
        }
    }
}
