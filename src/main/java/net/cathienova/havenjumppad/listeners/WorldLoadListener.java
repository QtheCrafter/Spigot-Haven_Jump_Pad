package net.cathienova.havenjumppad.listeners;

import net.cathienova.havenjumppad.managers.JumpPadManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldLoadListener implements Listener {
    private final JumpPadManager jumpPadManager;

    public WorldLoadListener(JumpPadManager manager) {
        this.jumpPadManager = manager;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        jumpPadManager.loadJumpPads();
    }
}
