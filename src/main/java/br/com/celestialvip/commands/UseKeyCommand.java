package br.com.celestialvip.commands;

import br.com.celestialvip.config.MessageService;
import br.com.celestialvip.data.repositories.CashRepository;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.models.keys.CashKey;
import br.com.celestialvip.models.keys.VipKey;
import br.com.celestialvip.services.ActivationService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class UseKeyCommand implements CommandExecutor, TabCompleter {

    private final MessageService messageService;
    private final VipRepository vipRepository;
    private final CashRepository cashRepository;
    private final ActivationService activationService;

    public UseKeyCommand(MessageService messageService, VipRepository vipRepository, CashRepository cashRepository,
                         ActivationService activationService) {
        this.messageService = messageService;
        this.vipRepository = vipRepository;
        this.cashRepository = cashRepository;
        this.activationService = activationService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("usarchave")) {
            return false;
        }
        if (args.length == 0) {
            messageService.send(sender, "usage_use_key");
            return true;
        }
        if (args.length != 2) {
            return false;
        }
        if (!(sender instanceof Player player)) {
            messageService.send(sender, "no_player_command");
            return true;
        }
        if (args[0].equalsIgnoreCase("vip")) {
            return vip(player, args);
        }
        if (args[0].equalsIgnoreCase("cash")) {
            return cash(player, args);
        }
        return false;
    }

    private boolean cash(Player player, String[] args) {
        CashKey key = cashRepository.getCashKeyByKeyCode(args[1], true);
        if (key == null) {
            messageService.send(player, "key_not_found");
            return true;
        }
        try {
            activationService.activateCash(player, String.valueOf(key.getAmountOfCash()));
        } catch (Exception e) {
            messageService.sendRaw(player, "&cErro ao ativar cash: " + e.getMessage());
            return true;
        }
        key.setUsedBy(player.getName());
        key.setActive(false);
        cashRepository.updateCashKey(key);
        return true;
    }

    private boolean vip(Player player, String[] args) {
        List<Vip> vips = vipRepository.getAllVipsByPlayerNick(player.getName(), true);
        VipKey key = vipRepository.getVipKeyByKeyCode(args[1], true);
        if (key == null) {
            messageService.send(player, "key_not_found");
            return true;
        }
        for (Vip vip : vips) {
            if (vip.getGroup().equals(key.getVipName())) {
                messageService.send(player, "vip_already_active", Map.of("vip", vip.getGroup()));
                return true;
            }
        }
        try {
            activationService.activateVip(player, key.getVipName(), String.valueOf(key.getDurationInDays()), key.isPermanent());
        } catch (Exception e) {
            messageService.sendRaw(player, "&cErro ao ativar VIP: " + e.getMessage());
            return true;
        }
        key.setUsedBy(player.getName());
        key.setActive(false);
        vipRepository.updateVipKey(key);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("usarchave")) {
            return null;
        }
        if (args.length == 1) {
            return List.of("vip", "cash").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
