package de.fluffeliger.wyldlife.modules.global;

import de.fluffeliger.wyldlife.modules.Module;
import de.fluffeliger.wyldlife.modules.global.commands.*;
import de.fluffeliger.wyldlife.modules.global.listener.JoinListener;
import de.fluffeliger.wyldlife.modules.global.listener.PlayerPreLoginListener;
import de.fluffeliger.wyldlife.modules.global.listener.QuitListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Objects;
import java.util.UUID;

public class GlobalModule extends Module {

    @Override
    public void onEnable() {
        new ReloadConfigCommand(instance, "reloadWyldConfig", "wyld.config.reload");
        new GameModeCommand(instance, "gamemode", "wyld.gamemode");
        new SpawnCommand(instance, "spawn", "wyld.spawn");
        new RedisCommand(instance, "redis", "wyld.redis");

        new BanCommand(instance, "ban", "wyld.ban");

        new PlayerPreLoginListener(instance);
        new JoinListener(instance);
        new QuitListener(instance);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public String getName() {
        return "global";
    }

    public static Location buildSpawn(YamlConfiguration yml) {
        double x = yml.getDouble("system.spawn.x");
        double y = yml.getDouble("system.spawn.y");
        double z = yml.getDouble("system.spawn.z");
        float yaw = (float) yml.getDouble("system.spawn.yaw");
        float pitch = (float) yml.getDouble("system.spawn.pitch");
        World world = Bukkit.getWorld(UUID.fromString(Objects.requireNonNull(yml.getString("system.spawn.world_uuid"))));

        return new Location(world, x, y, z, yaw, pitch);
    }
}
