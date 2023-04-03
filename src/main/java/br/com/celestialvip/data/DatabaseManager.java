package br.com.celestialvip.data;

import br.com.celestialvip.CelestialVIP;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.configuration.file.FileConfiguration;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

@Getter
@Setter
@ToString
public class DatabaseManager {
    private String drive;
    private String host;
    private String port;
    private String database;
    private String user;
    private String password;
    private String tbPrefix;
    private HikariDataSource dataSource;
    private File dataFolder = CelestialVIP.getPlugin().getDataFolder();
    private FileConfiguration config = CelestialVIP.getPlugin().getConfig();

    public DatabaseManager() {

        this.drive = config.getString("config.database.drive");
        this.host = config.getString("config.database.host");
        this.port = config.getString("config.database.port");
        this.database = config.getString("config.database.database");
        this.user = config.getString("config.database.user");
        this.password = config.getString("config.database.password");
        this.tbPrefix = config.getString("config.database.tb_prefix");

        createDataSource();
        creteTables();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void createDataSource() {
        HikariConfig hikariConfig = new HikariConfig();

        try {
            switch (Objects.requireNonNull(drive).toLowerCase()) {
                case "mysql":
                    hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
                    hikariConfig.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s", drive, host, port, database));
                    break;
                case "postgresql":
                    hikariConfig.setDriverClassName("org.postgresql.Driver");
                    hikariConfig.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s", drive, host, port, database));
                    break;
                default:
                    if (!dataFolder.exists()) {
                        dataFolder.mkdirs();
                    }
                    Path filePath = dataFolder.toPath().resolve("sqlite.db");
                    if (!Files.exists(filePath)) {
                        Files.createFile(filePath);
                    }
                    hikariConfig.setDriverClassName("org.sqlite.JDBC");
                    String url = "jdbc:sqlite:" + filePath.toAbsolutePath();
                    hikariConfig.setJdbcUrl(url);
                    break;
            }

            hikariConfig.setUsername(user);
            hikariConfig.setPassword(password);
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

    public void creteTables() {
        if (dataSource != null) {
            try (Statement connection = getConnection().createStatement()) {

                String playerData;
                String vip;
                String vipKey;
                String cashKey;
                String mercadoPagoVipKey;
                String mercadoPagoCashKey;

                switch (Objects.requireNonNull(drive).toLowerCase()) {

                    case "mysql":
                        playerData = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "player_data (nick VARCHAR(60) PRIMARY KEY, uuid VARCHAR(100) NOT NULL)";
                        vip = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip (id INT AUTO_INCREMENT PRIMARY KEY, player_nick VARCHAR(60), vip_group VARCHAR(60) NOT NULL, is_active BOOLEAN NOT NULL, vip_days INT, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, expiration_date DATE, FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                        vipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip_key (key_code VARCHAR(255) PRIMARY KEY, vip_name VARCHAR(255) NOT NULL, duration_in_days INT, is_active BOOLEAN NOT NULL, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by VARCHAR(60))";
                        cashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "cash_key (key_code VARCHAR(255) PRIMARY KEY, amount_of_cash INT NOT NULL, is_active BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by VARCHAR(60))";
                        mercadoPagoVipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_vip_codes (key_code VARCHAR(255) PRIMARY KEY, creation_date DATE NOT NULL, player_nick VARCHAR(60), FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                        mercadoPagoCashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_cash_codes (key_code VARCHAR(255) PRIMARY KEY, creation_date DATE NOT NULL, player_nick VARCHAR(60), FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                        break;
                    case "postgresql":
                        playerData = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "player_data (nick VARCHAR(60) PRIMARY KEY, uuid VARCHAR(100) NOT NULL)";
                        vip = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip (id SERIAL PRIMARY KEY, player_nick VARCHAR(60), vip_group VARCHAR(60) NOT NULL, is_active BOOLEAN NOT NULL, vip_days INT, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, expiration_date DATE, FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                        vipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip_key (key_code VARCHAR(255) PRIMARY KEY, vip_name VARCHAR(255) NOT NULL, duration_in_days INT, is_active BOOLEAN NOT NULL, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by VARCHAR(60))";
                        cashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "cash_key (key_code VARCHAR(255) PRIMARY KEY, amount_of_cash INT NOT NULL, is_active BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by VARCHAR(60))";
                        mercadoPagoVipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_vip_codes (key_code VARCHAR(255) PRIMARY KEY, creation_date DATE NOT NULL, player_nick VARCHAR(60), FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                        mercadoPagoCashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_cash_codes (key_code VARCHAR(255) PRIMARY KEY, creation_date DATE NOT NULL, player_nick VARCHAR(60), FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                        break;
                    default:
                        playerData = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "player_data (nick TEXT PRIMARY KEY, uuid TEXT NOT NULL)";
                        vip = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip (id INTEGER PRIMARY KEY AUTOINCREMENT, player_nick TEXT, vip_group TEXT NOT NULL, is_active BOOLEAN NOT NULL, vip_days INTEGER, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, expiration_date DATE, FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                        vipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "vip_key (key_code TEXT PRIMARY KEY, vip_name TEXT NOT NULL, duration_in_days INTEGER, is_active BOOLEAN NOT NULL, is_permanent BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by TEXT)";
                        cashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "cash_key (key_code TEXT PRIMARY KEY, amount_of_cash INTEGER NOT NULL, is_active BOOLEAN NOT NULL, creation_date DATE NOT NULL, used_by TEXT)";
                        mercadoPagoVipKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_vip_codes (key_code TEXT PRIMARY KEY, creation_date DATE NOT NULL, player_nick TEXT, FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                        mercadoPagoCashKey = "CREATE TABLE IF NOT EXISTS " + tbPrefix + "mercado_pago_cash_codes (key_code TEXT PRIMARY KEY, creation_date DATE NOT NULL, player_nick TEXT, FOREIGN KEY (player_nick) REFERENCES " + tbPrefix + "player_data(nick))";
                        break;
                }

                connection.executeUpdate(playerData);
                connection.executeUpdate(vip);
                connection.executeUpdate(vipKey);
                connection.executeUpdate(cashKey);
                connection.executeUpdate(mercadoPagoVipKey);
                connection.executeUpdate(mercadoPagoCashKey);
            } catch (Exception e) {
                getLogger().warning("Erro ao criar tabela, verifique as configs na pasta plugins/CelestialVIP: " + e.getMessage());
            }
        }
    }
}