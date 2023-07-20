package com.chuwdu.tutorialbot.commands;

import com.chuwdu.tutorialbot.CommandInteractionListener;
import com.chuwdu.tutorialbot.listeners.MessageReceived;
import com.chuwdu.tutorialbot.listeners.VoiceActivity;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.*;
import java.util.List;

public class CommandManager extends ListenerAdapter implements CommandInteractionListener {

    private final MessageReceived messageReceived;
    private final VoiceActivity voiceActivity;
    private final VoteCommand voteCommand;

    public CommandManager(MessageReceived messageReceived, VoiceActivity voiceActivity, VoteCommand voteCommand) {
        this.messageReceived = messageReceived;
        this.voiceActivity = voiceActivity;
        this.voteCommand = voteCommand;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equals("welcome")) {
            String userTag = event.getUser().getAsTag();
            event.reply("Welcome to the server, **" + userTag + "**!").setEphemeral(true).queue();
        } else if (command.equals("roles")) {
            event.deferReply().queue();
            String reponse = "";
            for(Role role : event.getGuild().getRoles()){
                reponse += role.getAsMention() + " \n";
            }
            event.getHook().sendMessage(reponse).queue();
        } else if (command.equals("say")) {
            OptionMapping messageOption = event. getOption("message");
            String message = messageOption.getAsString();

            MessageChannel channel;
            OptionMapping channelOption = event.getOption("channel");
            if(channelOption != null){
                channel = channelOption.getAsChannel().asGuildMessageChannel();
            }else{
                channel = event.getChannel();
            }

            channel.sendMessage(message).queue();
            event.reply("Your message was sent!").setEphemeral(true).queue();
        } else if (command.equals("emote")) {
            OptionMapping option = event.getOption("type");
            String type = option.getAsString();


            String replyMessage = "";
            switch(type.toLowerCase()){
                case "hug" -> {
                    replyMessage = "You hug the closest person to you.";
                }
                case "laugh" -> {
                    replyMessage = "You laugh hysterically at everyone around you";
                }
                case "cry" -> {
                    replyMessage = "You can't stop crying";
                }
            }
            event.reply(replyMessage).queue();
        } else if (command.equals("giverole")) {
            Member member = event.getOption("user").getAsMember();
            Role role = event.getOption("role").getAsRole();

            event.getGuild().addRoleToMember(member, role).queue();
            event.reply(member.getAsMention() + " has been given the " + role.getAsMention() + " role!").queue();
        } else if (command.equals("random")) {
            Integer min = Objects.requireNonNull(event.getOption("min")).getAsInt();
            Integer max = Objects.requireNonNull(event.getOption("max")).getAsInt();

            Random random = new Random();
            Integer result = random.nextInt(max) + min;

            event.reply("Your drawn number: " + result).queue();
        } else if (command.equals("ruless")) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Zasady serwera");
            embedBuilder.setColor(Color.RED);

            embedBuilder.addField("Zasada 1", "Nie obrażaj użytkowników.", false);
            embedBuilder.addField("Zasada 2", "Nie publikuj danych osobowych.", false);
            embedBuilder.addField("Zasada 3", "Nie wysyłaj pornografii, niesmacznych treści.", false);
            embedBuilder.addField("Zasada 4", "Nie nagrywaj rozmów.", false);
            embedBuilder.addField("Zasada 5", "Nie wysyłaj symbolik związanych z systemami totalitarnymi.", false);
            embedBuilder.addField("Zasada 6", "Nie wysyłaj wizerunków morderców, gwałcicieli.", false);
            embedBuilder.addField("Zasada 7", "Na kanale ogólnym możesz rozmawiać o tematach z innych pokoi, " +
                    "ale proszę o przeniesienie rozmowy do pokoju tematycznego gdy ktoś chce rozmawiać na inny temat.", false);
            embedBuilder.setFooter("""
                    Starzy bywalcy Piekła są nietykalni, nie mogą zostać wyrzuceni bez poważnego przewinienia. Uznajmy kogoś za starego bywalca po ocenie jego aktywności. Nowi przybysze są na czasowej liście obserwacyjnej.
                    Starzy bywalcy jeżeli nie są aktywni, są przenoszeni do pokoju Martwych, wychodzą z " +
                    "niego wykazując jakąkolwiek aktywność względem Piekła.""");

