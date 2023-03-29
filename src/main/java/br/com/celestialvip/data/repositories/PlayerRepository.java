package br.com.celestialvip.data.repositories;

import br.com.celestialvip.models.entities.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.eclipse.aether.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerRepository {

    private static final Logger logger = LoggerFactory.getLogger(PlayerRepository.class);
    private final String prefix;
    private final DataSource dataSource;

    public PlayerRepository(DataSource dataSource, FileConfiguration config) {
        this.prefix = (String) config.get("config.database.tb_prefix");
        this.dataSource = dataSource;
    }

    public void savePlayerData(PlayerData playerData) throws RepositoryException {
        String sql = "INSERT INTO " + prefix + "player_data (nick, uuid) VALUES (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerData.getNick());
            statement.setString(2, playerData.getUuid());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to save player data", e);
            throw new RepositoryException("Failed to save player data", e);
        }
    }

    public PlayerData getPlayerDataByNick(String nick) throws RepositoryException {
        PlayerData playerData = null;
        String sql = "SELECT * FROM " + prefix + "player_data WHERE nick = ? LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nick);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String uuid = resultSet.getString("uuid");
                    playerData = new PlayerData(nick, uuid);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get player data by nick", e);
            throw new RepositoryException("Failed to get player data by nick", e);
        }
        return playerData;
    }

    public PlayerData getPlayerDataByUuid(String uuid) throws RepositoryException {
        PlayerData playerData = null;
        String sql = "SELECT * FROM " + prefix + "player_data WHERE uuid = ? LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String nick = resultSet.getString("nick");
                    playerData = new PlayerData(nick, uuid);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get player data by uuid", e);
            throw new RepositoryException("Failed to get player data by uuid", e);
        }
        return playerData;
    }
}
