package br.com.celestialvip.commands;

import br.com.celestialvip.config.MessageService;
import br.com.celestialvip.config.PluginConfig;
import br.com.celestialvip.data.repositories.CashRepository;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.domain.exception.*;
import br.com.celestialvip.mercadopago.MercadoPagoAPI;
import br.com.celestialvip.models.entities.PaymentStatus;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.services.ActivationService;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RedeemCommand implements CommandExecutor, TabCompleter {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    private final MessageService messageService;
    private final VipRepository vipRepository;
    private final CashRepository cashRepository;
    private final ActivationService activationService;
    private final MercadoPagoAPI mercadoPagoAPI;
    private final PluginConfig pluginConfig;

    public RedeemCommand(MessageService messageService, VipRepository vipRepository, CashRepository cashRepository,
                         ActivationService activationService, MercadoPagoAPI mercadoPagoAPI, PluginConfig pluginConfig) {
        this.messageService = messageService;
        this.vipRepository = vipRepository;
        this.cashRepository = cashRepository;
        this.activationService = activationService;
        this.mercadoPagoAPI = mercadoPagoAPI;
        this.pluginConfig = pluginConfig;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("resgatar")) {
            return false;
        }
        if (args.length == 0) {
            messageService.send(sender, "usage_redeem");
            messageService.send(sender, "available_groups", Map.of("groups", String.join(", ", pluginConfig.getVipGroupKeys())));
            return true;
        }
        if (args.length != 2) {
            return false;
        }
        if (!(sender instanceof Player player)) {
            messageService.send(sender, "no_player_command");
            return true;
        }
        if (args[0].equalsIgnoreCase("cash")) {
            return cash(player, args);
        }
        if (args[0].equalsIgnoreCase("vip")) {
            return vip(player, args);
        }
        return false;
    }

    private boolean vip(Player player, String[] args) {
        String days = "0";
        boolean isPerm = true;

        if (vipRepository.getMercadoPagoVipKey(args[1]) != null) {
            messageService.send(player, KeyAlreadyUsedException.MESSAGE_KEY);
            return true;
        }

        PaymentStatus result;
        try {
            result = mercadoPagoAPI.getPaymentStatus(args[1]);
        } catch (IOException e) {
            messageService.send(player, "payment_not_found");
            return true;
        }

        if (result.getStatus() == null) {
            messageService.send(player, PaymentNotFoundException.MESSAGE_KEY);
            return true;
        }

        String[] partes = Arrays.stream(result.getExternalReference() != null ? result.getExternalReference().split("\\.") : new String[0])
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        if (partes.length != 2) {
            messageService.send(player, "no_vip_redeem");
            return true;
        }

        if (!"approved".equals(result.getStatus())) {
            messageService.send(player, PaymentNotApprovedException.MESSAGE_KEY);
            return true;
        }

        List<String> vipsGroups = List.copyOf(pluginConfig.getVipGroupKeys());
        if (!vipsGroups.contains(partes[0])) {
            messageService.send(player, "incorrect_payment_format");
            return true;
        }

        if (!partes[1].equalsIgnoreCase("perm")) {
            days = partes[1];
            isPerm = false;
        }

        List<Vip> vips = vipRepository.getAllVipsByPlayerNick(player.getName(), true);
        for (Vip vip : vips) {
            if (vip.getGroup().equals(partes[0])) {
                messageService.send(player, "vip_already_active", Map.of("vip", vip.getGroup()));
                return true;
            }
        }

        try {
            activationService.activateVip(player, partes[0], days, isPerm);
        } catch (Exception e) {
            messageService.sendRaw(player, "&cErro ao ativar VIP: " + e.getMessage());
            return true;
        }
        vipRepository.saveMercadoPagoVipKey(args[1], player.getName());
        return true;
    }

    private boolean cash(Player player, String[] args) {
        if (cashRepository.getMercadoPagoCashCode(args[1]) != null) {
            messageService.send(player, KeyAlreadyUsedException.MESSAGE_KEY);
            return true;
        }

        PaymentStatus result;
        try {
            result = mercadoPagoAPI.getPaymentStatus(args[1]);
        } catch (IOException e) {
            messageService.send(player, "payment_not_found");
            return true;
        }

        if (result.getStatus() == null) {
            messageService.send(player, PaymentNotFoundException.MESSAGE_KEY);
            return true;
        }

        String[] partes = Arrays.stream(result.getExternalReference() != null ? result.getExternalReference().split("\\.") : new String[0])
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        if (partes.length != 1) {
            messageService.send(player, "no_cash_redeem");
            return true;
        }

        if (!"approved".equals(result.getStatus())) {
            messageService.send(player, PaymentNotApprovedException.MESSAGE_KEY);
            return true;
        }

        try {
            activationService.activateCash(player, partes[0]);
        } catch (Exception e) {
            messageService.sendRaw(player, "&cErro ao ativar cash: " + e.getMessage());
            return true;
        }
        cashRepository.saveMercadoPagoCashCode(args[1], player.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("resgatar")) {
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
