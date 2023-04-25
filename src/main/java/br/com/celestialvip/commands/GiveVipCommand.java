package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.eclipse.aether.RepositoryException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GiveVipCommand implements CommandExecutor {

    FileConfiguration config = CelestialVIP.getPlugin().getConfig();
    String prefix = Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.prefix"))) + " ";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("darvip") && args.length == 3) {

            List<String> vipsGroupss = new ArrayList<>(Objects.requireNonNull(CelestialVIP.getPlugin().getConfig().getConfigurationSection("config.vips")).getKeys(false));
            if (!vipsGroupss.contains(args[1])) {
                sender.sendMessage(prefix+ Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.vip_group_not_found")).replace("{vip}",args[1])));
                return true;
            }

            int days = 0;
            boolean isPerm = false;
            String group = args[1];
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

            if(!player.isOnline()){
                sender.sendMessage(prefix+ Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.player_not_online")).replace("{player}", Objects.requireNonNull(player.getName()))));
                return true;
            }

            if (args[2].equalsIgnoreCase("perm")) {
                isPerm = true;
            }else{
                try{
                    days = Integer.parseInt(args[2]);
                    if(days<=0){
                        sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.no_valid_days"))));
                        return true;
                    }
                }catch (Exception e){
                    sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.no_valid_days"))));
                    return true;
                }
            }

            List<Vip> vips = CelestialVIP.getVipRepository().getAllVipsByPlayerNick(player.getName(), true);

            for(Vip vip : vips){
                if(vip.getGroup().equals(group)){
                    sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.vip_already_active_other")).replace("{player}", Objects.requireNonNull(player.getName())).replace("{vip}",vip.getGroup())));
                    return true;
                }
            }

            try {
                CelestialVIP.getActivationService().activateVip((Player) player, group, String.valueOf(days), isPerm);
                return true;
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
