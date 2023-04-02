package br.com.celestialvip.services;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.TimerTask;

import static org.bukkit.Bukkit.getLogger;

public class DeactivationService extends TimerTask {

    private final VipRepository vipRepository;
    private final FileConfiguration config;

    public DeactivationService(DataSource dataSource, FileConfiguration config) {
        this.vipRepository = new VipRepository(dataSource, config);
        this.config = config;
    }

    private Boolean vipExpired(LocalDate expires) {
        if (LocalDate.now().isAfter(expires)) {
            return true;
        }
        return false;
    }
    @Override
    public void run() {
        getLogger().info("§aBuscando vips expirados...");
        int expireds = 0;
        List<Vip> vips = vipRepository.getAllVips(true);
        for (Vip vip : vips){
            if(vipExpired(vip.getExpirationDate())){
                expireds++;
                ConfigurationSection vipSection = config.getConfigurationSection("config.vips."+vip.getGroup());
                if(vipSection != null){
                    vip.setActive(false);
                    vipRepository.updateVip(vip);
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(vip.getPlayerNick());
                    List<String> activationCommands = vipSection.getStringList("after-expiration-commands");

                    for (String command : activationCommands) {
                        command = replaceVipVariables(command,offlinePlayer.getName(),""+vip.getVipDays(),vip.getGroup(),vipSection);
                        if (command.startsWith("[console] ")) {
                            command = command.replace("&", "§");
                            String finalCommand = command;
                            Bukkit.getScheduler().runTask(CelestialVIP.getPlugin(CelestialVIP.class), () -> {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand.substring(10));
                            });

                        } else if (command.startsWith("[player] ")) {
                            if(offlinePlayer.isOnline()){
                                Bukkit.getPlayer(offlinePlayer.getName()).performCommand(command.substring(9));
                            }
                        } else if (command.startsWith("[message] ")) {
                            if(offlinePlayer.isOnline()){
                                command = command.replace("&", "§");
                                Bukkit.getPlayer(offlinePlayer.getName()).sendMessage(command.substring(10));
                            }
                        }
                    }
                }
            }
        }
        getLogger().info("§aForam encontrados §e"+expireds+" vips expirados.");
    }

    private String replaceVipVariables(String message, String playerNick, String days, String vipType, ConfigurationSection vipSection) {
        message = message.replace("%player%", playerNick);
        message = message.replace("%days%", days);
        message = message.replace("%group%", vipType);
        message = message.replace("%tag%", Utilities.translateColorCodes(vipSection.getString("tag")));

        return message;
    }

}
