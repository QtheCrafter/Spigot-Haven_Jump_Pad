package net.cathienova.havenjumppad.listeners;

import net.cathienova.havenjumppad.managers.JumpPadManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class JumpPadListener implements Listener {
    private final JumpPadManager jumpPadManager;

    public JumpPadListener(JumpPadManager manager) {
        this.jumpPadManager = manager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation().getBlock().getLocation();

        if (!loc.getBlock().getType().name().contains("PLATE")) {
            return;
        }

        Vector velocity = jumpPadManager.getJumpPadVelocity(loc, player);
        if (velocity != null) {
            if (!player.hasPermission("jumppad.use")) {
                player.sendMessage(jumpPadManager.getLangMessage("no_permission"));
                return;
            }
            player.setVelocity(velocity);
        }
    }
}
