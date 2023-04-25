package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.keys.CashKey;
import br.com.celestialvip.models.keys.VipKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DeleteKeyCommand implements CommandExecutor {
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
            sender.sendMessage("§aTodas as chaves vip ativas foram apagadas com sucesso!");
            return true;
        }
        VipKey vipKey = CelestialVIP.getVipRepository().getVipKeyByKeyCode(arg,true);
        if(vipKey == null){
            sender.sendMessage("§cEssa chave nao existe!");
            return true;
        }
        try {
            CelestialVIP.getVipRepository().deleteVipKey(vipKey.getKeyCode());
            sender.sendMessage("§aChave apagada com sucesso!");
        }catch (Exception e){
            sender.sendMessage("§cErro ao deletar a chave: " + e.getMessage());
        }
        return true;
    }

    private boolean cash(String arg, CommandSender sender) {
        if(arg.equals("all")){
            CelestialVIP.getCashRepository().deleteAllCashKeys(true);
            sender.sendMessage("§aTodas as chaves de cash ativas foram apagadas com sucesso!");
            return true;
        }
        CashKey cashKey = CelestialVIP.getCashRepository().getCashKeyByKeyCode(arg,true);
        if(cashKey == null){
            sender.sendMessage("§cEssa chave nao existe!");
            return true;
        }
        try {
            CelestialVIP.getCashRepository().deleteCashKey(cashKey.getKeyCode());
            sender.sendMessage("§aChave apagada com sucesso!");
        }catch (Exception e){
            sender.sendMessage("§cErro ao deletar a chave: " + e.getMessage());
        }
        return true;
    }
}
