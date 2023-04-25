package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.utils.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class VipInfoCommand implements CommandExecutor {

    FileConfiguration config = CelestialVIP.getPlugin().getConfig();
    String prefix = Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.prefix"))) + " ";


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("infovip")) {
            String nick = args.length==1?args[0]:sender.getName();
            List<Vip> vips = CelestialVIP.getVipRepository().getAllVipsByPlayerNick(nick,true);
            if(vips.isEmpty()){
                sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.vip_not_found")).replace("{player}",nick)));
                return true;
            }
            sender.sendMessage("\n"+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.info_vip_header"))));
            for(Vip vip : vips){
                sender.sendMessage(vip.toString());
            }
            return true;
        }
        return false;
    }
}
