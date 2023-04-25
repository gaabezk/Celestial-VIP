package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.keys.CashKey;
import br.com.celestialvip.models.keys.VipKey;
import br.com.celestialvip.utils.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DeleteKeyCommand implements CommandExecutor {

    FileConfiguration config = CelestialVIP.getPlugin().getConfig();
    String prefix = Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.prefix"))) + " ";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("apagarchave") && args.length == 2) {

            if(args[0].equalsIgnoreCase("cash")){
                return cash(args[1],sender);
            }else if(args[0].equalsIgnoreCase("vip")){
                return vip(args[1],sender);
            }
        }
        return false;
    }

    private boolean vip(String arg, CommandSender sender) {
        if(arg.equals("all")){
            CelestialVIP.getVipRepository().deleteAllVipKeys(true);
            sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.delete_all_keys_success"))));
            return true;
        }
        VipKey vipKey = CelestialVIP.getVipRepository().getVipKeyByKeyCode(arg,true);
        if(vipKey == null){
            sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.key_not_found"))));
            return true;
        }
        try {
            CelestialVIP.getVipRepository().deleteVipKey(vipKey.getKeyCode());
            sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.delete_key_success"))));
        }catch (Exception e){
            sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull((config.getString("config.messages.delete_key_error"))).replace("{error_message}",e.getMessage())));
        }
        return true;
    }

    private boolean cash(String arg, CommandSender sender) {
        if(arg.equals("all")){
            CelestialVIP.getCashRepository().deleteAllCashKeys(true);
            sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.delete_all_keys_success"))));
            return true;
        }
        CashKey cashKey = CelestialVIP.getCashRepository().getCashKeyByKeyCode(arg,true);
        if(cashKey == null){
            sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.key_not_found"))));
            return true;
        }
        try {
            CelestialVIP.getCashRepository().deleteCashKey(cashKey.getKeyCode());
            sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.delete_key_success"))));
        }catch (Exception e){
            sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull((config.getString("config.messages.delete_key_error"))).replace("{error_message}",e.getMessage())));
        }
        return true;
    }
}
