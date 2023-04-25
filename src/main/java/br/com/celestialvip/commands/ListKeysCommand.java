package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.keys.CashKey;
import br.com.celestialvip.models.keys.VipKey;
import br.com.celestialvip.utils.Utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ListKeysCommand implements CommandExecutor {

    FileConfiguration config = CelestialVIP.getPlugin().getConfig();
    String prefix = Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.prefix"))) + " ";


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("listarchaves") && args.length == 1) {
            if(args[0].equalsIgnoreCase("vip")){
                return vip(sender);
            }else if(args[0].equalsIgnoreCase("cash")){
                return cash(sender);
            }
        }
        return false;
    }

    private boolean cash(CommandSender sender) {
        List<CashKey> cashKeys = CelestialVIP.getCashRepository().getAllCashKeys(true);
        if(cashKeys.isEmpty()){
            sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.list_keys_empty"))));
            return true;
        }
        sender.sendMessage("\n"+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.list_keys_header"))));
        Component component;
        for(CashKey cashKey : cashKeys){
            component = Component.text(cashKey.toString())
                    .hoverEvent(HoverEvent.showText(Component.text(Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.copy_key")).replace("{key}",cashKey.getKeyCode())))))
                    .clickEvent(ClickEvent.copyToClipboard(cashKey.getKeyCode()));
            sender.sendMessage(component);
        }
        return true;
    }

    private boolean vip(CommandSender sender) {
        List<VipKey> vipKeys = CelestialVIP.getVipRepository().getAllVipKeys(true);
        if(vipKeys.isEmpty()){
            sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.list_keys_empty"))));
            return true;
        }
        sender.sendMessage("\n"+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.list_keys_header"))));
        Component component;
        for(VipKey vipKey : vipKeys){
            component = Component.text(vipKey.toString())
                    .hoverEvent(HoverEvent.showText(Component.text(Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.copy_key")).replace("{key}",vipKey.getKeyCode())))))
                    .clickEvent(ClickEvent.copyToClipboard(vipKey.getKeyCode()));
            sender.sendMessage(component);
        }
        return true;
    }
}
