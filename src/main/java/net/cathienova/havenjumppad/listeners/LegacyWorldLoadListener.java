package net.cathienova.havenjumppad.listeners;

import net.cathienova.havenjumppad.managers.JumpPadManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LegacyWorldLoadListener implements Listener {
    private final JumpPadManager jumpPadManager;
    private boolean loaded = false;

    public LegacyWorldLoadListener(JumpPadManager manager) {
        this.jumpPadManager = manager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!loaded) {
            jumpPadManager.loadJumpPads();
            loaded = true;
        }
    }
}
