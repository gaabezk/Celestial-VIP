package br.com.celestialvip;

import br.com.celestialvip.data.DatabaseManager;
import br.com.celestialvip.services.ActivationService;
import br.com.celestialvip.services.VipKeyService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.aether.RepositoryException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class CelestialVIP extends JavaPlugin implements CommandExecutor {
    DatabaseManager databaseManager = new DatabaseManager(getConfig());
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
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (vipKeyService.gerarChaveVip(sender, command, label, args)) {
            return true;
        }
        try {
            if (activationService.resgatarVip(sender, command, label, args)) {
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }

        if (command.getName()
                .equalsIgnoreCase("celestialvip") && args
                .length == 1 && args[0]
                .equalsIgnoreCase("reload")) {
            try {
                saveDefaultConfig();
                reloadConfig();
                databaseManager.reload(getConfig());
                vipKeyService = null;
                System.gc();
                activationService = new ActivationService(databaseManager.getDataSource(), getConfig());
                vipKeyService = new VipKeyService(databaseManager.getDataSource(), getConfig());
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