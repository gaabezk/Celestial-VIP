package br.com.celestialvip.commands;

import br.com.celestialvip.config.MessageService;
import br.com.celestialvip.config.PluginConfig;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.services.ActivationService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class GiveVipCommand implements CommandExecutor, TabCompleter {

    private final MessageService messageService;
    private final VipRepository vipRepository;
    private final ActivationService activationService;
    private final PluginConfig pluginConfig;

    public GiveVipCommand(MessageService messageService, VipRepository vipRepository, ActivationService activationService,
                          PluginConfig pluginConfig) {
        this.messageService = messageService;
        this.vipRepository = vipRepository;
        this.activationService = activationService;
        this.pluginConfig = pluginConfig;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("darvip")) {
            return false;
        }
        if (args.length == 0) {
            messageService.send(sender, "usage_give_vip");
            messageService.send(sender, "available_groups", Map.of("groups", String.join(", ", pluginConfig.getVipGroupKeys())));
            return true;
        }
        if (args.length != 3) {
            return false;
        }
        if (!pluginConfig.getVipGroupKeys().contains(args[1])) {
            messageService.send(sender, "vip_group_not_found", Map.of("vip", args[1]));
            return true;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (!offlinePlayer.isOnline()) {
            messageService.send(sender, "player_not_online", Map.of("player", offlinePlayer.getName() != null ? offlinePlayer.getName() : args[0]));
            return true;
        }
        int days = 0;
        boolean isPerm = args[2].equalsIgnoreCase("perm");
        if (!isPerm) {
            try {
                days = Integer.parseInt(args[2]);
                if (days <= 0) {
                    messageService.send(sender, "no_valid_days");
                    return true;
                }
            } catch (NumberFormatException e) {
                messageService.send(sender, "no_valid_days");
                return true;
            }
        }
        String group = args[1];
        String playerName = offlinePlayer.getName();
        if (playerName == null) playerName = args[0];
        List<Vip> vips = vipRepository.getAllVipsByPlayerNick(playerName, true);
        for (Vip vip : vips) {
            if (vip.getGroup().equals(group)) {
                messageService.send(sender, "vip_already_active_other", Map.of("player", playerName, "vip", vip.getGroup()));
                return true;
            }
        }
        try {
            activationService.activateVip((Player) offlinePlayer, group, String.valueOf(days), isPerm);
            messageService.send(sender, "give_vip_success");
        } catch (Exception e) {
            messageService.sendRaw(sender, "&cErro ao dar VIP: " + e.getMessage());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("darvip")) {
            return null;
        }
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        if (args.length == 2) {
            return pluginConfig.getVipGroupKeys().stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        if (args.length == 3) {
            return List.of("perm", "7", "15", "30", "60", "90").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
