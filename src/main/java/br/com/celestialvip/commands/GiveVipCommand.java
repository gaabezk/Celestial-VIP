package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.entities.Vip;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eclipse.aether.RepositoryException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GiveVipCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("darvip") && args.length == 3) {

            List<String> vipsGroupss = new ArrayList<>(Objects.requireNonNull(CelestialVIP.getPlugin().getConfig().getConfigurationSection("config.vips")).getKeys(false));
            if (!vipsGroupss.contains(args[1])) {
                sender.sendMessage("Grupo vip nao existe!");
                return true;
            }

            int days = 0;
            boolean isPerm = false;
            String group = args[1];
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

            if(!player.isOnline()){
                sender.sendMessage("§f"+player.getName()+" §cprecisa estar online para ativar o vip!");
                return true;
            }

            if (args[2].equalsIgnoreCase("perm")) {
                isPerm = true;
            }else{
                try{
                    days = Integer.parseInt(args[2]);
                    if(days<=0){
                        sender.sendMessage("Coloque um numero de dias válido!");
                        return true;
                    }
                }catch (Exception e){
                    sender.sendMessage("Coloque um numero de dias válido!");
                    return true;
                }
            }

            List<Vip> vips = CelestialVIP.getVipRepository().getAllVipsByPlayerNick(player.getName(), true);

            for(Vip vip : vips){
                if(vip.getGroup().equals(group)){
                    sender.sendMessage("§f"+player.getName()+" §cjá esta no vip §f" + vip.getGroup());
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
