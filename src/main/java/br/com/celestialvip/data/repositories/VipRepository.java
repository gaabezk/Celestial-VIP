package br.com.celestialvip.data.repositories;

import br.com.celestialvip.models.entities.Vip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VipRepository {

    private static final Logger logger = LoggerFactory.getLogger(VipRepository.class);

    private final DataSource dataSource;

    public VipRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveVip(Vip vip) {
        String sql = "INSERT INTO vip (player_nick, `group`, is_active, vip_days, creation_date, expiration_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vip.getPlayerNick());
            statement.setString(2, vip.getGroup());
            statement.setBoolean(3, vip.isActive());
            statement.setInt(4, vip.getVipDays());
            statement.setObject(5, Date.from(vip.getCreationDate().atStartOfDay(ZoneId.of("America/Sao_Paulo")).toInstant()));
            statement.setObject(6, Date.from(vip.getExpirationDate().atStartOfDay(ZoneId.of("America/Sao_Paulo")).toInstant()));
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while saving vip to database", e);
        }
    }

    public List<Vip> getAllVipsByPlayerNick(String playerNick, boolean active) {
        List<Vip> vips = new ArrayList<>();
        String sql = "SELECT * FROM vip WHERE player_nick = ? AND is_active = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerNick);
            statement.setBoolean(2, active);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    vips.add(returnVip(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while getting vips by player nick from database", e);
        }
        return vips;
    }

    public List<Vip> getAllVipsByGroup(String group, boolean active) {
        List<Vip> vips = new ArrayList<>();
        String sql = "SELECT * FROM vip WHERE `group` = ? AND is_active = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, group);
            statement.setBoolean(2, active);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    vips.add(returnVip(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while getting vips by group from database", e);
        }
        return vips;
    }

    public List<Vip> getAllVipsByDays(int days, boolean active) {
        List<Vip> vips = new ArrayList<>();
        String sql = "SELECT * FROM vip WHERE vip_days = ? AND is_active = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, days);
            statement.setBoolean(2, active);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    vips.add(returnVip(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while getting vips by days from database", e);
        }
        return vips;
    }

    private Vip returnVip(ResultSet resultSet) throws SQLException {
        return new Vip(
                resultSet.getInt("id"),
                resultSet.getString("player_nick"),
                resultSet.getString("group"),
                resultSet.getBoolean("is_active"),
                resultSet.getInt("vip_days"),
                resultSet.getDate("creation_date").toLocalDate(),
                resultSet.getDate("expiration_date").toLocalDate()
        );
    }
}