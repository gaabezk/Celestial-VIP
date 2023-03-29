package br.com.celestialvip.data.repositories;

import br.com.celestialvip.models.keys.CashKey;
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

public class CashKeyRepository {

    private static final Logger logger = LoggerFactory.getLogger(CashKeyRepository.class);

    private final DataSource dataSource;

    public CashKeyRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveCashKey(CashKey cashKey) {
        String sql = "INSERT INTO cash_key (key_code,used_by,amount_of_cash,is_active,creation_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, cashKey.getKeyCode());
            statement.setString(2, cashKey.getUsedBy());
            statement.setDouble(3, cashKey.getAmountOfCash());
            statement.setBoolean(4, cashKey.isActive());
            statement.setObject(5, Date.from(cashKey.getCreationDate().atStartOfDay(ZoneId.of("America/Sao_Paulo")).toInstant()));
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error while saving cash key to database", e);
        }
    }

    public List<CashKey> getAllCashKeys(boolean active) {
        List<CashKey> cashKeys = new ArrayList<>();
        String sql = "SELECT * FROM cash_key WHERE is_active = ?";
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

    public CashKey getCashKeyByKeyCode(String keyCode) {
        CashKey cashKey = null;
        String sql = "SELECT * FROM cash_key WHERE key_code = ? LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, keyCode);
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
                resultSet.getDouble("amount_of_cash"),
                resultSet.getBoolean("is_active"),
                resultSet.getDate("creation_date").toLocalDate()
        );
    }

}
