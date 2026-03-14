package br.com.celestialvip.commands;

import br.com.celestialvip.config.MessageService;
import br.com.celestialvip.config.PluginConfig;
import br.com.celestialvip.data.repositories.CashRepository;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.models.keys.CashKey;
import br.com.celestialvip.models.keys.VipKey;
import br.com.celestialvip.utils.Utilities;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class GenerateKeyCommand implements CommandExecutor, TabCompleter {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private final MessageService messageService;
    private final VipRepository vipRepository;
    private final CashRepository cashRepository;
    private final PluginConfig pluginConfig;

    public GenerateKeyCommand(MessageService messageService, VipRepository vipRepository, CashRepository cashRepository,
                              PluginConfig pluginConfig) {
        this.messageService = messageService;
        this.vipRepository = vipRepository;
        this.cashRepository = cashRepository;
        this.pluginConfig = pluginConfig;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("gerarchave")) {
            return false;
        }
        if (args.length == 0) {
            messageService.send(sender, "usage_generate_key");
            messageService.send(sender, "available_groups", Map.of("groups", String.join(", ", pluginConfig.getVipGroupKeys())));
            return true;
        }
        if (args.length < 2) {
            return false;
        }
        if (args[0].equalsIgnoreCase("vip") && args.length == 3) {
            return vip(sender, args);
        }
        if (args[0].equalsIgnoreCase("cash") && args.length == 2) {
            return cash(sender, args);
        }
        return false;
    }

    private boolean cash(CommandSender sender, String[] args) {
        try {
            int amount = Integer.parseInt(args[1]);
            CashKey cashKey = new CashKey(
                    Utilities.generateSecureRandomString(pluginConfig.getKeySize()),
                    null,
                    amount,
                    true,
                    LocalDate.now()
            );
            cashRepository.saveCashKey(cashKey);
            String copyMsg = messageService.getMessage("copy_key", Map.of("key", cashKey.getKeyCode()));
            Component component = Component.text(cashKey.getKeyCode())
                    .hoverEvent(HoverEvent.showText(LEGACY.deserialize(copyMsg)))
                    .clickEvent(ClickEvent.copyToClipboard(cashKey.getKeyCode()));
            sender.sendMessage(LEGACY.deserialize("\n" + messageService.formatWithPrefix("generate_key_success")));
            sender.sendMessage(component);
        } catch (Exception e) {
            messageService.send(sender, "generate_key_error", Map.of("error_message", e.getMessage() != null ? e.getMessage() : ""));
        }
        return true;
    }

    private boolean vip(CommandSender sender, String[] args) {
        List<String> vipsGroups = List.copyOf(pluginConfig.getVipGroupKeys());
        if (!vipsGroups.contains(args[1])) {
            messageService.send(sender, "vip_group_not_found", Map.of("vip", args[1]));
            return true;
        }
        boolean isPerm = args[2].equalsIgnoreCase("perm");
        int days = 0;
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
        try {
            VipKey vipKey = new VipKey(
                    Utilities.generateSecureRandomString(pluginConfig.getKeySize()),
                    null,
                    args[1],
                    isPerm ? 0 : days,
                    true,
                    isPerm,
                    LocalDate.now()
            );
            vipRepository.saveVipKey(vipKey);
            String copyMsg = messageService.getMessage("copy_key", Map.of("key", vipKey.getKeyCode()));
            Component component = Component.text(vipKey.getKeyCode())
                    .hoverEvent(HoverEvent.showText(LEGACY.deserialize(copyMsg)))
                    .clickEvent(ClickEvent.copyToClipboard(vipKey.getKeyCode()));
            sender.sendMessage(LEGACY.deserialize("\n" + messageService.formatWithPrefix("generate_key_success")));
            sender.sendMessage(component);
        } catch (Exception e) {
            messageService.send(sender, "generate_key_error", Map.of("error_message", e.getMessage() != null ? e.getMessage() : ""));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("gerarchave")) {
            return null;
        }
        if (args.length == 1) {
            return List.of("vip", "cash").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("vip")) {
            return pluginConfig.getVipGroupKeys().stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("vip")) {
            return List.of("perm", "7", "15", "30", "60", "90").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("cash")) {
            return List.of("100", "500", "1000", "5000", "10000").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        return List.of();
    }
}
