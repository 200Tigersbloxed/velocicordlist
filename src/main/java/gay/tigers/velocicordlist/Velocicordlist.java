package gay.tigers.velocicordlist;

import com.google.inject.Inject;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import gay.tigers.velocicordlist.Databasing.IDatabase;
import gay.tigers.velocicordlist.Databasing.JsonDatabase;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Plugin(
        id = "velocicordlist",
        name = "velocicordlist",
        version = "1.0"
)
public class Velocicordlist {
    @Inject
    private Logger logger;

    final private Component kickMessage = Component.text("You are not whitelisted on this server!");

    boolean loaded = true;
    private Config config;
    private IDatabase database;
    private DiscordBot discordBot;

    private void KickPlayer(Player player){
        player.disconnect(kickMessage);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        LocalDateTime start = LocalDateTime.now();
        logger.debug("Loading Config...");
        config = new Config(this, logger);
        if(!loaded){
            return;
        }
        logger.debug("Loaded Config!");
        logger.debug("Connecting to database...");
        switch (config.Current.database.toLowerCase()){
            case "json":
                database = new JsonDatabase(logger);
                break;
            default:
                logger.info("Invalid database (" + config.Current.database + ") provided! Assuming default");
                database = new JsonDatabase(logger);
                break;
        }
        if(!database.connect()) {
            loaded = false;
            logger.error("Failed to connect to database!");
            return;
        }
        logger.debug("Connected to database!");
        logger.debug("Connecting to Discord...");
        discordBot = new DiscordBot(config.Current.discordToken, database);
        logger.debug("Connected to Discord!");
        logger.info("Ready in " + ChronoUnit.MILLIS.between(start, LocalDateTime.now()) + "ms!");
    }

    @Subscribe
    public void onLogin(LoginEvent event){
        if(!loaded){
            return;
        }
        Player player = event.getPlayer();
        String username = player.getUsername();
        Optional<String[]> optionalArray = database.GetWhitelistedUsers();
        if(optionalArray.isEmpty()){
            KickPlayer(player);
            return;
        }
        String[] whitelistedUsers = optionalArray.get();
        boolean contains = false;
        for (String u : whitelistedUsers){
            if(Objects.equals(u.toLowerCase(), username.toLowerCase())){
                contains = true;
                break;
            }
        }
        if(!contains){
            KickPlayer(player);
        }
    }
}
