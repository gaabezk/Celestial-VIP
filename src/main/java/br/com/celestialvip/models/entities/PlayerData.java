package br.com.celestialvip.models.entities;

import br.com.celestialvip.data.DatabaseManager;
import lombok.*;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.SQLException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlayerData {
    private String uuid;
    private String nick;
    public void save(FileConfiguration config) throws SQLException {
        DatabaseManager databaseManager = new DatabaseManager(config);
        databaseManager.savePlayerData(this);
    }

    public static PlayerData load(String playerName, FileConfiguration config) throws SQLException {
        DatabaseManager databaseManager = new DatabaseManager(config);
        return databaseManager.loadPlayerData(playerName);
    }
}
