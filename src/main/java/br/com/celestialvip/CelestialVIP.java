package br.com.celestialvip;

import br.com.celestialvip.data.DatabaseManager;
import br.com.celestialvip.services.PlayerService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


public final class CelestialVIP extends JavaPlugin implements CommandExecutor {

    DatabaseManager databaseManager = new DatabaseManager(getConfig());
    PlayerService playerService = new PlayerService(databaseManager.getDataSource());

    @Override
    public void onEnable() {
        getLogger().info("\033[92mBy: gabezk | Obrigado por usar!\033[0m");
        saveDefaultConfig(); // cria o arquivo de configuração padrão se ele não existir
        getCommand("celestialvip").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName()
                .equalsIgnoreCase("celestialvip") && args
                .length == 1 && args[0]
                .equalsIgnoreCase("reload")) {
            try {
                saveDefaultConfig();
                reloadConfig();
                databaseManager.reload(getConfig());
                playerService.reload(databaseManager.getDataSource());
                System.gc();
            }catch (Exception e){
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