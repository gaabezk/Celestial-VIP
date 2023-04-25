package br.com.celestialvip;

import br.com.celestialvip.commands.*;
import br.com.celestialvip.data.DatabaseManager;
import br.com.celestialvip.data.repositories.CashRepository;
import br.com.celestialvip.data.repositories.PlayerRepository;
import br.com.celestialvip.data.repositories.VipRepository;
import br.com.celestialvip.mercadopago.MercadoPagoAPI;
import br.com.celestialvip.services.ActivationService;
import br.com.celestialvip.services.DeactivationService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Timer;


public final class CelestialVIP extends JavaPlugin implements CommandExecutor {

    private static CelestialVIP plugin ;
    private static DatabaseManager databaseManager;
    private static MercadoPagoAPI mercadoPagoAPI;
    private static ActivationService activationService;
    private static CashRepository cashRepository;
    private static PlayerRepository playerRepository;
    private static VipRepository vipRepository;
    Timer timer = new Timer();

    public CelestialVIP() {
        plugin = this;
        databaseManager = new DatabaseManager();
        mercadoPagoAPI = new MercadoPagoAPI();
        activationService = new ActivationService();
        cashRepository = new CashRepository();
        playerRepository = new PlayerRepository();
        vipRepository = new VipRepository();
    }

    @Override
    public void onEnable() {
        plugin = this;
        getLogger().info("\033[92mBy: gabezk | Obrigado por usar!\033[0m");
        saveDefaultConfig(); // cria o arquivo de configuração padrão se ele não existir
        getCommand("celestialvip").setExecutor(this);
        getCommand("gerarchave").setExecutor(new GenerateKeyCommand());
        getCommand("resgatar").setExecutor(new RedeemCommand());
        getCommand("usarchave").setExecutor(new UseKeyCommand());
        getCommand("listarchaves").setExecutor(new ListKeysCommand());
        getCommand("infovip").setExecutor(new VipInfoCommand());
        getCommand("apagarchave").setExecutor(new DeleteKeyCommand());
        checkVipExpiration();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName()
                .equalsIgnoreCase("celestialvip") && args
                .length == 1 && args[0]
                .equalsIgnoreCase("reload")) {
            try {
                databaseManager.getConnection().close();
                cancelTimer();
                saveDefaultConfig();
                reloadConfig();
                databaseManager = new DatabaseManager();
                mercadoPagoAPI = new MercadoPagoAPI();
                playerRepository = new PlayerRepository();
                vipRepository = new VipRepository();
                cashRepository = new CashRepository();
                activationService = new ActivationService();
                checkVipExpiration();
                System.gc();
            } catch (Exception e) {
                sender.sendMessage(e.getMessage());
            }
            sender.sendMessage("Configurações recarregadas com sucesso!");
            return true;
        }
        return false;
    }

    public void checkVipExpiration() {
        int vipExpirationCheckInterval = getConfig().getInt("config.vip-expiration-check-interval");
        timer.schedule(new DeactivationService(), 0, vipExpirationCheckInterval * 1000);
    }

    public void cancelTimer(){
        timer.cancel();
        timer.purge();
        timer = new Timer();
    }

    @Override
    public void onDisable() {
        try {
            databaseManager.getConnection().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        getLogger().info("\033[91mPlugin desativado!\033[0m");
    }

    public static CelestialVIP getPlugin() {
        return plugin;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static ActivationService getActivationService() {
        return activationService;
    }

    public static MercadoPagoAPI getMercadoPagoAPI() {
        return mercadoPagoAPI;
    }

    public static CashRepository getCashRepository() {
        return cashRepository;
    }

    public static PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public static VipRepository getVipRepository() {
        return vipRepository;
    }
}