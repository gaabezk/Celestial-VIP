package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.services.DeactivationService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RemoveVipCommand implements CommandExecutor {

    private final DeactivationService deactivationService = new DeactivationService();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("removervip") && args.length == 2) {
            int num = 0;

            List<Vip> vips = CelestialVIP.getVipRepository().getAllVipsByPlayerNick(args[0],true);

            if(vips.isEmpty()){
                sender.sendMessage("§cEsse jogador nao possui vips ativos!");
                return true;
            }

            for (Vip vip : vips){
                if(vip.getGroup().equals(args[1])){
                    deactivationService.deactivateVip(vip);
                    num++;
                    sender.sendMessage("§aVip removido com sucesso!");
                    return true;
                }
            }
            if(num==0){
                sender.sendMessage("§cEsse jogador nao possui o vip: §f" + args[1]);
                return true;
            }
        }
        return false;
    }
}
