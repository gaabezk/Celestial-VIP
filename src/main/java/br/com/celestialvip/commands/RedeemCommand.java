package br.com.celestialvip.commands;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.entities.PayamentStatus;
import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.utils.Utilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.eclipse.aether.RepositoryException;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RedeemCommand implements CommandExecutor {

    FileConfiguration config = CelestialVIP.getPlugin().getConfig();
    String prefix = Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.prefix"))) + " ";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("resgatar") && args.length == 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.no_player_command"))));
                return true;
            }
            Player player = (Player) sender;
            if(args[0].equalsIgnoreCase("cash")){
                return cash(player,args);
            }else if(args[0].equalsIgnoreCase("vip")){
                return vip(player,args);
            }
        }
        return false;
    }

    private boolean vip(Player player, String[] args) {

        String days = "0";
        boolean isPerm = true;

        List<Vip> vips = CelestialVIP.getVipRepository().getAllVipsByPlayerNick(player.getName(), true);

        String code = CelestialVIP.getVipRepository().getMercadoPagoVipKey(args[1]);

        if (code != null) {
            player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.key_already_used"))));
            return true;
        }

        PayamentStatus result = null;
        try {
            result = CelestialVIP.getMercadoPagoAPI().getPaymentStatus(args[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (result.getStatus() == null) {
            player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.payment_not_found"))));
            return true;
        }

        String[] partes = result.getExternalReference().split("\\.");

        partes = Arrays.stream(partes)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        if (partes.length != 2) {
            player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.no_vip_redeem"))));
            return true;
        }

        if (!result.getStatus().equals("approved")) {
            player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.payment_not_approved"))));
            return true;
        }

        List<String> vipsGroups = new ArrayList<>(Objects.requireNonNull(CelestialVIP.getPlugin().getConfig().getConfigurationSection("config.vips")).getKeys(false));
        if (!vipsGroups.contains(partes[0])) {
            player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.incorrect_payment_format"))));
            return true;
        }

        if(!partes[1].equalsIgnoreCase("perm")){
            days=partes[1];
            isPerm=false;
        }

        for(Vip vip : vips){
            if(vip.getGroup().equals(partes[0])){
                player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.vip_already_active")).replace("{vip",vip.getGroup())));
                return true;
            }
        }

        try {
            CelestialVIP.getActivationService().activateVip(player, partes[0], days, isPerm);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        CelestialVIP.getVipRepository().saveMercadoPagoVipKey(args[1], player.getName());

        return true;
    }

    private boolean cash(Player player, String[] args){

        String code = CelestialVIP.getCashRepository().getMercadoPagoCashCode(args[1]);

        if (code != null) {
            player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.key_already_used"))));
            return true;
        }

        PayamentStatus result = null;
        try {
            result = CelestialVIP.getMercadoPagoAPI().getPaymentStatus(args[1]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        if (result.getStatus() == null) {
            player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.payment_not_found"))));
            return true;
        }

        String[] partes = result.getExternalReference().split("\\.");

        partes = Arrays.stream(partes)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        if (partes.length != 1) {
            player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.no_cash_redeem"))));
            return true;
        }

        if (!result.getStatus().equals("approved")) {
            player.sendMessage(prefix+Utilities.translateColorCodes(Objects.requireNonNull(config.getString("config.messages.payment_not_approved"))));
            return true;
        }

        try {
            CelestialVIP.getActivationService().activateCash(player, partes[0]);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
        CelestialVIP.getCashRepository().saveMercadoPagoCashCode(args[1], player.getName());

        return true;
    }


}
