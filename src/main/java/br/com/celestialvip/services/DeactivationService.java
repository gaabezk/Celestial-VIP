package br.com.celestialvip.services;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.TimerTask;

import static org.bukkit.Bukkit.getLogger;

public class DeactivationService extends TimerTask {
    private final FileConfiguration config = CelestialVIP.getPlugin().getConfig();

    @Override
    public void run() {

        getLogger().info("\033[93m[CelestialVIP] \033[92mBuscando por VIPS expirados...\033[0m");
        int expireds = 0;
        List<Vip> vips = CelestialVIP.getVipRepository().getAllVips(true,false);
        for (Vip vip : vips) {
            if (vip.isVipExpired()) {
                expireds++;
                deactivateVip(vip);
            }
        }
        if(expireds==0){
            getLogger().info("\033[93m[CelestialVIP] \033[92mNenhum VIP expirado encontrado.\033[0m");
        }else{
            getLogger().info("\033[93m[CelestialVIP] \033[92mForam encontrados \033[93m"+expireds+"\033[92m VIPS expirados.\033[0m");
        }
    }

    public void deactivateVip(Vip vip){
        ConfigurationSection vipSection = config.getConfigurationSection("config.vips." + vip.getGroup());
        if (vipSection != null) {
            vip.setActive(false);
            CelestialVIP.getVipRepository().updateVip(vip);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(vip.getPlayerNick());
            List<String> activationCommands = vipSection.getStringList("after-expiration-commands");

            for (String command : activationCommands) {
                command = replaceVipVariables(command, offlinePlayer.getName(), "" + vip.getVipDays(), vip.getGroup(), vipSection);
                if (command.startsWith("[console] ")) {
                    command = command.replace("&", "§");
                    String finalCommand = command;
                    Bukkit.getScheduler().runTask(CelestialVIP.getPlugin(CelestialVIP.class), () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand.substring(10));
                    });

                } else if (command.startsWith("[player] ")) {
                    if (offlinePlayer.isOnline()) {
                        Bukkit.getPlayer(offlinePlayer.getName()).performCommand(command.substring(9));
                    }
                } else if (command.startsWith("[message] ")) {
                    if (offlinePlayer.isOnline()) {
                        command = command.replace("&", "§");
                        Bukkit.getPlayer(offlinePlayer.getName()).sendMessage(command.substring(10));
                    }
                } else if (command.startsWith("[sound] ")){
                    if (offlinePlayer.isOnline()) {
                        Bukkit.getPlayer(offlinePlayer.getName()).playSound(Bukkit.getPlayer(offlinePlayer.getName()).getLocation(), Sound.valueOf(command.substring(8).toUpperCase()), 1.0f, 1.0f);
                    }
                }
            }
        }
    }

    private String replaceVipVariables(String message, String playerNick, String days, String vipType, ConfigurationSection vipSection) {
        message = message.replace("%player%", playerNick);
        message = message.replace("%days%", days);
        message = message.replace("%group%", vipType);
        message = message.replace("%tag%", Utilities.translateColorCodes(vipSection.getString("tag")));

        return message;
    }

}
