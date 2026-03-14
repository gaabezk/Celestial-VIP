package br.com.celestialvip.services;

import br.com.celestialvip.config.PluginConfig;
import br.com.celestialvip.data.repositories.PlayerRepository;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.domain.exception.RepositoryException;
import br.com.celestialvip.models.entities.PlayerData;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.utils.Utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.ZoneId;
import java.util.List;

public class ActivationService {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private final PluginConfig config;
    private final VipRepository vipRepository;
    private final PlayerRepository playerRepository;
    private final JavaPlugin plugin;

    public ActivationService(PluginConfig config, VipRepository vipRepository, PlayerRepository playerRepository, JavaPlugin plugin) {
        this.config = config;
        this.vipRepository = vipRepository;
        this.playerRepository = playerRepository;
        this.plugin = plugin;
    }

    public void activateVip(Player player, String vipType, String days, boolean isPermanent) throws RepositoryException {
        ConfigurationSection vipSection = config.getRaw().getConfigurationSection("config.vips." + vipType);
        if (vipSection == null) {
            return;
        }
        ZoneId zoneId = ZoneId.of(config.getTimezone());

        Vip vip = new Vip();
        vip.setVipDays(Integer.parseInt(days));
        vip.setPlayerNick(player.getName());
        vip.setGroup(vipType);
        vip.setActive(true);
        vip.setPermanent(isPermanent);
        vip.definirDatas(zoneId);

        PlayerData playerData = playerRepository.getPlayerDataByNick(player.getName());
        if (playerData == null) {
            playerRepository.savePlayerData(new PlayerData(player.getName(), player.getUniqueId().toString()));
        }
        vipRepository.saveVip(vip);

        List<String> activationCommands = config.getVipActivationCommands(vipType);
        for (String command : activationCommands) {
            command = replaceVipVariables(command, player.getName(), days, vipType, vipSection);
            executeActivationCommand(command, player);
        }

        if (config.isAnnounceActive()) {
            String message = Utilities.translateColorCodes(config.getPrefix() + " " + replaceVipVariables(config.getAnnounceMessage(), player.getName(), days, vipType, vipSection));
            String announceType = config.getAnnounceType() != null ? config.getAnnounceType() : "chat";
            Component msgComponent = LEGACY.deserialize(message);
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if ("actionbar".equals(announceType)) {
                    p.sendActionBar(msgComponent);
                } else if ("title".equals(announceType)) {
                    String titleStr = Utilities.translateColorCodes(replaceVipVariables(config.getAnnounceTitle(), player.getName(), days, vipType, vipSection));
                    String subStr = Utilities.translateColorCodes(replaceVipVariables(config.getAnnounceSubtitle(), player.getName(), days, vipType, vipSection));
                    p.showTitle(Title.title(LEGACY.deserialize(titleStr), LEGACY.deserialize(subStr)));
                } else {
                    p.sendMessage(msgComponent);
                }
            }
        }
    }

    public void activateCash(Player player, String value) throws RepositoryException {
        ConfigurationSection cashSection = config.getCashSection();
        if (cashSection == null) {
            return;
        }
        PlayerData playerData = playerRepository.getPlayerDataByNick(player.getName());
        if (playerData == null) {
            playerRepository.savePlayerData(new PlayerData(player.getName(), player.getUniqueId().toString()));
        }
        List<String> activationCommands = config.getCashActivationCommands();
        for (String command : activationCommands) {
            command = replaceCashVariables(command, player.getName(), value);
            executeActivationCommand(command, player);
        }
    }

    private void executeActivationCommand(String command, Player player) {
        if (command.startsWith("[console] ")) {
            String finalCmd = command.replace("&", "§").substring(10);
            plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), finalCmd));
        } else if (command.startsWith("[player] ")) {
            player.performCommand(command.substring(9));
        } else if (command.startsWith("[message] ")) {
            player.sendMessage(LEGACY.deserialize(command.replace("&", "§").substring(10)));
        } else if (command.startsWith("[sound] ")) {
            String soundName = command.substring(8).trim().toUpperCase();
            try {
                player.playSound(player.getLocation(), Sound.valueOf(soundName), 1.0f, 1.0f);
            } catch (IllegalArgumentException ignored) {
                // invalid sound name in config
            }
        }
    }

    private String replaceVipVariables(String message, String playerName, String days, String vipType, ConfigurationSection vipSection) {
        message = message.replace("%player%", playerName);
        message = message.replace("%days%", days);
        message = message.replace("%group%", vipType);
        String tag = vipSection.getString("tag");
        message = message.replace("%tag%", tag != null ? Utilities.translateColorCodes(tag) : vipType);
        return message;
    }

    private String replaceCashVariables(String message, String playerName, String value) {
        return message.replace("%player%", playerName).replace("%value%", value);
    }
}
