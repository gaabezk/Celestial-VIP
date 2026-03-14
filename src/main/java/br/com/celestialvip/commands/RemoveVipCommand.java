package br.com.celestialvip.commands;

import br.com.celestialvip.config.MessageService;
import br.com.celestialvip.config.PluginConfig;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.services.DeactivationService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class RemoveVipCommand implements CommandExecutor, TabCompleter {

    private final MessageService messageService;
    private final VipRepository vipRepository;
    private final DeactivationService deactivationService;
    private final PluginConfig pluginConfig;

    public RemoveVipCommand(MessageService messageService, VipRepository vipRepository, DeactivationService deactivationService, PluginConfig pluginConfig) {
        this.messageService = messageService;
        this.vipRepository = vipRepository;
        this.deactivationService = deactivationService;
        this.pluginConfig = pluginConfig;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("removervip")) {
            return false;
        }
        if (args.length == 0) {
            messageService.send(sender, "usage_remove_vip");
            messageService.send(sender, "available_groups", Map.of("groups", String.join(", ", pluginConfig.getVipGroupKeys())));
            return true;
        }
        if (args.length != 2) {
            return false;
        }
        List<Vip> vips = vipRepository.getAllVipsByPlayerNick(args[0], true);
        if (vips.isEmpty()) {
            messageService.send(sender, "vip_not_found", Map.of("player", args[0]));
            return true;
        }
        for (Vip vip : vips) {
            if (vip.getGroup().equals(args[1])) {
                deactivationService.deactivateVip(vip);
                messageService.send(sender, "remove_vip_success");
                return true;
            }
        }
        messageService.send(sender, "vip_not_active_other", Map.of("player", args[0], "vip", args[1]));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("removervip")) {
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
        return List.of();
    }
}
