package de.fluffeliger.wyldlife.modules.global.commands;

import de.fluffeliger.wyldlife.WyldLife;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RedisCommand implements CommandExecutor, TabCompleter {

    String commandName;
    String permission;
    WyldLife instance;

    public RedisCommand(WyldLife instance, String commandName, String permission) {
        this.instance = instance;
        this.commandName = commandName;
        this.permission = permission;

        Objects.requireNonNull(instance.getCommand(commandName)).setExecutor(this);
        Objects.requireNonNull(instance.getCommand(commandName)).setTabCompleter(this);
        if (permission != null) Objects.requireNonNull(instance.getCommand(commandName)).setPermission(permission);
        if (permission != null) Objects.requireNonNull(instance.getCommand(commandName)).setPermissionMessage(WyldLife.parseColors(instance.getConfigurationManager().getYML().getString("system.prefix") + "&7Dir fehlt die Permission &b" + permission));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        StatefulRedisPubSubConnection<String, String> connection = WyldLife.getInstance().getRedisClient().connectPubSub();
        RedisPubSubAsyncCommands<String, String> async = connection.async();
        async.publish(WyldLife.getInstance().getRedisChannel(), "WyldLife:Command:" + cmd.getName() + ":" + sender.getName() + ":" + String.join(" ", args));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> tab = new ArrayList<>();

        return tab;
    }
}