            event.replyEmbeds(embedBuilder.build()).queue();
        } else if (command.equals("delete")) {
            OptionMapping option = event.getOption("number");

            if (option != null) {
                int count = option.getAsInt();

                if (count < 1) {
                    event.reply("Podaj poprawną liczbę wiadomości do usunięcia (minimum 1).").setEphemeral(true).queue();
                    return;
                }

                TextChannel textChannel = (TextChannel) event.getChannel();

                textChannel.getHistory().retrievePast(count).queue(messages -> {
                    if (count == 1) {
                        if (messages.isEmpty()) {
                            event.reply("Nie znaleziono żadnych wiadomości do usunięcia.").setEphemeral(true).queue();
                            return;
                        }

                        Message messageToDelete = messages.get(0);
                        textChannel.deleteMessageById(messageToDelete.getId()).queue(
                                success -> event.reply("Usunięto ostatnią wiadomość.").setEphemeral(true).queue(),
                                error -> event.reply("Nie udało się usunąć wiadomości.").setEphemeral(true).queue()
                        );
                    } else {
                        if (messages.size() < 2) {
                            event.reply("Nie znaleziono wystarczającej liczby wiadomości do usunięcia.").setEphemeral(true).queue();
                            return;
                        }
                        textChannel.deleteMessages(messages).queue(
                                success -> event.reply("Usunięto ostatnią wiadomość.").setEphemeral(true).queue(),
                                error -> event.reply("Nie udało się usunąć wiadomości.").setEphemeral(true).queue()
                        );
                    }
                });
            } else {
                event.reply("Podaj poprawną liczbę wiadomości do usunięcia.").setEphemeral(true).queue();
            }
        } else if (command.equals("suggestion")) {
            OptionMapping optionMapping = event.getOption("text");
            String channelID = "1125587703283589150";
            GuildChannel channel = event.getGuild().getGuildChannelById(channelID);

            if (optionMapping != null) {
                String text = optionMapping.getAsString();

                //String suggestionChannelID = event.getJDA().getChannelById(channelID, );
                TextChannel suggestionChannel = (TextChannel) channel;

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setTitle("Sugestia");
                embedBuilder.setColor(Color.MAGENTA);
                embedBuilder.setDescription("Autor: " + event.getUser().getAsMention());
                embedBuilder.addField("Treść sugestii", text, false);
                suggestionChannel.sendMessageEmbeds(embedBuilder.build()).queue(
                        success -> event.reply("Sugestia została wysłana na kanał " + suggestionChannel.getAsMention()).queue(),
                        error -> event.reply("Wystąpił błąd podczas wysyłania sugestii.").queue()
                );
            } else {
                event.reply("Musisz wprowadzić tekst do sugestii aby ją wysłać, spróbuj ponownie.").queue();
            }
        } else if (command.equals("pick")) {
            //pierwszy embed z wiekiem do wybrania

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Wiek");
            embedBuilder.setDescription("Wybierz jedną z ról dostępnych poniżej, aby pokazać innym swój wiek");
            embedBuilder.addField("15+", ":one:", true);
            embedBuilder.addField("25+", ":two:", true);
            embedBuilder.addField("35+", ":three:", true);
            embedBuilder.addField("45+", ":four:", true);
            embedBuilder.setColor(Color.lightGray);

            EmbedBuilder embed2 = new EmbedBuilder();
            embed2.setTitle("Zainteresowania");
            embed2.setDescription("Wybierz jedną z ról dostępnych poniżej, aby pokazać innym swoje zainteresowania");
            embed2.addField("Polityka", ":one:", true);
            embed2.addField("IT", ":two:", true);
            embed2.addField("Gotowanie", ":three:", true);
            embed2.addField("Podróże", ":four:", true);
            embed2.setColor(Color.lightGray);

            EmbedBuilder embed3 = new EmbedBuilder();
            embed3.setTitle("Preferowany typ komunikacji");
            embed3.setDescription("Wybierz jedną z ról dostępnych poniżej, aby pokazać innym swój preferowany typ komunikacji");
            embed3.addField("Kanał Głosowy", ":one:", true);
            embed3.addField("Kanał Tekstowy", ":two:", true);
            embed3.addField("Wiadomość Prywatna", ":three:", true);
            embed3.setColor(Color.lightGray);

            //event.getChannel().sendMessageEmbeds(embeds).queue();
            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue(message -> {
                message.addReaction(Emoji.fromUnicode("1️⃣")).queue();
                message.addReaction(Emoji.fromUnicode("2️⃣")).queue();
                message.addReaction(Emoji.fromUnicode("3️⃣")).queue();
                message.addReaction(Emoji.fromUnicode("4️⃣")).queue();
            });
            event.getChannel().sendMessageEmbeds(embed2.build()).queue(message -> {
                message.addReaction(Emoji.fromUnicode("1️⃣")).queue();
                message.addReaction(Emoji.fromUnicode("2️⃣")).queue();
                message.addReaction(Emoji.fromUnicode("3️⃣")).queue();
                message.addReaction(Emoji.fromUnicode("4️⃣")).queue();
            });
            event.getChannel().sendMessageEmbeds(embed3.build()).queue(message -> {
                message.addReaction(Emoji.fromUnicode("1️⃣")).queue();
                message.addReaction(Emoji.fromUnicode("2️⃣")).queue();
                message.addReaction(Emoji.fromUnicode("3️⃣")).queue();
            });
        } else if (command.equals("top")) {

            OptionMapping optionMapping = event.getOption("type");
            String type = optionMapping.getAsString();

            switch (type.toLowerCase()){
                case "text" -> {
                    List<Map.Entry<Long, Long>> sortedEntries = messageReceived.getActivityTextMap().entrySet().stream()
                            .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                            .limit(10).toList();

                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setTitle("Top 10 aktywności na serwerze!")
                            .setColor(Color.YELLOW);
                    getTop10MembersActivity(event, sortedEntries, embedBuilder);
                }
                case "voice" -> {
                    List<Map.Entry<Long, Long>> sortedEntries = voiceActivity.getActivityVoiceMap().entrySet().stream()
                            .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                            .limit(10)
                            .toList();

                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setTitle("Top 10 voice activity on the server!")
                            .setColor(Color.YELLOW);
                    getTop10MembersActivity(event, sortedEntries, embedBuilder);
                }
            }
        }else if(command.equals("vote")){
            event.reply("Click the button to say hello")
                    .addActionRow(
                           Button.primary("hello", "Click Me"),
                            Button.secondary("No", "Don't click me"),
                            Button.success("emoji", Emoji.fromFormatted("<:minn:245267426227388416>"))) // Button with only an emoji)
                    .queue();
        }else if (event.getName().equals("info")) {
            event.reply("Click the buttons for more info")
                    .addActionRow( // link buttons don't send events, they just open a link in the browser when clicked
                            Button.link("https://github.com/DV8FromTheWorld/JDA", "GitHub")
                                    .withEmoji(Emoji.fromFormatted("<:github:849286315580719104>")), // Link Button with label and emoji
                            Button.link("https://ci.dv8tion.net/job/JDA/javadoc/", "Javadocs")) // Link Button with only a label
                    .queue();
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        voteCommand.onButtonInteraction(event);
    }

    private void getTop10MembersActivity(SlashCommandInteractionEvent event, List<Map.Entry<Long, Long>> sortedEntries, EmbedBuilder embedBuilder) {
        for(int i = 0; i < sortedEntries.size(); i++){
            Map.Entry<Long, Long> entry = sortedEntries.get(i);
            Long userId = entry.getKey();
            Long count = entry.getValue();

            int finalI = i;
            int finalI1 = i;
            event.getJDA().retrieveUserById(userId).queue(
                    user -> embedBuilder.addField("Rank: " + (finalI1 + 1), "User: " + user.getAsMention() + " Count: " + count, false),
                    error -> embedBuilder.addField("Rank: " + (finalI + 1), "User ID: " + userId + " Count: " + count, false)
            );
        }
        event.replyEmbeds(embedBuilder.build()).queue();
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("welcome", "Get welcomed by the bot."));
        //commandData.add(Commands.slash("roles", "Display all roles of the server"));

        // Command: /say <message> [channel]
        OptionData option1 = new OptionData(OptionType.STRING, "message", "The message you want the bot say.", true);
        OptionData option2 = new OptionData(OptionType.CHANNEL, "channel", "The channel you want to send this message in", false).setChannelTypes(ChannelType.TEXT, ChannelType.NEWS, ChannelType.GUILD_PUBLIC_THREAD);
        commandData.add(Commands.slash("say", "Make the bot say a message.").addOptions(option1, option2));

        // Command: /emote [type]
        OptionData option3 = new OptionData(OptionType.STRING, "type", "The type of emotion to express", true)
                .addChoice("Hug", "hug")
                .addChoice("Laugh", "laugh")
                .addChoice("Cry", "cry");
        commandData.add(Commands.slash("emote", "Express your emotions through text.").addOptions(option3));

        // Command: /giverole <user> <role>
        OptionData option4 = new OptionData(OptionType.USER, "user", "The user to give the role to", true);
        OptionData option5 = new OptionData(OptionType.ROLE, "role", "The role to be given", true);
        commandData.add(Commands.slash("giverole", "Give a user a role.").addOptions(option4, option5));

        // Command: /random <from> <to>
        OptionData option6 = new OptionData(OptionType.INTEGER, "min", "The lowest integer in the range.", true);
        OptionData option7 = new OptionData(OptionType.INTEGER, "max", "The highest integer in the range.", true);

        commandData.add(Commands.slash("random", "Roll a number from the range").addOptions(option6, option7));

        // Command: /regulamin
        commandData.add(Commands.slash("rules", "Get server rules"));

        // Command: /delete <number>
        OptionData option8 = new OptionData(OptionType.INTEGER, "number", "Number of messages to delete");

        commandData.add(Commands.slash("delete", "Delete the messages.").addOptions(option8));

        // Command: /suggestion <text>
        OptionData option9 = new OptionData(OptionType.STRING, "text", "The content of the suggestion sent to the administrator", true);
        commandData.add(Commands.slash("suggestion", "Send a suggestion to the administrator").addOptions(option9));

        // Command: /roles

        commandData.add(Commands.slash("pick", "Send a message with the roles to pick").setDefaultPermissions(DefaultMemberPermissions.DISABLED));

        // Command: /invite
        commandData.add(Commands.slash("invite", "Create a permanent invitation link to the server"));

        // Command: /top
        OptionData topOptions = new OptionData(OptionType.STRING, "type", "Type of the top category", true)
                        .addChoice("text", "text")
                        .addChoice("voice", "voice");
        commandData.add(Commands.slash("top", "Get top 10 activity members").addOptions(topOptions));

        // Command: /vote
        commandData.add(Commands.slash("vote", "Create a vote on the server"));

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }
}

