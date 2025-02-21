package net.cathienova.havenjumppad.listeners;

import net.cathienova.havenjumppad.managers.JumpPadManager;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;

public class JumpPadBreakListener implements Listener {
    private final JumpPadManager jumpPadManager;

    public JumpPadBreakListener(JumpPadManager manager) {
        this.jumpPadManager = manager;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location loc = block.getLocation();

        if (isPressurePlate(block.getType())) {
            if (jumpPadManager.removeJumpPad(loc)) {
                Player player = event.getPlayer();
                player.sendMessage(jumpPadManager.getLangMessage("jumppad_removed"));
            }
        }
    }

    private boolean isPressurePlate(Material material) {
        String name = material.name();
        return name.contains("PLATE");
    }
}
