package br.com.celestialvip.data.repositories;

import br.com.celestialvip.data.DatabaseManager;
import br.com.celestialvip.models.keys.VipKey;
import org.bukkit.configuration.file.FileConfiguration;
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

public class VipKeyRepository {

    private static final Logger logger = LoggerFactory.getLogger(VipKeyRepository.class);

    private final DataSource dataSource;
    private final FileConfiguration config;
    private final String prefix;

    public VipKeyRepository(DataSource dataSource, FileConfiguration config) {
        this.dataSource = dataSource;
        this.config = config;
        this.prefix = (String) config.get("config.database.tb_prefix");
    }

    public void saveVipKey(VipKey vipKey){
        String sql = "INSERT INTO "+prefix+"vip_key (key_code,used_by,vip_name,duration_in_days, is_active,is_permanent,creation_date) VALUES (?, ?, ?, ?, ?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vipKey.getKeyCode());
            statement.setString(2, vipKey.getUsedBy());
            statement.setString(3, vipKey.getVipName());

            if(vipKey.getDurationInDays()!=null) {
                statement.setInt(4, vipKey.getDurationInDays());
            }else{
                statement.setNull(4, java.sql.Types.INTEGER);
            }
            statement.setBoolean(5, vipKey.isActive());
            statement.setBoolean(6, vipKey.isPermanent());
            statement.setObject(7, Date.from(vipKey.getCreationDate().atStartOfDay(ZoneId.of("America/Sao_Paulo")).toInstant()));
            statement.executeUpdate();
        } catch(SQLException e){
            logger.error("Error while saving vip key to database", e);
        }
    }

    public List<VipKey> getAllVipKeys(boolean active){
        List<VipKey> cashKeys = new ArrayList<>();
        String sql = "SELECT * FROM "+prefix+"vip_key WHERE is_active = ?";
        try(Connection connection = dataSource.getConnection();
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

    public VipKey getCashKeyByKeyCode(String keyCode){
        VipKey vipKey = null;
        String sql = "SELECT * FROM "+prefix+"vip_key WHERE key_code = ? LIMIT 1";
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

}
