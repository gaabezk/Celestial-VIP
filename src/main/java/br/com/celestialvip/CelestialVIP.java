package br.com.celestialvip;

import br.com.celestialvip.data.DatabaseManager;
import br.com.celestialvip.services.ActivationService;
import br.com.celestialvip.services.DeactivationService;
import br.com.celestialvip.services.VipKeyService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.aether.RepositoryException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


public final class CelestialVIP extends JavaPlugin implements CommandExecutor {
    DatabaseManager databaseManager = new DatabaseManager(getConfig(),getDataFolder());
    VipKeyService vipKeyService = new VipKeyService(databaseManager.getDataSource(), getConfig());
    ActivationService activationService = new ActivationService(databaseManager.getDataSource(), getConfig());
    List<String> vips = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("\033[92mBy: gabezk | Obrigado por usar!\033[0m");
        saveDefaultConfig(); // cria o arquivo de configuração padrão se ele não existir
        getCommand("celestialvip").setExecutor(this);
        getCommand("gerarchavevip").setExecutor(this);
        getCommand("resgatarvip").setExecutor(this);
        getCommand("resgatarcash").setExecutor(this);
        checkVipExpiration();
    }

    public void checkVipExpiration() {
        int vipExpirationCheckInterval = getConfig().getInt("config.vip-expiration-check-interval");
        Timer timer = new Timer();
        timer.schedule(new DeactivationService(databaseManager.getDataSource(),getConfig()), 0, vipExpirationCheckInterval * 1000);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        try{
            if (vipKeyService.gerarChaveVip(sender, command, label, args)) {
                return true;
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }


        try {
            if (activationService.redeemVip(sender, command, label, args)) {
                return true;
            }
        } catch (IOException | RepositoryException e) {
            throw new RuntimeException(e);
        }

        try{
            if (activationService.redeemCash(sender,command,label,args)){
                return true;
            }
        } catch (IOException | RepositoryException e) {
            throw new RuntimeException(e);
        }

        if (command.getName()
                .equalsIgnoreCase("celestialvip") && args
                .length == 1 && args[0]
                .equalsIgnoreCase("reload")) {
            try {
                saveDefaultConfig();
                reloadConfig();
                databaseManager = new DatabaseManager(getConfig(),getDataFolder());
                activationService = new ActivationService(databaseManager.getDataSource(), getConfig());
                vipKeyService = new VipKeyService(databaseManager.getDataSource(), getConfig());
                System.gc();
            } catch (Exception e) {
                sender.sendMessage(e.getMessage());
            }
            sender.sendMessage("Configurações recarregadas com sucesso!");
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        getLogger().info("\033[91mPlugin desativado!\033[0m");
    }
}