package br.com.celestialvip.data.repositories;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.entities.PlayerData;
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
    private final DataSource dataSource = CelestialVIP.getDatabaseManager().getDataSource();
    private final String prefix = CelestialVIP.getPlugin().getConfig().getString("config.database.tb_prefix");


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
