package com.chuwdu.tutorialbot;

import com.chuwdu.tutorialbot.commands.CommandManager;
import com.chuwdu.tutorialbot.commands.VoteCommand;
import com.chuwdu.tutorialbot.listeners.*;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import javax.security.auth.login.LoginException;

public class Main {

    private final Dotenv config;
    private final ShardManager shardManager;
    private static final String API_URL = "https://api.openai.com/v1/engines/davinci-codex/completions";
    public Main() throws LoginException {
        config = Dotenv.configure().load();
        String token = config.get("TOKEN");

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.watching("is cooking in hell"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_INVITES,
                GatewayIntent.MESSAGE_CONTENT);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        builder.enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.ACTIVITY);
        shardManager = builder.build();
        MessageReceived messageReceived = new MessageReceived();
        VoiceActivity voiceActivity = new VoiceActivity();
        VoteCommand voteCommand = new VoteCommand();
        //Register listeners
        shardManager.addEventListener(
                new EventListener(),
                messageReceived,
                voiceActivity,
                voteCommand,
                new CommandManager(messageReceived, voiceActivity, voteCommand)
        );
    }

    public Dotenv getConfig() {
        return config;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public static void main(String[] args) {
        try{
            Main bot = new Main();
        }catch(LoginException exception){
            exception.printStackTrace();
        }

    }
}