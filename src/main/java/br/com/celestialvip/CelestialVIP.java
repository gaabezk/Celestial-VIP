package br.com.celestialvip;

import br.com.celestialvip.data.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CelestialVIP extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("\033[92mBy: gabezk | Obrigado por usar!\033[0m");
        saveDefaultConfig(); // cria o arquivo de configuração padrão se ele não existir
        DatabaseManager databaseManager = new DatabaseManager(getConfig());


//        try {
//            databaseManager.savePlayerData(new PlayerData("teste-era-pra-ser-uuidfthfh","gabezkdd_"));
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            getLogger().info(databaseManager.loadPlayerData("gabezk_").toString());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }

    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin desativado!");
    }
}
