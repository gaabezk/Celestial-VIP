package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.entities.Vip;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VipInfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("infovip")) {
            List<Vip> vips = CelestialVIP.getVipRepository().getAllVipsByPlayerNick(args.length==1?args[0]:sender.getName(),true);
            if(vips.isEmpty()){
                sender.sendMessage("§cNão foi encontrado nenhum vip ativo para: §a"+(args.length==1?args[0]:sender.getName()));
                return true;
            }
            sender.sendMessage("\n§aInformações VIP:");
            for(Vip vip : vips){
                sender.sendMessage(vip.toString());
            }
            return true;
        }
        return false;
    }
}
