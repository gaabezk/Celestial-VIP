package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.config.MessageService;
import br.com.celestialvip.config.PluginConfig;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.services.DeactivationService;
import br.com.celestialvip.models.entities.Vip;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class CelestialVipCommand implements CommandExecutor, TabCompleter {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private final CelestialVIP plugin;
    private final PluginConfig pluginConfig;
    private final MessageService messageService;
    private final VipRepository vipRepository;
    private final DeactivationService deactivationService;

    public CelestialVipCommand(CelestialVIP plugin,
                               PluginConfig pluginConfig,
                               MessageService messageService,
                               VipRepository vipRepository,
                               DeactivationService deactivationService) {
        this.plugin = plugin;
        this.pluginConfig = pluginConfig;
        this.messageService = messageService;
        this.vipRepository = vipRepository;
        this.deactivationService = deactivationService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("celestialvip")) {
            return false;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        return switch (sub) {
            case "reload" -> executeReload(sender);
            case "check" -> executeCheck(sender);
            case "renew" -> executeRenew(sender, args);
            case "listvips" -> executeListVips(sender);
            default -> {
                sendHelp(sender);
                yield true;
            }
        };
    }

    private boolean executeReload(CommandSender sender) {
        if (!sender.hasPermission("celestialvip.adm")) {
            messageService.send(sender, "no_permission");
            return true;
        }
        plugin.reloadPlugin(sender);
        return true;
    }

    private boolean executeCheck(CommandSender sender) {
        if (!sender.hasPermission("celestialvip.adm")) {
            messageService.send(sender, "no_permission");
            return true;
        }
        deactivationService.run();
        messageService.send(sender, "command_check_success");
        return true;
    }

    private boolean executeRenew(CommandSender sender, String[] args) {
        if (!sender.hasPermission("celestialvip.adm")) {
            messageService.send(sender, "no_permission");
            return true;
        }
        if (args.length != 4) {
            messageService.send(sender, "command_usage_renew");
            return true;
        }

        String playerName = args[1];
        String group = args[2];
        String daysStr = args[3];
        int days;
        try {
            days = Integer.parseInt(daysStr);
            if (days <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            messageService.send(sender, "no_valid_days");
            return true;
        }

        if (!pluginConfig.isVipRenewable(group)) {
            messageService.send(sender, "vip_not_renewable");
            return true;
        }

        List<Vip> vips = vipRepository.getAllVipsByPlayerNick(playerName, true);
        Vip vip = vips.stream().filter(v -> v.getGroup().equalsIgnoreCase(group)).findFirst().orElse(null);
        if (vip == null) {
            messageService.send(sender, "vip_not_found", java.util.Map.of("player", playerName));
            return true;
        }

        vip.extendDays(days, ZoneId.of(pluginConfig.getTimezone()));
        vipRepository.updateVip(vip);

        String expiration = vip.isPermanent() ? "Permanente" : (vip.getExpirationDate() != null ? vip.getExpirationDate().toString() : "N/A");
        messageService.send(sender, "vip_renew_success", java.util.Map.of("group", group, "days", String.valueOf(days), "expiration", expiration));
        return true;
    }

    private boolean executeListVips(CommandSender sender) {
        if (!sender.hasPermission("celestialvip.adm")) {
            messageService.send(sender, "no_permission");
            return true;
        }

        List<Vip> activeVips = new ArrayList<>(vipRepository.getAllVips(true, false));
        activeVips.addAll(vipRepository.getAllVips(true, true));
        if (activeVips.isEmpty()) {
            messageService.send(sender, "command_no_vips");
            return true;
        }

        messageService.send(sender, "vip_list_header");
        ZoneId zoneId = ZoneId.of(pluginConfig.getTimezone());
        for (Vip v : activeVips) {
            String expiration = v.isPermanent() ? "Permanente" : (v.getExpirationDate() != null ? v.getExpirationDate().toString() : "N/A");
            String line = messageService.getMessage("vip_list_entry")
                    .replace("{player}", v.getPlayerNick())
                    .replace("{group}", v.getGroup())
                    .replace("{expiration}", expiration)
                    .replace("{daysleft}", String.valueOf(v.daysLeft(zoneId)));
            sender.sendMessage(LEGACY.deserialize(line));
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        messageService.send(sender, "command_help_header");
        messageService.send(sender, "command_help_user_infovip");
        messageService.send(sender, "command_help_user_resgatar");
        messageService.send(sender, "command_help_user_usarchave");
        if (sender.hasPermission("celestialvip.adm")) {
            messageService.send(sender, "command_help_admin_title");
            messageService.send(sender, "command_help_admin_renew");
            messageService.send(sender, "command_help_admin_listvips");
            messageService.send(sender, "command_help_admin_reload");
            messageService.send(sender, "command_help_admin_check");
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("celestialvip")) {
            return null;
        }
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("reload");
            options.add("check");
            if (sender.hasPermission("celestialvip.adm")) {
                options.add("renew");
                options.add("listvips");
            }
            return options.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        if (!sender.hasPermission("celestialvip.adm")) {
            return List.of();
        }
        // /celestialvip renew <jogador> <grupo> <dias>
        if (args.length == 2 && args[0].equalsIgnoreCase("renew")) {
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(org.bukkit.entity.Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("renew")) {
            return pluginConfig.getVipGroupKeys().stream()
                    .filter(group -> group.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("renew")) {
            return List.of("7", "15", "30", "60", "90").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[3].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
