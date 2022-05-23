package de.fluffeliger.wyldlife.modules;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    public List<Module> modules = new ArrayList<>();

    public boolean isRegistered(Module module) {
        return modules.contains(module);
    }

    public void registerModule(Module module) {
        if (isRegistered(module)) return;
        modules.add(module);
        Bukkit.getLogger().info("Registered module: " + module.getName());
    }

    public void enableAll() {
        for (Module module : modules) {
            try {
                if (module.enabled) {
                    Bukkit.getLogger().info("Skipped enabling module: " + module.getName());
                    continue;
                }
                enable(module);
                Bukkit.getLogger().info("Enabled module: " + module.getName());
            }
            catch (Exception e) {
                Bukkit.getLogger().info("Failed enabling module: " + module.getName());
            }
        }
    }

    public void disableAll() {
        for (Module module : modules) {
            try {
                if (!module.enabled) {
                    Bukkit.getLogger().info("Skipped disabling module: " + module.getName());
                    continue;
                }
                disable(module);
                Bukkit.getLogger().info("Disabled module: " + module.getName());
            }
            catch (Exception e) {
                Bukkit.getLogger().info("Failed disabling module: " + module.getName());
            }
        }
    }

    public Module getModule(String name) {
        return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void disable(String name) {
        Module module = getModule(name);
        if (module == null) return;
        disable(module);
    }

    public void enable(String name) {
        Module module = getModule(name);
        if (module == null) return;
        enable(module);
    }

    public void disable(Module module) {
        if (!module.enabled) return;
        module.enabled = false;
        module.onDisable();
    }

    public void enable(Module module) {
        if (module.enabled) return;
        module.enabled = true;
        module.onEnable();
    }

}
