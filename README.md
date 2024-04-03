# velocicordlist
A Velocity plugin that manages a whitelist via. a Discord bot

## Setting up

1. Create a [discord application](https://discord.com/developers/applications)
2. Create a bot
  + This guide assumes you know how to invite the bot
  + Ensure the bot has the SLASH_COMMANDS permission
3. Copy and Paste the token into the `config.toml` file
  + This file is generated once the plugin is run once
4. Start the server again

## Using the bot

+ `/whitelist add [username] [isBedrock]`
  + Adds a username to the whitelist
  + `username` : `String`
    + The username of the player
  + `isBedrock` : `boolean` (OPTIONAL)
    + If the player is going to be joining on Bedrock via. Geyser
    + Adds a `.` to the beginning of the username
    + This defaults to `false`
+ `/whitelist remove [username] [isBedrock]`
  + Removes a username from the whitelist
  + `username` : `String`
    + The username of the player
  + `isBedrock` : `boolean` (OPTIONAL)
    + If the player was added as a bedrock player
    + Adds a `.` to the beginning of the username
    + This defaults to `false`