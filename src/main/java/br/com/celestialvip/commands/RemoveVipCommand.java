package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.services.DeactivationService;
import br.com.celestialvip.utils.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RemoveVipCommand implements CommandExecutor {

    private final DeactivationService deactivationService = new DeactivationService();
    FileConfiguration config = CelestialVIP.getPlugin().getConfig();
    String prefix = Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.prefix"))) + " ";


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("removervip") && args.length == 2) {
            int num = 0;

            List<Vip> vips = CelestialVIP.getVipRepository().getAllVipsByPlayerNick(args[0],true);

            if(vips.isEmpty()){
                sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.vip_not_found")).replace("{player}",args[0])));
                return true;
            }

            for (Vip vip : vips){
                if(vip.getGroup().equals(args[1])){
                    deactivationService.deactivateVip(vip);
                    num++;
                    sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.remove_vip_success"))));
                    return true;
                }
            }
            if(num==0){
                sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.vip_not_active_other")).replace("{player}",args[0]).replace("{vip}",args[1])));
                return true;
            }
        }
        return false;
    }
}
