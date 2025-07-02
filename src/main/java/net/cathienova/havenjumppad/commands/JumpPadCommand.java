package net.cathienova.havenjumppad.commands;

import net.cathienova.havenjumppad.managers.JumpPadManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class JumpPadCommand implements CommandExecutor, TabCompleter {
    private final JumpPadManager jumpPadManager;

    public JumpPadCommand(JumpPadManager manager) {
        this.jumpPadManager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by admins.");
            return true;
        }

        Player player = (Player) sender;
        Location targetLocation = getTargetBlockLocation(player, 5);

        if (targetLocation == null) {
            player.sendMessage(jumpPadManager.getLangMessage("not_looking_at_block"));
            return true;
        }

        Material blockType = targetLocation.getBlock().getType();
        if (!blockType.name().contains("PLATE")) {
            player.sendMessage(jumpPadManager.getLangMessage("not_on_plate"));
            return true;
        }

        if (args.length == 1) {
            String action = args[0].toLowerCase();

            switch (action) {
                case "remove":
                    if (jumpPadManager.removeJumpPad(targetLocation)) {
                        player.sendMessage(jumpPadManager.getLangMessage("jumppad_removed"));
                    } else {
                        player.sendMessage(jumpPadManager.getLangMessage("not_a_jumppad"));
                    }
                    return true;

                case "info":
                    var data = jumpPadManager.getStoredJumpPadData(targetLocation);
                    if (data != null) {
                        player.sendMessage(jumpPadManager.getLangMessage("jumppad_info")
                                .replace("{direction}", data.direction)
                                .replace("{velocity}", String.format("%.1f", data.velocity))
                                .replace("{y}", String.format("%.1f", data.yVelocity)));
                    } else {
                        player.sendMessage(jumpPadManager.getLangMessage("not_a_jumppad"));
                    }
                    return true;

                case "disable":
                    if (jumpPadManager.disableJumpPad(targetLocation)) {
                        player.sendMessage(jumpPadManager.getLangMessage("jumppad_disabled"));
                    } else {
                        player.sendMessage(jumpPadManager.getLangMessage("not_a_jumppad"));
                    }
                    return true;

                case "enable":
                    if (jumpPadManager.enableJumpPad(targetLocation)) {
                        player.sendMessage(jumpPadManager.getLangMessage("jumppad_enabled"));
                    } else {
                        player.sendMessage(jumpPadManager.getLangMessage("not_a_jumppad"));
                    }
                    return true;

                default:
                    player.sendMessage(jumpPadManager.getLangMessage("usage"));
                    return true;
            }
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("add")) {
            try {
                String direction = args[1].toLowerCase();
                if (!direction.equals("+x") && !direction.equals("-x") && !direction.equals("+z") && !direction.equals("-z")) {
                    player.sendMessage("§cInvalid direction! Use +x, -x, +z, or -z.");
                    return true;
                }
                double velocity = Double.parseDouble(args[2]);
                double yVelocity = 1.0; // Default Y velocity
                if (args.length == 4) {
                    yVelocity = Double.parseDouble(args[3]);
                }
                jumpPadManager.addJumpPad(targetLocation, direction, velocity, yVelocity);
                player.sendMessage(jumpPadManager.getLangMessage("jumppad_added")
                                           .replace("{direction}", direction)
                                           .replace("{velocity}", String.valueOf(velocity))
                                           .replace("{y}", String.valueOf(yVelocity)));
            } catch (NumberFormatException e) {
                player.sendMessage(jumpPadManager.getLangMessage("invalid_number"));
            }
            return true;
        }

        player.sendMessage(jumpPadManager.getLangMessage("usage"));
        return true;
    }

    private Location getTargetBlockLocation(Player player, int maxDistance) {
        BlockIterator iterator = new BlockIterator(player, maxDistance);
        while (iterator.hasNext()) {
            Block block = iterator.next();

            if (!isAir(block.getType())) {
                return block.getLocation();
            }
        }
        return null;
    }

    private boolean isAir(Material material) {
        // 1.13+ has dedicated "isAir()" method, but 1.8 does not
        return material == Material.AIR || material.name().equals("CAVE_AIR") || material.name().equals("VOID_AIR");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }

        if (args.length == 1) {
            completions.add("add");
            completions.add("remove");
            completions.add("info");
            completions.add("disable");
            completions.add("enable");
        }

        return completions;
    }
}
