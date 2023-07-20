package com.chuwdu.tutorialbot.commands;

import com.chuwdu.tutorialbot.VoteCommandListener;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class VoteCommand extends ListenerAdapter implements VoteCommandListener {

    private final Map<String, Integer> votesMap;
    private final Map<String, String> userVotesMap;
    private final AtomicBoolean isVotingInProgress;

    public VoteCommand() {
        votesMap = new HashMap<>();
        userVotesMap = new HashMap<>();
        isVotingInProgress = new AtomicBoolean(false);
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (isVotingInProgress.get()) return;

        isVotingInProgress.set(true);

        String componentId = event.getComponentId();
        String userId = event.getUser().getId();

        if (userVotesMap.containsKey(userId)) {
            event.reply("You have already voted!").setEphemeral(true).queue((message) -> isVotingInProgress.set(false));
            return;
        }

        if (componentId.equals("hello") || componentId.equals("No") || componentId.equals("emoji")) {
            int currentVotes = votesMap.getOrDefault(componentId, 0);
            votesMap.put(componentId, currentVotes + 1);
            userVotesMap.put(userId, componentId);

            int totalVotes = votesMap.values().stream().mapToInt(Integer::intValue).sum();

            StringBuilder response = new StringBuilder();
            response.append("Vote Results:\n");

            for (Map.Entry<String, Integer> entry : votesMap.entrySet()) {
                String option = entry.getKey();
                int votes = entry.getValue();
                double percentage = (votes / (double) totalVotes) * 100;

                response.append(option).append(": ").append(votes).append(" votes (").append(String.format("%.2f", percentage)).append("%)\n");
            }

            event.editMessage(response.toString()).queue((message) -> isVotingInProgress.set(false));
        }
    }

    // Method to reset the voting state and clear the vote records
    public void resetVoting() {
        votesMap.clear();
        userVotesMap.clear();
        isVotingInProgress.set(false);
    }
}
