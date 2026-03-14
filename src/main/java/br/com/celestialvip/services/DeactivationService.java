package br.com.celestialvip.services;

import br.com.celestialvip.config.PluginConfig;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.utils.Utilities;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.ZoneId;
import java.util.List;

/**
 * Runnable to be executed on the main thread via Bukkit.getScheduler().runTaskTimer(...).
 */
public class DeactivationService implements Runnable {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private final PluginConfig config;
    private final VipRepository vipRepository;
    private final JavaPlugin plugin;

    public DeactivationService(PluginConfig config, VipRepository vipRepository, JavaPlugin plugin) {
        this.config = config;
        this.vipRepository = vipRepository;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        ZoneId zoneId = ZoneId.of(config.getTimezone());
        List<Vip> vips = vipRepository.getAllVips(true, false);
        int expireds = 0;
        for (Vip vip : vips) {
            if (vip.isVipExpired(zoneId)) {
                expireds++;
                deactivateVip(vip);
            }
        }
        if (expireds == 0) {
            plugin.getLogger().info("\033[32m[CelestialVIP] Nenhum VIP expirado encontrado.\033[0m");
        } else {
            plugin.getLogger().info("\033[32m[CelestialVIP] Foram encontrados " + expireds + " VIPS expirados.\033[0m");
        }
    }

    public void deactivateVip(Vip vip) {
        ConfigurationSection vipSection = config.getRaw().getConfigurationSection("config.vips." + vip.getGroup());
        if (vipSection == null) {
            // If the VIP group was removed from config, still deactivate the VIP record.
            vip.setActive(false);
            vipRepository.updateVip(vip);
            plugin.getLogger().warning("\033[33mVIP group '" + vip.getGroup() + "' não existe mais na config; desativando VIP do jogador " + vip.getPlayerNick() + ".\033[0m");
            return;
        }
        vip.setActive(false);
        vipRepository.updateVip(vip);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(vip.getPlayerNick());
        List<String> commands = config.getVipAfterExpirationCommands(vip.getGroup());

        // Notify the player if online
        if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
            offlinePlayer.getPlayer().sendMessage(LEGACY.deserialize("§cSeu VIP '" + vip.getGroup() + "' expirou!"));
        }

        for (String command : commands) {
            command = replaceVipVariables(command, offlinePlayer.getName(), String.valueOf(vip.getVipDays()), vip.getGroup(), vipSection);
            if (command.startsWith("[console] ")) {
                String finalCommand = command.replace("&", "§").substring(10);
                Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand));
            } else if (command.startsWith("[player] ") && offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().performCommand(command.substring(9));
            } else if (command.startsWith("[message] ") && offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                offlinePlayer.getPlayer().sendMessage(LEGACY.deserialize(command.replace("&", "§").substring(10)));
            } else if (command.startsWith("[sound] ") && offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
                try {
                    offlinePlayer.getPlayer().playSound(offlinePlayer.getPlayer().getLocation(), Sound.valueOf(command.substring(8).trim().toUpperCase()), 1.0f, 1.0f);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    private String replaceVipVariables(String message, String playerNick, String days, String vipType, ConfigurationSection vipSection) {
        message = message.replace("%player%", playerNick);
        message = message.replace("%days%", days);
        message = message.replace("%group%", vipType);
        String tag = vipSection.getString("tag");
        message = message.replace("%tag%", tag != null ? Utilities.translateColorCodes(tag) : vipType);
        return message;
    }
}
