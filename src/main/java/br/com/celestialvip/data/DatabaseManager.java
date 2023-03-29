package br.com.celestialvip.data;

import br.com.celestialvip.models.entities.PlayerData;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.Objects;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

@Getter
@Setter
@ToString
public class DatabaseManager {
    private String type;
    private String host;
    private String port;
    private String database;
    private String user;
    private String password;
    private String tbPrefix;
    private HikariDataSource dataSource;

    public DatabaseManager(FileConfiguration config) {
        if (
                !((String) Objects.requireNonNull(config.get("config.database.type"))).equalsIgnoreCase("mysql") &&
                        !((String) Objects.requireNonNull(config.get("config.database.type"))).equalsIgnoreCase("postgresql") &&
                        !((String) Objects.requireNonNull(config.get("config.database.type"))).equalsIgnoreCase("BLANK_")) {
            getLogger().warning("Banco de dados não suportado!!");
        }

        this.type = (String) config.get("config.database.type");
        this.host = ((String) config.get("config.database.host"));
        this.port = (String) config.get("config.database.port");
        this.database = (String) config.get("config.database.database");
        this.user = (String) config.get("config.database.user");
        this.password = (String) config.get("config.database.password");
        this.tbPrefix = (String) config.get("config.database.tb_prefix");

        createDataSource(config);
        creteTables();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void reload(FileConfiguration config) {

        if (
                !((String) Objects.requireNonNull(config.get("config.database.type"))).equalsIgnoreCase("mysql") &&
                        !((String) Objects.requireNonNull(config.get("config.database.type"))).equalsIgnoreCase("postgresql") &&
                        !((String) Objects.requireNonNull(config.get("config.database.type"))).equalsIgnoreCase("BLANK_")) {
            getLogger().warning("Banco de dados não suportado!!");
        }

        this.type = (String) config.get("config.database.type");
        this.host = ((String) config.get("config.database.host"));
        this.port = (String) config.get("config.database.port");
        this.database = (String) config.get("config.database.database");
        this.user = (String) config.get("config.database.user");
        this.password = (String) config.get("config.database.password");
        this.tbPrefix = (String) config.get("config.database.tb_prefix");

        createDataSource(config);
        creteTables();
    }

    private void createDataSource(FileConfiguration config) {
        if (!Objects.equals(config.get("config.database.type"), "BLANK_")) {
            String jdbcUrl = "jdbc:" +
                    (((String) config.get("config.database.type")).equalsIgnoreCase("mysql") ? "mysql" : "postgresql") +
                    "://" +
                    config.get("config.database.host") +
                    ":" +
                    config.get("config.database.port") +
                    "/" +
                    config.get("config.database.database");
            HikariConfig hikariConfig = new HikariConfig();
            try {
                hikariConfig.setJdbcUrl(jdbcUrl);
                hikariConfig.setUsername((String) config.get("config.database.user"));
                hikariConfig.setPassword((String) config.get("config.database.password"));
                hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
                hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
                hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "Erro ao configurar as propriedades de conexão: " + e.getMessage());
            }
            try {
                this.dataSource = new HikariDataSource(hikariConfig);
            } catch (RuntimeException e) {
                getLogger().log(Level.SEVERE, "Erro ao conectar ao banco: " + e.getMessage());
            }
        }
    }

    public void creteTables() {
        if (dataSource != null) {
            try (Statement connection = getConnection().createStatement()) {
                String playerData = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "player_data (nick VARCHAR(60) PRIMARY KEY, uuid VARCHAR(100) NOT NULL)";
                String vip = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip (id SERIAL PRIMARY KEY, player_nick VARCHAR(60), `group` VARCHAR(60) NOT NULL, is_active BOOLEAN NOT NULL, vip_days INT NOT NULL, creation_date DATE NOT NULL, expiration_date DATE NOT NULL, FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                String vipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip_key (key_code VARCHAR(255) PRIMARY KEY, vip_name VARCHAR(255) NOT NULL, duration_in_days INT, is_active BOOLEAN NOT NULL, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by VARCHAR(60))";
                String cashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "cash_key (key_code VARCHAR(255) PRIMARY KEY, quantity DOUBLE NOT NULL, is_active BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by VARCHAR(60))";
                String mercadoPagoVipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_vip_codes (key_code VARCHAR(255) PRIMARY KEY, creation_date DATE NOT NULL)";
                connection.executeUpdate(playerData);
                connection.executeUpdate(vip);
                connection.executeUpdate(vipKey);
                connection.executeUpdate(cashKey);
                connection.executeUpdate(mercadoPagoVipKey);
            } catch (Exception e) {
                getLogger().warning("Erro ao criar tabela, verifique as configs na pasta plugins/CelestialVIP: " + e.getMessage());
            }
        }
    }

    public void savePlayerData(PlayerData playerData) throws SQLException {
        try (Connection connection = getConnection()) {
            synchronized (connection) {
                String sql = "INSERT INTO player_data (uuid, nick) VALUES (?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, playerData.getUuid());
                    statement.setString(2, playerData.getNick());
                    statement.executeUpdate();
                }
            }
        }
    }

    public PlayerData loadPlayerData(String nick) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM player_data WHERE nick = ? LIMIT 1")) {
            statement.setString(1, nick);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String uuid = resultSet.getString("uuid");
                    return new PlayerData(uuid, nick);
                } else {
                    return null;
                }
            }
        }
    }


}