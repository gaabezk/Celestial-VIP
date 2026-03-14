package br.com.celestialvip.commands;

import br.com.celestialvip.config.MessageService;
import br.com.celestialvip.data.repositories.CashRepository;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.models.keys.CashKey;
import br.com.celestialvip.models.keys.VipKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class ListKeysCommand implements CommandExecutor, TabCompleter {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private final MessageService messageService;
    private final VipRepository vipRepository;
    private final CashRepository cashRepository;

    public ListKeysCommand(MessageService messageService, VipRepository vipRepository, CashRepository cashRepository) {
        this.messageService = messageService;
        this.vipRepository = vipRepository;
        this.cashRepository = cashRepository;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("listarchaves")) {
            return false;
        }
        if (args.length == 0) {
            messageService.send(sender, "usage_list_keys");
            return true;
        }
        if (args.length != 1) {
            return false;
        }
        if (args[0].equalsIgnoreCase("vip")) {
            return vip(sender);
        }
        if (args[0].equalsIgnoreCase("cash")) {
            return cash(sender);
        }
        return false;
    }

    private boolean cash(CommandSender sender) {
        List<CashKey> cashKeys = cashRepository.getAllCashKeys(true);
        if (cashKeys.isEmpty()) {
            messageService.send(sender, "list_keys_empty");
            return true;
        }
        sender.sendMessage(LEGACY.deserialize("\n" + messageService.getMessage("list_keys_header")));
        for (CashKey cashKey : cashKeys) {
            Component component = Component.text(cashKey.getKeyCode())
                    .hoverEvent(HoverEvent.showText(LEGACY.deserialize(messageService.getMessage("copy_key", Map.of("key", cashKey.getKeyCode())))))
                    .clickEvent(ClickEvent.copyToClipboard(cashKey.getKeyCode()));
            sender.sendMessage(component);
        }
        return true;
    }

    private boolean vip(CommandSender sender) {
        List<VipKey> vipKeys = vipRepository.getAllVipKeys(true);
        if (vipKeys.isEmpty()) {
            messageService.send(sender, "list_keys_empty");
            return true;
        }
        sender.sendMessage(LEGACY.deserialize("\n" + messageService.getMessage("list_keys_header")));
        for (VipKey vipKey : vipKeys) {
            Component component = Component.text(vipKey.getKeyCode())
                    .hoverEvent(HoverEvent.showText(LEGACY.deserialize(messageService.getMessage("copy_key", Map.of("key", vipKey.getKeyCode())))))
                    .clickEvent(ClickEvent.copyToClipboard(vipKey.getKeyCode()));
            sender.sendMessage(component);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("listarchaves")) {
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
