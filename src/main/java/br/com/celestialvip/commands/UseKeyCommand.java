package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.models.keys.CashKey;
import br.com.celestialvip.models.keys.VipKey;
import br.com.celestialvip.utils.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.eclipse.aether.RepositoryException;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class UseKeyCommand implements CommandExecutor {

    FileConfiguration config = CelestialVIP.getPlugin().getConfig();
    String prefix = Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.prefix"))) + " ";


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("usarchave") && args.length == 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.no_player_command"))));
                return true;
            }
            Player player = (Player) sender;
            if(args[0].equalsIgnoreCase("vip")){
                return vip(player,args);
            }else if(args[0].equalsIgnoreCase("cash")){
                return cash(player,args);
            }
        }
        return false;
    }

    private boolean cash(Player player, String[] args) {

        CashKey key = CelestialVIP.getCashRepository().getCashKeyByKeyCode(args[1],true);

        if(key==null){
            player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.key_not_found"))));
            return true;
        }

        try {
            CelestialVIP.getActivationService().activateCash(player,key.getAmountOfCash().toString());
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        key.setUsedBy(player.getName());
        key.setActive(false);
        CelestialVIP.getCashRepository().updateCashKey(key);
        return true;
    }

    private boolean vip(Player player, String[] args) {

        List<Vip> vips = CelestialVIP.getVipRepository().getAllVipsByPlayerNick(player.getName(), true);

        VipKey key = CelestialVIP.getVipRepository().getVipKeyByKeyCode(args[1],true);

        if(key==null){
            player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.key_not_found"))));
            return true;
        }

        for(Vip vip : vips){
            if(vip.getGroup().equals(key.getVipName())){
                player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.vip_already_active")).replace("{vip}",vip.getGroup())));
                return true;
            }
        }

        try {
            CelestialVIP.getActivationService().activateVip(player,key.getVipName(),String.valueOf(key.getDurationInDays()), key.isPermanent());
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        key.setUsedBy(player.getName());
        key.setActive(false);
        CelestialVIP.getVipRepository().updateVipKey(key);
        return true;
    }
}

