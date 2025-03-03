package net.cathienova.havenjumppad;

import net.cathienova.havenjumppad.commands.JumpPadCommand;
import net.cathienova.havenjumppad.listeners.JumpPadBreakListener;
import net.cathienova.havenjumppad.listeners.JumpPadListener;
import net.cathienova.havenjumppad.listeners.LegacyWorldLoadListener;
import net.cathienova.havenjumppad.listeners.WorldLoadListener;
import net.cathienova.havenjumppad.managers.JumpPadManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class HavenJumpPad extends JavaPlugin {
    private JumpPadManager jumpPadManager;
    private Logger logger;

    @Override
    public void onEnable() {
        this.logger = getLogger();

        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        jumpPadManager = new JumpPadManager(this);

        getServer().getPluginManager().registerEvents(new JumpPadListener(jumpPadManager), this);
        getServer().getPluginManager().registerEvents(new JumpPadBreakListener(jumpPadManager), this);

        JumpPadCommand jumpPadCommand = new JumpPadCommand(jumpPadManager);
        getCommand("jumppad").setExecutor(jumpPadCommand);
        getCommand("jumppad").setTabCompleter(jumpPadCommand);

        if (isModernVersion()) {
            getServer().getPluginManager().registerEvents(new WorldLoadListener(jumpPadManager), this);
        } else {
            getServer().getPluginManager().registerEvents(new LegacyWorldLoadListener(jumpPadManager), this);
        }
        Bukkit.getConsoleSender().sendMessage("§6===================================");
        Bukkit.getConsoleSender().sendMessage("§5   Haven §2JumpPad Initialized       ");
        Bukkit.getConsoleSender().sendMessage("§6===================================");
    }

    @Override
    public void onDisable() {
        jumpPadManager.saveJumpPads();
        logger.info("§5Haven §2JumpPad §cdisabled.");
    }

    private boolean isModernVersion() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        String[] split = version.split("\\.");
        int major = Integer.parseInt(split[0]);
        int minor = Integer.parseInt(split[1]);

        return major > 1 || minor >= 13; // 1.13+
    }
}
