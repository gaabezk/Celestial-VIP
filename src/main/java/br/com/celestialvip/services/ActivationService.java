package br.com.celestialvip.services;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.entities.PlayerData;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.eclipse.aether.RepositoryException;

import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class ActivationService {

    private final FileConfiguration config = CelestialVIP.getPlugin().getConfig();

    public void activateVip(Player player, String vipType, String days, boolean isPermanent) throws RepositoryException {
        ConfigurationSection vipSection = config.getConfigurationSection("config.vips." + vipType); // obtém a seção de configuração para o tipo de VIP escolhido

        if (vipSection != null) {

            Vip vip = new Vip();
            vip.setVipDays(Integer.parseInt(days));
            vip.setPlayerNick(player.getName());
            vip.setGroup(vipType);
            vip.setActive(true);
            vip.setPermanent(isPermanent);
            vip.definirDatas();

            PlayerData playerData = CelestialVIP.getPlayerRepository().getPlayerDataByNick(player.getName());
            if (playerData == null) {
                CelestialVIP.getPlayerRepository().savePlayerData(new PlayerData(player.getName(), player.getUniqueId().toString()));
            }
            CelestialVIP.getVipRepository().saveVip(vip);

            List<String> activationCommands = vipSection.getStringList("activation-commands"); // obtém a lista de comandos de ativação para o tipo de VIP escolhido
            for (String command : activationCommands) {

                command = replaceVipVariables(command, player, days, vipType, vipSection);

                if (command.startsWith("[console] ")) {
                    command = command.replace("&", "§");
                    CelestialVIP.getPlugin(CelestialVIP.class).getServer().dispatchCommand(getServer().getConsoleSender(), command.substring(10)); // executa o comando como console
                } else if (command.startsWith("[player] ")) {
                    player.performCommand(command.substring(9)); // executa o comando como jogador
                } else if (command.startsWith("[message] ")) {
                    command = command.replace("&", "§");
                    player.sendMessage(command.substring(10)); // envia a mensagem para o jogador
                } else if (command.startsWith("[sound] ")){
                    player.playSound(player.getLocation(), Sound.valueOf(command.substring(8).toUpperCase()), 1.0f, 1.0f);

                }
            }

            if (config.getBoolean("config.announce.active")) {
                String message = Utilities.translateColorCodes(config.getString("config.prefix") + " " + replaceVipVariables(config.getString("config.announce.chat-and-actionbar.message"), player, days, vipType, vipSection));
                String announceType = config.getString("config.announce.type");

                if(announceType == null){
                    announceType = "chat";
                }

                switch (announceType) {
                    case "chat":
                        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                            p.sendMessage(message);
                        }
                        break;
                    case "actionbar":
                        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                            p.sendActionBar(message);
                        }
                        break;
                    case "title":
                        String title = Utilities.translateColorCodes(replaceVipVariables(config.getString("config.announce.title.title"), player, days, vipType, vipSection));
                        String subTitle = Utilities.translateColorCodes(replaceVipVariables(config.getString("config.announce.title.subtitle"), player, days, vipType, vipSection));
                        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                            p.sendTitle(title, subTitle);
                        }
                        break;
                }
            }
        }
    }

    public void activateCash(Player player, String value) throws RepositoryException {
        ConfigurationSection cashSection = config.getConfigurationSection("config.cash");

        if (cashSection != null) {

            PlayerData playerData = CelestialVIP.getPlayerRepository().getPlayerDataByNick(player.getName());
            if (playerData == null) {
                CelestialVIP.getPlayerRepository().savePlayerData(new PlayerData(player.getName(), player.getUniqueId().toString()));
            }

            List<String> activationCommands = cashSection.getStringList("activation-commands"); // obtém a lista de comandos de ativação para o tipo de VIP escolhido
            for (String command : activationCommands) {

                command = replaceCashVariables(command, player, value);

                if (command.startsWith("[console] ")) {
                    command = command.replace("&", "§");
                    CelestialVIP.getPlugin(CelestialVIP.class).getServer().dispatchCommand(getServer().getConsoleSender(), command.substring(10)); // executa o comando como console
                } else if (command.startsWith("[player] ")) {
                    player.performCommand(command.substring(9)); // executa o comando como jogador
                } else if (command.startsWith("[message] ")) {
                    command = command.replace("&", "§");
                    player.sendMessage(command.substring(10)); // envia a mensagem para o jogador
                } else if (command.startsWith("[sound] ")){
                    player.playSound(player.getLocation(), Sound.valueOf(command.substring(8).toUpperCase()), 1.0f, 1.0f);
                }
            }
        }
    }

    private String replaceVipVariables(String message, Player player, String days, String vipType, ConfigurationSection vipSection) {
        message = message.replace("%player%", player.getName());
        message = message.replace("%days%", days);
        message = message.replace("%group%", vipType);
        message = message.replace("%tag%", Utilities.translateColorCodes(vipSection.getString("tag")));

        return message;
    }

    private String replaceCashVariables(String message, Player player, String value) {
        message = message.replace("%player%", player.getName());
        message = message.replace("%value%", value);

        return message;
    }
}
