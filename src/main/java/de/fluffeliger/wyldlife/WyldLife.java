package de.fluffeliger.wyldlife;

import io.lettuce.core.*;
import de.fluffeliger.wyldlife.config.ConfigurationManager;
import de.fluffeliger.wyldlife.config.FileManager;
import de.fluffeliger.wyldlife.database.DataBase;
import de.fluffeliger.wyldlife.database.DataBaseConnectReturn;
import de.fluffeliger.wyldlife.modules.ModuleManager;
import de.fluffeliger.wyldlife.modules.chat.ChatModule;
import de.fluffeliger.wyldlife.modules.global.GlobalModule;
import de.fluffeliger.wyldlife.modules.global.listener.RedisListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class WyldLife extends JavaPlugin {

    private static WyldLife instance;
    private final ModuleManager moduleManager = new ModuleManager();
    private final ConfigurationManager configurationManager = new ConfigurationManager();
    private final DataBase dataBase = new DataBase();
    private final String redisChannel = "wyld:life";
    private RedisClient redisClient;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Initializing WyldLife...");

        getLogger().info("Creating redis...");
        redisClient = RedisClient.create("redis://localhost:6379");
        StatefulRedisPubSubConnection<String, String> connection = redisClient.connectPubSub();
        connection.addListener(new RedisListener());

        getLogger().info("Subbing to redis channel " + redisChannel + "...");
        RedisPubSubAsyncCommands<String, String> async = connection.async();
        async.subscribe(redisChannel);

        // Load Configuration
        ConfigurationManager.addDefault("system.prefix", "&b&lSystem &8» ");
        ConfigurationManager.addDefault("system.spawn.x", 0);
        ConfigurationManager.addDefault("system.spawn.y", 64);
        ConfigurationManager.addDefault("system.spawn.z", 0);
        ConfigurationManager.addDefault("system.spawn.yaw", 0);
        ConfigurationManager.addDefault("system.spawn.pitch", 0);
        ConfigurationManager.addDefault("system.spawn.world_uuid", Objects.requireNonNull(Bukkit.getWorld("world")).getUID().toString());
        ConfigurationManager.addDefault("system.database.ip", "127.0.0.1");
        ConfigurationManager.addDefault("system.database.username", "username");
        ConfigurationManager.addDefault("system.database.password", "password");
        ConfigurationManager.addDefault("system.database.database", "database_name");
        ConfigurationManager.addDefault("system.database.server_table_name", "table_name");
        ConfigurationManager.addDefault("system.database.url_template", "jdbc:mysql://{ADDRESS}:3306/?autoReconnect=true&useSSL=false");
        ConfigurationManager.addDefault("groups.default.format", "{PREFIX} &8× &7{DISPLAY_NAME} &8» &7{MESSAGE_CONTENT}");
        ConfigurationManager.addDefault("groups.default.prefix", "&7&lSpieler");
        ConfigurationManager.addDefault("groups.default.colors", false);
        FileManager.init();
        configurationManager.setYmlAndFix(FileManager.getConfig());
        FileManager.setConfig(configurationManager.getYML());

        switch (dataBase.connect(configurationManager.getYML())) {
            case WORKED -> getLogger().info("Connected to database!");
            case ALREADY_CONNECTED -> getLogger().info("Connection already established!");
            default -> getLogger().severe("Could not connect to database!");
        }

        if (dataBase.isConnected()) {
            if (dataBase.setUp().equals(DataBaseConnectReturn.WORKED)) {
                getLogger().info("Database set up!");
            }
            else getLogger().severe("Failed to set up database!");
        }

        // Load Modules
        moduleManager.registerModule(new GlobalModule());
        moduleManager.registerModule(new ChatModule());

        moduleManager.enableAll();

        getLogger().info("Initialization of WyldLife finished!");
    }

    @Override
    public void onDisable() {
        if (dataBase.isConnected()) {
            DataBaseConnectReturn dataBaseConnectReturn = dataBase.disconnect();
            if (dataBaseConnectReturn.equals(DataBaseConnectReturn.WORKED)) getLogger().info("Disconnected from database!");
            else getLogger().info("Cannot disconnect from database! => " + dataBaseConnectReturn);
        }
    }

    public static WyldLife getInstance() {
        return instance;
    }

    public RedisClient getRedisClient() {
        return redisClient;
    }

    public String getRedisChannel() {
        return redisChannel;
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public DataBase.DataBaseManager getDataBase() {
        return new DataBase.DataBaseManager(dataBase.getConnection());
    }

    public static void sendMessage(String message, Player player) {
        player.sendMessage(message.replace('&', '§'));
    }

    public static void sendMessage(String message, CommandSender sender) {
        sender.sendMessage(message.replace('&', '§'));
    }

    public static String parseColors(String message) {
        return message.replace('&', '§');
    }

    public static boolean isOnline(String name) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    public static Player getOnline(String name) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) return player;
        }
        return null;
    }

    public static List<Player> getOnlinePlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public static List<String> getOnlinePlayerNames() {
        List<String> out = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            out.add(online.getName());
        }
        return out;
    }

    public static List<Player> getSavePlayers(Player player) {
        List<Player> out = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (player.canSee(online) && player != online) out.add(online);
        }
        return out;
    }

    public static List<String> getSavePlayersNames(Player player) {
        List<String> out = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (player.canSee(online) && player != online) out.add(online.getName());
        }
        return out;
    }

    public static UUID formatUUID(String plain) {
        String id = plain.substring(0, 8) + "-" + plain.substring(8, 12) + "-" + plain.substring(12, 16) + "-" + plain.substring(16, 20) + "-" + plain.substring(20, 32);
        return UUID.fromString(id);
    }

    /*
    public static class ParameterStringBuilder {
        public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }

            String resultString = result.toString();
            return resultString.length() > 0
                    ? resultString.substring(0, resultString.length() - 1)
                    : resultString;
        }
    }*/

}
