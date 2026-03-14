package br.com.celestialvip.commands;

import br.com.celestialvip.config.MessageService;
import br.com.celestialvip.data.repositories.CashRepository;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.models.keys.CashKey;
import br.com.celestialvip.models.keys.VipKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class DeleteKeyCommand implements CommandExecutor, TabCompleter {

    private final MessageService messageService;
    private final VipRepository vipRepository;
    private final CashRepository cashRepository;

    public DeleteKeyCommand(MessageService messageService, VipRepository vipRepository, CashRepository cashRepository) {
        this.messageService = messageService;
        this.vipRepository = vipRepository;
        this.cashRepository = cashRepository;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("apagarchave")) {
            return false;
        }
        if (args.length == 0) {
            messageService.send(sender, "usage_delete_key");
            return true;
        }
        if (args.length != 2) {
            return false;
        }
        if (args[0].equalsIgnoreCase("cash")) {
            return cash(args[1], sender);
        }
        if (args[0].equalsIgnoreCase("vip")) {
            return vip(args[1], sender);
        }
        return false;
    }

    private boolean vip(String arg, CommandSender sender) {
        if (arg.equals("all")) {
            vipRepository.deleteAllVipKeys(true);
            messageService.send(sender, "delete_all_keys_success");
            return true;
        }
        VipKey vipKey = vipRepository.getVipKeyByKeyCode(arg, true);
        if (vipKey == null) {
            messageService.send(sender, "key_not_found");
            return true;
        }
        try {
            vipRepository.deleteVipKey(vipKey.getKeyCode());
            messageService.send(sender, "delete_key_success");
        } catch (Exception e) {
            messageService.send(sender, "delete_key_error", Map.of("error_message", e.getMessage() != null ? e.getMessage() : ""));
        }
        return true;
    }

    private boolean cash(String arg, CommandSender sender) {
        if (arg.equals("all")) {
            cashRepository.deleteAllCashKeys(true);
            messageService.send(sender, "delete_all_keys_success");
            return true;
        }
        CashKey cashKey = cashRepository.getCashKeyByKeyCode(arg, true);
        if (cashKey == null) {
            messageService.send(sender, "key_not_found");
            return true;
        }
        try {
            cashRepository.deleteCashKey(cashKey.getKeyCode());
            messageService.send(sender, "delete_key_success");
        } catch (Exception e) {
            messageService.send(sender, "delete_key_error", Map.of("error_message", e.getMessage() != null ? e.getMessage() : ""));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("apagarchave")) {
            return null;
        }
        if (args.length == 1) {
            return List.of("vip", "cash").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("vip")) {
            return List.of("all").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("cash")) {
            return List.of("all").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
