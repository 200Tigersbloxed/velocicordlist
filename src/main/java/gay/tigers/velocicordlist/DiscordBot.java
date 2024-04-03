package gay.tigers.velocicordlist;

import gay.tigers.velocicordlist.Databasing.IDatabase;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.interaction.*;

import java.util.*;
import java.util.function.Predicate;

class DiscordBot {
    private DiscordApi discordApi;

    DiscordBot(String token, IDatabase database){
        discordApi = new DiscordApiBuilder().setToken(token).login().join();
        SlashCommand.with("whitelist", "Adds your minecraft username to the whitelist", Arrays.asList(
                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "add", "Adds a username to the whitelist", Arrays.asList(
                        SlashCommandOption.create(SlashCommandOptionType.STRING, "username", "Your username", true),
                        SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "isBedrock", "If you are joining on bedrock", false)
                )),
                SlashCommandOption.createWithOptions(SlashCommandOptionType.SUB_COMMAND, "remove", "Removes a username from the whitelist", Arrays.asList(
                        SlashCommandOption.create(SlashCommandOptionType.STRING, "username", "Your username", true),
                        SlashCommandOption.create(SlashCommandOptionType.BOOLEAN, "isBedrock", "If you were joining on bedrock", false)
                ))
        )).createGlobal(discordApi).join();
        discordApi.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
            if (slashCommandInteraction.getFullCommandName().equals("whitelist add")) {
                Optional<SlashCommandInteractionOption> name = slashCommandInteraction.getArgumentByIndex(0);
                Optional<SlashCommandInteractionOption> bedrock = slashCommandInteraction.getArgumentByIndex(1);
                if(name.isEmpty()){
                    slashCommandInteraction.createImmediateResponder()
                            .setContent("Please provide a username!")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();
                    return;
                }
                boolean isBedrock = false;
                if(bedrock.isPresent()){
                    SlashCommandInteractionOption bedrockOption = bedrock.get();
                    Optional<Boolean> optionalBoolean = bedrockOption.getBooleanValue();
                    if(optionalBoolean.isPresent()){
                        isBedrock = optionalBoolean.get();
                    }
                }
                Optional<String> usernameOptional = name.get().getStringValue();
                if(usernameOptional.isEmpty()){
                    slashCommandInteraction.createImmediateResponder()
                            .setContent("Please provide a username!")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();
                    return;
                }
                String username = isBedrock ? '.' + usernameOptional.get() : usernameOptional.get();
                Optional<String[]> optionalArray = database.GetWhitelistedUsers();
                if(optionalArray.isEmpty()){
                    optionalArray = Optional.of(new String[0]);
                }
                String[] array = optionalArray.get();
                ArrayList<String> clone = new ArrayList<>(List.of(array));
                if(clone.contains(username)){
                    // remove any duplicates
                    clone.removeIf(new Predicate<String>() {
                        @Override
                        public boolean test(String s) {
                            return s.equalsIgnoreCase(username);
                        }
                    });
                }
                clone.add(username);
                database.SetWhitelistedUsers(clone.toArray(new String[0]));
                slashCommandInteraction.createImmediateResponder()
                        .setContent("Added to whitelist!")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
            }
            if (slashCommandInteraction.getFullCommandName().equals("whitelist remove")) {
                Optional<SlashCommandInteractionOption> name = slashCommandInteraction.getArgumentByIndex(0);
                Optional<SlashCommandInteractionOption> bedrock = slashCommandInteraction.getArgumentByIndex(1);
                if(name.isEmpty()){
                    slashCommandInteraction.createImmediateResponder()
                            .setContent("Please provide a username!")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();
                    return;
                }
                boolean isBedrock = false;
                if(bedrock.isPresent()){
                    SlashCommandInteractionOption bedrockOption = bedrock.get();
                    Optional<Boolean> optionalBoolean = bedrockOption.getBooleanValue();
                    if(optionalBoolean.isPresent()){
                        isBedrock = optionalBoolean.get();
                    }
                }
                Optional<String> usernameOptional = name.get().getStringValue();
                if(usernameOptional.isEmpty()){
                    slashCommandInteraction.createImmediateResponder()
                            .setContent("Please provide a username!")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();
                    return;
                }
                String username = isBedrock ? '.' + usernameOptional.get() : usernameOptional.get();
                Optional<String[]> optionalArray = database.GetWhitelistedUsers();
                if(optionalArray.isEmpty()){
                    optionalArray = Optional.of(new String[0]);
                }
                String[] array = optionalArray.get();
                ArrayList<String> clone = new ArrayList<>(List.of(array));
                clone.removeIf(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return s.equalsIgnoreCase(username);
                    }
                });
                database.SetWhitelistedUsers(clone.toArray(new String[0]));
                if(clone.size() == array.length){
                    slashCommandInteraction.createImmediateResponder()
                            .setContent("Username not found in whitelist!")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond();
                    return;
                }
                slashCommandInteraction.createImmediateResponder()
                        .setContent("Removed from whitelist!")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
            }
        });
    }
}