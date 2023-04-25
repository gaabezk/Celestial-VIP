package br.com.celestialvip.data.repositories;

import br.com.celestialvip.CelestialVIP;
import br.com.celestialvip.models.keys.CashKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CashRepository {

    private static final Logger logger = LoggerFactory.getLogger(VipRepository.class);
    private final DataSource dataSource = CelestialVIP.getDatabaseManager().getDataSource();
    private final String prefix = CelestialVIP.getPlugin().getConfig().getString("config.database.tb_prefix");


    public void saveMercadoPagoCashCode(String mercadoPagoCashKey, String playerNick) {
        String sql = "INSERT INTO " + prefix + "mercado_pago_cash_codes (key_code,creation_date,player_nick) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, mercadoPagoCashKey);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            statement.setString(3, playerNick);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while saving Mercado Pago CashKey to database", e);
        }
    }

    public String getMercadoPagoCashCode(String keyCode) {
        String code = null;
        String sql = "SELECT * FROM " + prefix + "mercado_pago_cash_codes WHERE key_code = ? LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, keyCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    code = resultSet.getString("key_code");
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get cash key data by key code", e);
        }
        return code;
    }

    public void saveCashKey(CashKey cashKey) {
        String sql = "INSERT INTO " + prefix + "cash_key (key_code,used_by,amount_of_cash,is_active,creation_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cashKey.getKeyCode());
            statement.setString(2, cashKey.getUsedBy());
            statement.setInt(3, cashKey.getAmountOfCash());
            statement.setBoolean(4, cashKey.isActive());
            statement.setObject(5, Date.from(cashKey.getCreationDate().atStartOfDay(ZoneId.of("America/Sao_Paulo")).toInstant()));
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while saving cash key to database", e);
        }
    }

    public void updateCashKey(CashKey cashKey) {
        String sql = "UPDATE " + prefix + "cash_key SET used_by=?, is_active=?, amount_of_cash=?, creation_date=? WHERE key_code=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cashKey.getUsedBy());
            statement.setBoolean(2, cashKey.isActive());
            statement.setInt(3, cashKey.getAmountOfCash());
            statement.setDate(4, java.sql.Date.valueOf(cashKey.getCreationDate()));
            statement.setString(5, cashKey.getKeyCode());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while updating cash key in database", e);
        }
    }

    public void deleteCashKey(String key) {
        String sql = "DELETE FROM " + prefix + "cash_key WHERE key_code = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, key);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while deleting cash key in database", e);
        }
    }

    public void deleteAllCashKeys(Boolean isActive) {
        String sql = "DELETE FROM " + prefix + "cash_key WHERE is_active = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, isActive);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while deleting all cash keys in database", e);
        }
    }

    public List<CashKey> getAllCashKeys(boolean active) {
        List<CashKey> cashKeys = new ArrayList<>();
        String sql = "SELECT * FROM " + prefix + "cash_key WHERE is_active = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, active);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    cashKeys.add(returnCashKey(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error while getting cash keys from database", e);
        }
        return cashKeys;
    }

    public CashKey getCashKeyByKeyCode(String keyCode,Boolean active) {
        CashKey cashKey = null;
        String sql = "SELECT * FROM " + prefix + "cash_key WHERE key_code = ? AND is_active = ? LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, keyCode);
            statement.setBoolean(2, active);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    cashKey = returnCashKey(resultSet);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to get player data by nick", e);
        }
        return cashKey;
    }

    private CashKey returnCashKey(ResultSet resultSet) throws SQLException {
        return new CashKey(
                resultSet.getString("key_code"),
                resultSet.getString("used_by"),
                resultSet.getInt("amount_of_cash"),
                resultSet.getBoolean("is_active"),
                resultSet.getDate("creation_date").toLocalDate()
        );
    }
}
