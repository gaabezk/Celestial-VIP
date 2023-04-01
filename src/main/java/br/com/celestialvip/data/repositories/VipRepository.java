package br.com.celestialvip.data.repositories;

import br.com.celestialvip.models.entities.Vip;
import br.com.celestialvip.models.keys.VipKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VipRepository {

    private static final Logger logger = LoggerFactory.getLogger(VipRepository.class);
    private final DataSource dataSource;
    private final String prefix;
    private final FileConfiguration config;

    public VipRepository(DataSource dataSource, FileConfiguration config) {
        this.dataSource = dataSource;
        this.prefix = config.getString("config.database.tb_prefix");
        this.config = config;
    }

    public void saveVip(Vip vip) {
        String sql = "INSERT INTO " + prefix + "vip (player_nick, vip_group, is_active, vip_days, creation_date, expiration_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vip.getPlayerNick());
            statement.setString(2, vip.getGroup());
            statement.setBoolean(3, vip.isActive());
            statement.setInt(4, vip.getVipDays());
            statement.setDate(5, java.sql.Date.valueOf(vip.getCreationDate()));
            statement.setDate(6, java.sql.Date.valueOf(vip.getExpirationDate()));
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while saving vip to database", e);
        }
    }

    public void updateVip(Vip vip) {
        String sql = "UPDATE " + prefix + "vip SET player_nick=?, vip_group=?, is_active=?, vip_days=?, creation_date=?, expiration_date=? WHERE id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vip.getPlayerNick());
            statement.setString(2, vip.getGroup());
            statement.setBoolean(3, vip.isActive());
            statement.setInt(4, vip.getVipDays());
            statement.setDate(5, java.sql.Date.valueOf(vip.getCreationDate()));
            statement.setDate(6, java.sql.Date.valueOf(vip.getExpirationDate()));
            statement.setInt(7, vip.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while updating vip in database", e);
        }
    }

    public List<Vip> getAllVips(boolean active) {
        List<Vip> vips = new ArrayList<>();
        String sql = "SELECT * FROM " + prefix + "vip WHERE is_active = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, active);
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

    public List<Vip> getAllVipsByPlayerNick(String playerNick, boolean active) {
        List<Vip> vips = new ArrayList<>();
        String sql = "SELECT * FROM " + prefix + "vip WHERE player_nick = ? AND is_active = ?";
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
        String sql = "SELECT * FROM " + prefix + "vip WHERE vip_group = ? AND is_active = ?";
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
        String sql = "SELECT * FROM " + prefix + "vip WHERE vip_days = ? AND is_active = ?";
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

    public void saveMercadoPagoVipKey(String mercadoPagoVipKey, String playerNick) {
        String sql = "INSERT INTO " + prefix + "mercado_pago_vip_codes (key_code,creation_date,player_nick) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, mercadoPagoVipKey);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            statement.setString(3, playerNick);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while saving Mercado Pago VipKey to database", e);
        }
    }

    public String getMercadoPagoVipKey(String keyCode) {
        String code = null;
        String sql = "SELECT * FROM " + prefix + "mercado_pago_vip_codes WHERE key_code = ? LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, keyCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    code = resultSet.getString("key_code");
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get vip key data by key code", e);
        }
        return code;
    }

    public void saveVipKey(VipKey vipKey) {
        String sql = "INSERT INTO " + prefix + "vip_key (key_code,used_by,vip_name,duration_in_days, is_active,is_permanent,creation_date) VALUES (?, ?, ?, ?, ?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vipKey.getKeyCode());
            statement.setString(2, vipKey.getUsedBy());
            statement.setString(3, vipKey.getVipName());

            if (vipKey.getDurationInDays() != null) {
                statement.setInt(4, vipKey.getDurationInDays());
            } else {
                statement.setNull(4, java.sql.Types.INTEGER);
            }
            statement.setBoolean(5, vipKey.isActive());
            statement.setBoolean(6, vipKey.isPermanent());
            statement.setObject(7, Date.from(vipKey.getCreationDate().atStartOfDay(ZoneId.of("America/Sao_Paulo")).toInstant()));
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while saving vip key to database", e);
        }
    }

    public List<VipKey> getAllVipKeys(boolean active) {
        List<VipKey> cashKeys = new ArrayList<>();
        String sql = "SELECT * FROM " + prefix + "vip_key WHERE is_active = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, active);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    cashKeys.add(returnVipKey(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while getting vip keys from database", e);
        }
        return cashKeys;
    }

    public VipKey getCashKeyByKeyCode(String keyCode) {
        VipKey vipKey = null;
        String sql = "SELECT * FROM " + prefix + "vip_key WHERE key_code = ? LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, keyCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    vipKey = returnVipKey(resultSet);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get vip key data by key code", e);
        }
        return vipKey;
    }

    private VipKey returnVipKey(ResultSet resultSet) throws SQLException {
        return new VipKey(
                resultSet.getString("key_code"),
                resultSet.getString("used_by"),
                resultSet.getString("vip_name"),
                resultSet.getInt("duration_in_days"),
                resultSet.getBoolean("is_active"),
                resultSet.getBoolean("is_permanent"),
                resultSet.getDate("creation_date").toLocalDate()
        );
    }

    private Vip returnVip(ResultSet resultSet) throws SQLException {
        return new Vip(
                resultSet.getInt("id"),
                resultSet.getString("player_nick"),
                resultSet.getString("vip_group"),
                resultSet.getBoolean("is_active"),
                resultSet.getInt("vip_days"),
                resultSet.getDate("creation_date").toLocalDate(),
                resultSet.getDate("expiration_date").toLocalDate()
        );
    }
}