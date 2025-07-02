package net.cathienova.havenjumppad.managers;

import net.cathienova.havenjumppad.HavenJumpPad;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JumpPadManager {
    private final HavenJumpPad plugin;
    private final Map<Location, JumpPadData> jumpPads = new HashMap<>();
    private final Set<Location> disabledJumpPads = new HashSet<>();
    private File jumpPadFile;
    private FileConfiguration jumpPadConfig;
    private FileConfiguration langConfig;

    // New class to store direction and velocities
    public static class JumpPadData {
        public final String axis; // 'x' or 'z'
        public final double velocity;
        public final double yVelocity;
        public JumpPadData(String axis, double velocity, double yVelocity) {
            this.axis = axis;
            this.velocity = velocity;
            this.yVelocity = yVelocity;
        }
    }

    public JumpPadManager(HavenJumpPad plugin) {
        this.plugin = plugin;
        setupFiles();
        loadJumpPads();
        loadLangFile();
    }

    private void setupFiles() {
        // Setup Jump Pads file
        jumpPadFile = new File(plugin.getDataFolder(), "jump_pads.yml");
        if (!jumpPadFile.exists()) {
            try {
                jumpPadFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create jump_pads.yml!");
                e.printStackTrace();
            }
        }
        jumpPadConfig = YamlConfiguration.loadConfiguration(jumpPadFile);
    }

    public void loadJumpPads() {
        jumpPads.clear();
        disabledJumpPads.clear();

        if (jumpPadConfig.contains("jumpPads")) {
            for (String key : jumpPadConfig.getConfigurationSection("jumpPads").getKeys(false)) {
                String[] parts = key.split(",");
                if (parts.length < 4) continue;

                String worldName = parts[0];
                if (Bukkit.getWorld(worldName) == null) continue;

                Location loc = new Location(
                        Bukkit.getWorld(worldName),
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3])
                );

                String axis = jumpPadConfig.getString("jumpPads." + key + ".axis", "x");
                double velocity = jumpPadConfig.getDouble("jumpPads." + key + ".velocity", 1.0);
                double yVelocity = jumpPadConfig.getDouble("jumpPads." + key + ".yVelocity", 1.0);
                boolean isDisabled = jumpPadConfig.getBoolean("jumpPads." + key + ".disabled", false);

                jumpPads.put(loc, new JumpPadData(axis, velocity, yVelocity));
                if (isDisabled) {
                    disabledJumpPads.add(loc);
                }
            }
        }
    }

    public void saveJumpPads() {
        jumpPadConfig.set("jumpPads", null);

        for (Map.Entry<Location, JumpPadData> entry : jumpPads.entrySet()) {
            Location loc = entry.getKey();
            if (loc.getWorld() == null) continue;
            JumpPadData data = entry.getValue();
            String key = loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
            jumpPadConfig.set("jumpPads." + key + ".axis", data.axis);
            jumpPadConfig.set("jumpPads." + key + ".velocity", data.velocity);
            jumpPadConfig.set("jumpPads." + key + ".yVelocity", data.yVelocity);
            jumpPadConfig.set("jumpPads." + key + ".disabled", disabledJumpPads.contains(loc));
        }

        try {
            jumpPadConfig.save(jumpPadFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save jump_pads.yml!");
            e.printStackTrace();
        }
    }

    public void addJumpPad(Location loc, String axis, double velocity, double yVelocity) {
        if (loc.getWorld() == null) {
            plugin.getLogger().warning("Tried to add a Jump Pad in a null world!");
            return;
        }
        jumpPads.put(loc, new JumpPadData(axis, velocity, yVelocity));
        disabledJumpPads.remove(loc);
        saveJumpPads();
    }

    public boolean removeJumpPad(Location loc) {
        if (jumpPads.remove(loc) != null) {
            disabledJumpPads.remove(loc);
            saveJumpPads();
            return true;
        }
        return false;
    }

    public boolean disableJumpPad(Location loc) {
        if (jumpPads.containsKey(loc)) {
            disabledJumpPads.add(loc);
            saveJumpPads();
            return true;
        }
        return false;
    }

    public boolean enableJumpPad(Location loc) {
        if (disabledJumpPads.contains(loc)) {
            disabledJumpPads.remove(loc);
            saveJumpPads();
            return true;
        }
        return false;
    }

    public Vector getJumpPadVelocity(Location loc, Player player) {
        if (disabledJumpPads.contains(loc)) return null;
        JumpPadData data = jumpPads.get(loc);
        if (data == null) return null;
        Vector velocity = new Vector(0, 0, 0);
        Vector facing = player.getLocation().getDirection();
        if (data.axis.equals("x")) {
            velocity.setX(facing.getX() >= 0 ? data.velocity : -data.velocity);
        } else if (data.axis.equals("z")) {
            velocity.setZ(facing.getZ() >= 0 ? data.velocity : -data.velocity);
        }
        velocity.setY(data.yVelocity);
        return velocity;
    }

    public JumpPadData getStoredJumpPadData(Location loc) {
        return jumpPads.get(loc);
    }

    public void loadLangFile() {
        String lang = plugin.getConfig().getString("settings.language", "en_us");
        File langFile = new File(plugin.getDataFolder(), "lang/" + lang + ".yml");

        if (!langFile.exists()) {
            plugin.saveResource("lang/" + lang + ".yml", false);
        }

        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public String getLangMessage(String key) {
        if (langConfig == null) {
            loadLangFile();
        }

        String prefix = plugin.getConfig().getString("prefix", "§l§x§c§5§8§a§e§dHaven JumpPad §r» ");
        return prefix + langConfig.getString("messages." + key, "§cMissing lang entry: " + key);
    }
}
