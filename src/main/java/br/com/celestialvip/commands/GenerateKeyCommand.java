package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.keys.CashKey;
import br.com.celestialvip.models.keys.VipKey;
import br.com.celestialvip.utils.Utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class GenerateKeyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (cmd.getName().equalsIgnoreCase("gerarchave") && args.length >= 2) {

            if(args[0].equalsIgnoreCase("vip") && args.length == 3){
                return vip(sender,args);
            }else if(args[0].equalsIgnoreCase("cash") && args.length == 2){
                return cash(sender,args);
            }
        }
        return false;
    }

    private boolean cash(CommandSender sender, String[] args) {
        try {
            CashKey cashKey = new CashKey(
                    Utilities.generateSecureRandomString(CelestialVIP.getPlugin().getConfig().getInt("config.key-size")),
                    null,
                    Integer.parseInt(args[1]),
                    true,
                    LocalDate.now()
            );
            CelestialVIP.getCashRepository().saveCashKey(cashKey);

            Component component = Component.text(cashKey.toString())
                    .hoverEvent(HoverEvent.showText(Component.text("Clique para copiar a chave: "+cashKey.getKeyCode())))
                    .clickEvent(ClickEvent.copyToClipboard(cashKey.getKeyCode()));
            sender.sendMessage("\n§aChave gerada com sucesso!");
            sender.sendMessage(component);

        } catch (Exception e) {
            Bukkit.getLogger().info(("Erro ao criar a chave cash: " + e.getMessage()));
        }
        return true;
    }

    private boolean vip(CommandSender sender, String[] args) {
        Set<String> vipKeys = Objects.requireNonNull(CelestialVIP.getPlugin().getConfig().getConfigurationSection("config.vips")).getKeys(false);

        List<String> vipsGroups = new ArrayList<>(vipKeys);
        boolean isPerm = false;
        int days = 0;
        VipKey vipKey;
        if (vipsGroups.contains(args[1])) {

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

            try {
                vipKey = new VipKey(
                        Utilities.generateSecureRandomString(CelestialVIP.getPlugin().getConfig().getInt("config.key-size")),
                        null,
                        args[1],
                        isPerm ? 0 : days,
                        true,
                        isPerm,
                        LocalDate.now());
                CelestialVIP.getVipRepository().saveVipKey(vipKey);

                Component component = Component.text(vipKey.toString())
                        .hoverEvent(HoverEvent.showText(Component.text("Clique para copiar a chave: "+vipKey.getKeyCode())))
                        .clickEvent(ClickEvent.copyToClipboard(vipKey.getKeyCode()));
                sender.sendMessage("\n§aChave gerada com sucesso!");
                sender.sendMessage(component);

            } catch (Exception e) {
                Bukkit.getLogger().info(("Erro ao criar a chave vip: " + e.getMessage()));
            }
        } else {
            sender.sendMessage("Erro: o grupo VIP " + args[0] + " não foi encontrado. Verifique se digitou corretamente e se o grupo está definido no plugin.");
        }
        return true;
    }
}
