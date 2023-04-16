package team.unnamed.emojis.resourcepack.writer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.emojis.object.serialization.Streams;
import team.unnamed.emojis.util.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.logging.Level;

public class PackMetaWriter implements Consumer<ResourcePack> {

    private final Plugin plugin;

    public PackMetaWriter(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void accept(ResourcePack resourcePack) {

        String description = ChatColor.translateAlternateColorCodes(
                '&',
                plugin.getConfig().getString("pack.meta.description", "Emojis generated")
        );
        File file = new File(plugin.getDataFolder(), "icon.png");

        if (!file.exists()) {
            plugin.getLogger().warning("Resource-pack icon not found " +
                    "(must be at unemojis/icon.png), using a default one");
            try (OutputStream output = new FileOutputStream(file)) {
                Streams.pipe(
                        plugin.getResource("icon.png"),
                        output
                );
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create a default resource-pack icon", e);
            }
        }

        resourcePack.packMeta(getPackFormatVersion(), description);

        // write icon
        if (file.exists()) {
            resourcePack.icon(Writable.file(file));
        }
    }

    private static int getPackFormatVersion() {
        Version version = Version.CURRENT;
        byte major = version.major();

        if (major != 1) {
            failUnsupportedVersion();
        }

        byte minor = version.minor();

        if (minor < 6) failUnsupportedVersion();
        if (minor < 9) return 1;
        if (minor < 11) return 2;
        if (minor < 13) return 3;
        if (minor < 15) return 4;

        // Minecraft 1.15, 1.16, 1.17 and 1.18 use
        // their minor number - 10 as resource-pack
        // format version, this may change, so we
        // may have to change this later
        if (Bukkit.getBukkitVersion().startsWith("1.19.3")) return 12; // 1.19.3 uses 12
        return minor - 10;
    }

    private static void failUnsupportedVersion() {
        throw new UnsupportedOperationException("Unsupported version: " + Version.CURRENT);
    }

}
