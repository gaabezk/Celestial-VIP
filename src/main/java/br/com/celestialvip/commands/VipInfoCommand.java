package br.com.celestialvip.commands;

import br.com.celestialvip.config.MessageService;
import br.com.celestialvip.config.PluginConfig;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.models.entities.Vip;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.util.List;

public class VipInfoCommand implements CommandExecutor, TabCompleter {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private final MessageService messageService;
    private final VipRepository vipRepository;
    private final PluginConfig pluginConfig;

    public VipInfoCommand(MessageService messageService, VipRepository vipRepository, PluginConfig pluginConfig) {
        this.messageService = messageService;
        this.vipRepository = vipRepository;
        this.pluginConfig = pluginConfig;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("infovip")) {
            return false;
        }
        if (args.length > 1) {
            messageService.send(sender, "usage_vip_info");
            return true;
        }
        String nick;
        if (args.length == 1) {
            nick = args[0];
        } else {
            if (!(sender instanceof Player player)) {
                messageService.send(sender, "no_player_command");
                return true;
            }
            nick = player.getName();
        }
        List<Vip> vips = vipRepository.getAllVipsByPlayerNick(nick, true);
        if (vips.isEmpty()) {
            messageService.send(sender, "vip_not_found", java.util.Map.of("player", nick));
            return true;
        }
        ZoneId zoneId = ZoneId.of(pluginConfig.getTimezone());
        sender.sendMessage(LEGACY.deserialize(messageService.getPrefix() + messageService.getMessage("info_vip_header")));
        for (Vip vip : vips) {
            sender.sendMessage(LEGACY.deserialize(vip.toString(zoneId)));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("infovip")) {
            return null;
        }
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
